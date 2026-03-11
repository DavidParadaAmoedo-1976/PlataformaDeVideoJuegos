package org.davidparada.modelo.dto;

import java.time.Instant;

public class ReporteVentasDto {

    private Instant fechaInicio;
    private Instant fechaFin;
    private Integer totalVentas;
    private Double ingresosTotales;

    public ReporteVentasDto(Instant fechaInicio, Instant fechaFin, Integer totalVentas, Double ingresosTotales) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalVentas = totalVentas;
        this.ingresosTotales = ingresosTotales;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }

    public Integer getTotalVentas() {
        return totalVentas;
    }

    public Double getIngresosTotales() {
        return ingresosTotales;
    }

}
