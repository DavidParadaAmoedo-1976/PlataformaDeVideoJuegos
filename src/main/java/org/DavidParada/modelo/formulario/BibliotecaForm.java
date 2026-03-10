package org.DavidParada.modelo.formulario;

import java.time.Instant;

public class BibliotecaForm {

    private  Long idUsuario;
    private  Long idJuego;
    private  Instant fechaAdquisicion;
    private  Double horasDeJuego;
    private  Instant ultimaFechaDeJuego;
    private  boolean estadoInstalacion;

    public BibliotecaForm() {
    }

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



