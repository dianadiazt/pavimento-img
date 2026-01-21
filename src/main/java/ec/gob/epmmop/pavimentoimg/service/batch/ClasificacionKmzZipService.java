package ec.gob.epmmop.pavimentoimg.service.batch;

import ec.gob.epmmop.pavimentoimg.dto.PythonClasificacionResponse;
import ec.gob.epmmop.pavimentoimg.kml.KmlData;
import ec.gob.epmmop.pavimentoimg.kml.KmlParser;
import ec.gob.epmmop.pavimentoimg.kml.KmlWriter;
import ec.gob.epmmop.pavimentoimg.service.PavimentoIaService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
public class ClasificacionKmzZipService {

    private final PavimentoIaService iaService;

    public ClasificacionKmzZipService(PavimentoIaService iaService) {
        this.iaService = iaService;
    }

    public Path procesar(MultipartFile puntosKmz, MultipartFile imagenesZip) throws Exception {

        Path work = Files.createTempDirectory("pavimento_batch_");
        Path kmzIn = work.resolve("puntos.kmz");
        Path zipImgs = work.resolve("imagenes.zip");

        Files.copy(puntosKmz.getInputStream(), kmzIn, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(imagenesZip.getInputStream(), zipImgs, StandardCopyOption.REPLACE_EXISTING);

        // 1) Extraer KMZ
        Path kmzDir = work.resolve("kmz");
        Files.createDirectories(kmzDir);
        unzip(kmzIn, kmzDir);

        Path docKml = encontrarPrimerKml(kmzDir);

        // 2) Leer puntos (placemarks)
        KmlData data = KmlParser.leerPlacemark(docKml);

        // 3) Extraer ZIP imágenes
        Path imgsDir = work.resolve("imgs");
        Files.createDirectories(imgsDir);
        unzip(zipImgs, imgsDir);

        // 4) Index de imágenes por nombre base (sin extensión)
        Map<String, Path> imagenPorNombreBase = indexarImagenes(imgsDir);

        // 5) Clasificar por cada punto existente en el KMZ
        Map<String, String> estadoPorNombre = new HashMap<>();

        for (KmlData.Placemark p : data.getPlacemarks()) {

            String base = normalizarNombre(p.getName());
            Path img = imagenPorNombreBase.get(base);

            if (img == null) {
                estadoPorNombre.put(base, "SIN_IMAGEN");
                continue;
            }

            try {
                // ✅ Llamada al servicio IA (Python) desde el service central
                PythonClasificacionResponse py = iaService.clasificarArchivo(img);

                String estado = (py != null && py.getEstado() != null)
                        ? py.getEstado().toUpperCase()
                        : "SIN_IA";

                estadoPorNombre.put(base, estado);

            } catch (Exception e) {
                estadoPorNombre.put(base, "SIN_SERVICIO_IA");
            }
        }

        // 6) Escribir KML nuevo con estilos
        Path outKml = work.resolve("doc.kml");
        KmlWriter.escribir(outKml, data, estadoPorNombre);

        // 7) Empacar KMZ final
        Path outKmz = work.resolve("resultado_pavimento.kmz");
        zipSoloDocKml(outKml, outKmz);

        return outKmz;
    }

    // ----------------- helpers -----------------

    private static Path encontrarPrimerKml(Path dir) throws IOException {
        // normal doc.kml
        Path doc = dir.resolve("doc.kml");
        if (Files.exists(doc)) return doc;

        // si no, buscar el primer .kml
        try (var stream = Files.walk(dir)) {
            return stream
                    .filter(p -> p.toString().toLowerCase().endsWith(".kml"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró ningún archivo .kml dentro del KMZ"));
        }
    }

    private static Map<String, Path> indexarImagenes(Path imgsDir) throws IOException {
        Map<String, Path> map = new HashMap<>();
        try (var stream = Files.walk(imgsDir)) {
            stream.filter(p -> {
                        String s = p.toString().toLowerCase();
                        return s.endsWith(".jpg") || s.endsWith(".jpeg");
                    })
                    .forEach(p -> {
                        String base = normalizarNombre(p.getFileName().toString());
                        // si hay repetidos, se queda con el primero
                        map.putIfAbsent(base, p);
                    });
        }
        return map;
    }

    private static String normalizarNombre(String filename) {
        String f = filename.trim();
        int dot = f.lastIndexOf('.');
        if (dot > 0) f = f.substring(0, dot);
        return f.toLowerCase();
    }

    private static void unzip(Path zipFile, Path destDir) throws IOException {
        try (ZipFile zf = new ZipFile(zipFile.toFile())) {
            Enumeration<? extends ZipEntry> entries = zf.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                Path outPath = destDir.resolve(entry.getName()).normalize();
                if (!outPath.startsWith(destDir)) continue; // seguridad

                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                    continue;
                }

                Path parent = outPath.getParent();
                if (parent != null) Files.createDirectories(parent);

                try (InputStream in = zf.getInputStream(entry)) {
                    Files.copy(in, outPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }


    private static void zipSoloDocKml(Path docKml, Path outKmz) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outKmz))) {
            ZipEntry e = new ZipEntry("doc.kml");
            zos.putNextEntry(e);
            Files.copy(docKml, zos);
            zos.closeEntry();
        }
    }
}
