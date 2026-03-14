package org.davidparada.modelo.dto;

public class JuegosPopularesDto {

    private Integer posicion;
    private JuegoDto juego;
    private Double metricaPrincipal;

    public JuegosPopularesDto(Integer posicion,
                              JuegoDto juego,
                              Double metricaPrincipal) {
        this.posicion = posicion;
        this.juego = juego;
        this.metricaPrincipal = metricaPrincipal;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public JuegoDto getJuego() {
        return juego;
    }

    public Double getMetricaPrincipal() {
        return metricaPrincipal;
    }
}
