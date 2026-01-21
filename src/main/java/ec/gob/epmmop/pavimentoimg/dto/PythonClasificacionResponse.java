package ec.gob.epmmop.pavimentoimg.dto;

public class PythonClasificacionResponse {
    private String estado;      // BUENO / REGULAR / MALO
    private Double confianza;   // opcional
    private String dano;        // opcional (ej: "Baches", "Piel de cocodrilo")

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getConfianza() { return confianza; }
    public void setConfianza(Double confianza) { this.confianza = confianza; }

    public String getDano() { return dano; }
    public void setDano(String dano) { this.dano = dano; }
}
