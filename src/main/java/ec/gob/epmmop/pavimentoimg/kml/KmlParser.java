package ec.gob.epmmop.pavimentoimg.kml;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;

public class KmlParser {

    public static KmlData leerPlacemark(Path kmlPath) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        Document doc = dbf.newDocumentBuilder().parse(kmlPath.toFile());
        doc.getDocumentElement().normalize();

        NodeList placemarks = doc.getElementsByTagNameNS("*", "Placemark");

        KmlData data = new KmlData();

        for (int i = 0; i < placemarks.getLength(); i++) {
            Node n = placemarks.item(i);
            if (!(n instanceof Element)) continue;
            Element pm = (Element) n;

            String name = textOfFirst(pm, "name");
            if (name == null || name.isBlank()) continue;

            // Buscar coordinates dentro del Placemark
            String coords = textOfFirst(pm, "coordinates");
            if (coords == null || coords.isBlank()) continue;

            // coordenadas: "lon,lat,alt" (a veces hay espacios o saltos)
            String cleaned = coords.trim().split("\\s+")[0]; // primer punto
            String[] parts = cleaned.split(",");
            if (parts.length < 2) continue;

            double lon = Double.parseDouble(parts[0]);
            double lat = Double.parseDouble(parts[1]);

            data.add(new KmlData.Placemark(name.trim(), lon, lat));
        }

        return data;
    }

    private static String textOfFirst(Element parent, String localName) {
        NodeList list = parent.getElementsByTagNameNS("*", localName);
        if (list.getLength() == 0) return null;
        Node n = list.item(0);
        return n != null ? n.getTextContent() : null;
    }
}
