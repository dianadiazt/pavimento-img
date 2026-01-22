package ec.gob.epmmop.pavimentoimg.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PythonClasificacionResponse {

    private String estado;
    private String recomendacion;
    private String dano;

    @JsonAlias({"areaDanoPct", "area_dano_pct"})
    private Double areaDanoPct;

    private Double confianza;

    @JsonAlias({"porcentajePavimentoPct", "porcentaje_pavimento_pct"})
    private Double porcentajePavimentoPct;

    private Boolean intervenida;

    @JsonAlias({"snAporta", "sn_aporta"})
    private Boolean snAporta;

    private String nota;

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
