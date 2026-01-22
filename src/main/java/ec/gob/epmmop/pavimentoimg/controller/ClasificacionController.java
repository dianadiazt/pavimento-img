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

        PythonClasificacionResponse py;
        try {
            py = iaService.clasificar(file); // <-- 1 parámetro, OK
        } catch (Exception e) {
            return new ClasificacionResponse(
                    nombre,
                    "SIN_SERVICIO_IA",
                    "Clasificación manual / reintentar más tarde",
                    null,
                    null,
                    null,
                    null,   // porcentajePavimentoPct
                    null,   // intervenida
                    null,   // snAporta
                    e.getMessage()
            );
        }

        if (py == null) {
            return new ClasificacionResponse(
                    nombre,
                    "SIN_IA",
                    "Respuesta IA vacía",
                    null,
                    null,
                    null,
                    null,   // porcentajePavimentoPct
                    null,   // intervenida
                    null,   // snAporta
                    "Python devolvió null"
            );
        }

        // Traer campos Python
        String dano = py.getDano();
        Double areaPct = py.getAreaDanoPct();
        Double confianza = py.getConfianza();

        // ✅ NUEVO: porcentaje de pavimento en la foto
        Double porcentajePavimentoPct = py.getPorcentajePavimentoPct();

        Boolean intervenida = py.getIntervenida();
        Boolean snAporta = py.getSnAporta();
        String nota = py.getNota();

        // Si viene el % de daño => estado por manual
        if (areaPct != null) {
            String estadoCalc = calcularEstadoPorArea(areaPct);
            String recomendacion = recomendacionPorEstado(estadoCalc);

            return new ClasificacionResponse(
                    nombre,
                    estadoCalc,
                    recomendacion,
                    dano,
                    areaPct,
                    confianza,
                    porcentajePavimentoPct,
                    intervenida,
                    snAporta,
                    nota
            );
        }

        // Si NO viene área => usar estado del modelo Python (si existe)
        String estado = (py.getEstado() != null) ? py.getEstado().trim().toUpperCase() : "";
        if (estado.isBlank()) {
            return new ClasificacionResponse(
                    nombre,
                    "SIN_IA",
                    "Respuesta IA vacía",
                    dano,
                    null,
                    confianza,
                    porcentajePavimentoPct,
                    intervenida,
                    snAporta,
                    nota
            );
        }

        return new ClasificacionResponse(
                nombre,
                estado,
                recomendacionPorEstado(estado),
                dano,
                null,
                confianza,
                porcentajePavimentoPct,
                intervenida,
                snAporta,
                nota
        );
    }

    private static String calcularEstadoPorArea(double areaPct) {
        // 0-0.5% BUENO
        // 0.5-3% REGULAR
        // >3% MALO
        if (areaPct <= 0.5) return "BUENO";
        if (areaPct <= 3.0) return "REGULAR";
        return "MALO";
    }

    private static String recomendacionPorEstado(String estado) {
        if ("BUENO".equalsIgnoreCase(estado)) return "Mantenimiento rutinario";
        if ("REGULAR".equalsIgnoreCase(estado)) return "Mantenimiento periódico";
        if ("MALO".equalsIgnoreCase(estado)) return "Reconstrucción y Rehabilitación";
        return "Evaluación técnica requerida";
    }
}
