package ec.gob.epmmop.pavimentoimg.service;

import ec.gob.epmmop.pavimentoimg.dto.PythonClasificacionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PavimentoIaService {

    private final RestTemplate restTemplate;

    @Value("${ia.pavimento.url}")
    private String iaUrl; // Ej: http://127.0.0.1:8000

    public PavimentoIaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Clasifica una imagen que llega como MultipartFile (ej. desde Postman)
     */
    public PythonClasificacionResponse clasificar(MultipartFile file) throws Exception {
        // Guardamos temporalmente y reutilizamos el método por Path
        Path temp = Files.createTempFile("pav_", "_" + safeName(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), temp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try {
            return clasificarArchivo(temp);
        } finally {
            try { Files.deleteIfExists(temp); } catch (Exception ignored) {}
        }
    }

    /**
     * ✅ ESTE ES EL MÉTODO QUE TE FALTA
     * Clasifica una imagen desde disco (Path). Se usa en el proceso KMZ+ZIP.
     */
    public PythonClasificacionResponse clasificarArchivo(Path imgPath) {
        FileSystemResource fileResource = new FileSystemResource(imgPath.toFile());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<PythonClasificacionResponse> resp = restTemplate.exchange(
                iaUrl + "/clasificar",
                HttpMethod.POST,
                requestEntity,
                PythonClasificacionResponse.class
        );

        return resp.getBody();
    }

    private static String safeName(String name) {
        if (name == null) return "imagen.jpg";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
