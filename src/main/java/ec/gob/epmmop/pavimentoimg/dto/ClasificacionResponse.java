package ec.gob.epmmop.pavimentoimg.dto;

public class ClasificacionResponse {
    private String filename;
    private String estado;
    private String recomendacion;

    public ClasificacionResponse() {}

    public ClasificacionResponse(String filename, String estado, String recomendacion) {
        this.filename = filename;
        this.estado = estado;
        this.recomendacion = recomendacion;
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) { this.recomendacion = recomendacion; }
}
