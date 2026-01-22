package ec.gob.epmmop.pavimentoimg.dto;

public class ClasificacionResponse {

    private String filename;
    private String estado;
    private String recomendacion;

    private String dano;
    private Double areaDanoPct;
    private Double confianza;

    // ✅ NUEVO
    private Double porcentajePavimentoPct;

    private Boolean intervenida;
    private Boolean snAporta;
    private String nota;

    public ClasificacionResponse() {}

    // ✅ CONSTRUCTOR ÚNICO Y OFICIAL (10 PARAMS)
    public ClasificacionResponse(
            String filename,
            String estado,
            String recomendacion,
            String dano,
            Double areaDanoPct,
            Double confianza,
            Double porcentajePavimentoPct,
            Boolean intervenida,
            Boolean snAporta,
            String nota
    ) {
        this.filename = filename;
        this.estado = estado;
        this.recomendacion = recomendacion;
        this.dano = dano;
        this.areaDanoPct = areaDanoPct;
        this.confianza = confianza;
        this.porcentajePavimentoPct = porcentajePavimentoPct;
        this.intervenida = intervenida;
        this.snAporta = snAporta;
        this.nota = nota;
    }

    // getters & setters
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) { this.recomendacion = recomendacion; }

    public String getDano() { return dano; }
    public void setDano(String dano) { this.dano = dano; }

    public Double getAreaDanoPct() { return areaDanoPct; }
    public void setAreaDanoPct(Double areaDanoPct) { this.areaDanoPct = areaDanoPct; }

    public Double getConfianza() { return confianza; }
    public void setConfianza(Double confianza) { this.confianza = confianza; }

    public Double getPorcentajePavimentoPct() { return porcentajePavimentoPct; }
    public void setPorcentajePavimentoPct(Double porcentajePavimentoPct) {
        this.porcentajePavimentoPct = porcentajePavimentoPct;
    }

    public Boolean getIntervenida() { return intervenida; }
    public void setIntervenida(Boolean intervenida) { this.intervenida = intervenida; }

    public Boolean getSnAporta() { return snAporta; }
    public void setSnAporta(Boolean snAporta) { this.snAporta = snAporta; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
}
