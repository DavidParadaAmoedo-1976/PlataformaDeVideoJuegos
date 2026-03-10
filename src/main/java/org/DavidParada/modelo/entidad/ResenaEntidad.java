package org.DavidParada.modelo.entidad;

import org.DavidParada.modelo.enums.EstadoPublicacionEnum;

import java.time.Instant;

public class ResenaEntidad {

    private final Long idResena;
    private final Long idUsuario;
    private final Long idJuego;
    private final boolean recomendado;
    private final String textoResena;
    private final Double cantidadHorasJugadas;
    private final Instant fechaPublicacion;
    private final Instant fechaUltimaEdicion;
    private final EstadoPublicacionEnum estadoPublicacion;

    public ResenaEntidad(Long idResena,
                         Long idUsuario,
                         Long idJuego,
                         boolean recomendado,
                         String textoResena,
                         Double cantidadHorasJugadas,
                         Instant fechaPublicacion,
                         Instant fechaUltimaEdicion,
                         EstadoPublicacionEnum estadoPublicacion) {
        this.idResena = idResena;
        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.recomendado = recomendado;
        this.textoResena = textoResena;
        this.cantidadHorasJugadas = cantidadHorasJugadas;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaUltimaEdicion = fechaUltimaEdicion;
        this.estadoPublicacion = estadoPublicacion;
    }

    public Long getIdResena() {
        return idResena;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdJuego() {
        return idJuego;
    }

    public boolean isRecomendado() {
        return recomendado;
    }

    public String getTextoResena() {
        return textoResena;
    }

    public Double getCantidadHorasJugadas() {
        return cantidadHorasJugadas;
    }

    public Instant getFechaPublicacion() {
        return fechaPublicacion;
    }

    public Instant getFechaUltimaEdicion() {
        return fechaUltimaEdicion;
    }

    public EstadoPublicacionEnum getEstadoPublicacion() {
        return estadoPublicacion;
    }
}