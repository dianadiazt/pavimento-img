package ec.gob.epmmop.pavimentoimg.controller;

import ec.gob.epmmop.pavimentoimg.dto.ClasificacionResponse;
import ec.gob.epmmop.pavimentoimg.dto.PythonClasificacionResponse;
import ec.gob.epmmop.pavimentoimg.service.PavimentoIaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ClasificacionController {

    private final PavimentoIaService iaService;

    public ClasificacionController(PavimentoIaService iaService) {
        this.iaService = iaService;
    }

    @PostMapping(value = "/clasificar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClasificacionResponse clasificar(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "filename", required = false) String filename
    ) {

        String nombre = (filename != null && !filename.isBlank())
                ? filename
                : file.getOriginalFilename();

        PythonClasificacionResponse py = null;

        // ✅ Si Python está caído, NO debe botar 500
        try {
            py = iaService.clasificar(file);
        } catch (Exception ignored) {
            // Puedes loguear aquí si quieres
        }

        // ✅ Si no hay respuesta de Python:
        if (py == null || py.getEstado() == null || py.getEstado().isBlank()) {
            return new ClasificacionResponse(
                    nombre,
                    "SIN_SERVICIO_IA",
                    "Clasificación manual / reintentar más tarde"
            );
        }

        String estado = py.getEstado().toUpperCase();

        String recomendacion;
        if ("BUENO".equals(estado)) recomendacion = "Mantenimiento rutinario";
        else if ("REGULAR".equals(estado)) recomendacion = "Mantenimiento periódico";
        else if ("MALO".equals(estado)) recomendacion = "Rehabilitación / Reconstrucción";
        else recomendacion = "Evaluación técnica requerida";

        return new ClasificacionResponse(nombre, estado, recomendacion);
    }
}
