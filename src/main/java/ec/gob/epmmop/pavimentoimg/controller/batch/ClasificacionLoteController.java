package ec.gob.epmmop.pavimentoimg.controller.batch;

import ec.gob.epmmop.pavimentoimg.service.batch.ClasificacionKmzZipService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
public class ClasificacionLoteController {

    private final ClasificacionKmzZipService service;

    public ClasificacionLoteController(ClasificacionKmzZipService service) {
        this.service = service;
    }

    @PostMapping(value = "/clasificar-kmz-zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> clasificarKmzZip(
            @RequestPart("kmz") MultipartFile kmz,
            @RequestPart("zip_imagenes") MultipartFile zipImagenes
    ) throws Exception {

        Path outKmz = service.procesar(kmz, zipImagenes);

        Resource resource = new FileSystemResource(outKmz.toFile());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resultado_pavimento.kmz")
                .contentType(MediaType.parseMediaType("application/vnd.google-earth.kmz"))
                .body(resource);
    }
}
