package ec.gob.epmmop.pavimentoimg.kml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class KmlWriter {

    public static void escribir(Path outKml, KmlData data, Map<String, String> estadoPorNombreBase) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
        sb.append("  <Document>\n");
        sb.append("    <name>Resultado Pavimento</name>\n");

        // Estilos (color KML = aabbggrr)
        // Verde: ff00ff00 | Amarillo: ff00ffff | Rojo: ff0000ff | Gris: ff7f7f7f
        sb.append(style("bueno",   "ff00ff00"));
        sb.append(style("regular", "ff00ffff"));
        sb.append(style("malo",    "ff0000ff"));
        sb.append(style("gris",    "ff7f7f7f"));

        for (KmlData.Placemark p : data.getPlacemarks()) {
            String base = normalizarNombre(p.getName());
            String estado = estadoPorNombreBase.getOrDefault(base, "SIN_IMAGEN");

            String styleUrl = "#gris";
            String recomendacion = "Sin imagen";

            if ("BUENO".equalsIgnoreCase(estado)) {
                styleUrl = "#bueno";
                recomendacion = "Mantenimiento rutinario";
            } else if ("REGULAR".equalsIgnoreCase(estado)) {
                styleUrl = "#regular";
                recomendacion = "Mantenimiento periódico";
            } else if ("MALO".equalsIgnoreCase(estado)) {
                styleUrl = "#malo";
                recomendacion = "Rehabilitación / Reconstrucción";
            } else if ("SIN_SERVICIO_IA".equalsIgnoreCase(estado)) {
                styleUrl = "#gris";
                recomendacion = "Clasificación manual / reintentar más tarde";
            } else if ("SIN_IA".equalsIgnoreCase(estado)) {
                styleUrl = "#gris";
                recomendacion = "Respuesta IA vacía";
            } else if ("SIN_IMAGEN".equalsIgnoreCase(estado)) {
                styleUrl = "#gris";
                recomendacion = "No existe imagen para este punto";
            }

            sb.append("    <Placemark>\n");
            sb.append("      <name>").append(escapeXml(p.getName())).append("</name>\n");
            sb.append("      <description>")
                    .append(escapeXml("Estado: " + estado + " | " + recomendacion))
                    .append("</description>\n");
            sb.append("      <styleUrl>").append(styleUrl).append("</styleUrl>\n");
            sb.append("      <Point>\n");
            sb.append("        <coordinates>")
                    .append(p.getLon()).append(",").append(p.getLat()).append(",0")
                    .append("</coordinates>\n");
            sb.append("      </Point>\n");
            sb.append("    </Placemark>\n");
        }

        sb.append("  </Document>\n");
        sb.append("</kml>\n");

        Files.writeString(outKml, sb.toString(), StandardCharsets.UTF_8);
    }

    private static String style(String id, String colorAabbggrr) {
        return "    <Style id=\"" + id + "\">\n" +
                "      <IconStyle>\n" +
                "        <color>" + colorAabbggrr + "</color>\n" +
                "        <scale>1.1</scale>\n" +
                "        <Icon>\n" +
                "          <href>http://maps.google.com/mapfiles/kml/paddle/wht-blank.png</href>\n" +
                "        </Icon>\n" +
                "      </IconStyle>\n" +
                "    </Style>\n";
    }

    private static String normalizarNombre(String filename) {
        String f = filename.trim();
        int dot = f.lastIndexOf('.');
        if (dot > 0) f = f.substring(0, dot);
        return f.toLowerCase();
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
