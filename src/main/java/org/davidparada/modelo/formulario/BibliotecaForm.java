package org.davidparada.modelo.formulario;

import java.time.Instant;

public class BibliotecaForm {

    private final Long idUsuario;
    private final Long idJuego;
    private final Instant fechaAdquisicion;
    private final Double horasDeJuego;
    private final Instant ultimaFechaDeJuego;
    private final boolean estadoInstalacion;

    public BibliotecaForm(Long idUsuario,
                          Long idJuego,
                          Instant fechaAdquisicion,
                          Double horasDeJuego,
                          Instant ultimaFechaDeJuego,
                          boolean estadoInstalacion) {

        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.fechaAdquisicion = fechaAdquisicion;
        this.horasDeJuego = horasDeJuego;
        this.ultimaFechaDeJuego = ultimaFechaDeJuego;
        this.estadoInstalacion = estadoInstalacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdJuego() {
        return idJuego;
    }

    public Instant getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public Double getHorasDeJuego() {
        return horasDeJuego;
    }

    public Instant getUltimaFechaDeJuego() {
        return ultimaFechaDeJuego;
    }

    public boolean isEstadoInstalacion() {
        return estadoInstalacion;
    }
}



