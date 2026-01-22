package ec.gob.epmmop.pavimentoimg.service;

import ec.gob.epmmop.pavimentoimg.dto.PythonClasificacionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class PavimentoIaService {

    private final RestTemplate restTemplate;

    @Value("${ia.pavimento.url}")
    private String iaUrl;

    @Value("${ia.pavimento.apikey}")
    private String apiKey;

    public PavimentoIaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PythonClasificacionResponse clasificar(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String filename = safeName(file.getOriginalFilename());
        return enviarAPython(bytes, filename);
    }

    public PythonClasificacionResponse clasificarArchivo(Path imgPath) {
        FileSystemResource fileResource = new FileSystemResource(imgPath.toFile());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // FastAPI: Header x-api-key
        headers.set("x-api-key", apiKey);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<PythonClasificacionResponse> resp = restTemplate.exchange(
                iaUrl + "/clasificar",
                HttpMethod.POST,
                requestEntity,
                PythonClasificacionResponse.class
        );

        return resp.getBody();
    }

    private PythonClasificacionResponse enviarAPython(byte[] bytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("x-api-key", apiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<PythonClasificacionResponse> resp = restTemplate.exchange(
                iaUrl + "/clasificar",
                HttpMethod.POST,
                request,
                PythonClasificacionResponse.class
        );

        return resp.getBody();
    }

    private static String safeName(String name) {
        if (name == null || name.isBlank()) return "imagen.jpg";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
