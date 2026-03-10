package org.DavidParada.modelo.entidad;

import org.DavidParada.modelo.enums.ClasificacionJuegoEnum;
import org.DavidParada.modelo.enums.EstadoJuegoEnum;

import java.time.LocalDate;

public class JuegoEntidad {
    private Long idJuego;
    private String titulo;
    private String descripcion;
    private String desarrollador;
    private LocalDate fechaLanzamiento;
    private Double precioBase;
    private Integer descuento;
    private String categoria;
    private ClasificacionJuegoEnum clasificacionPorEdad;
    private String[] idiomas;
    private EstadoJuegoEnum estado;

    public JuegoEntidad(Long idJuego,
                        String titulo,
                        String descripcion,
                        String desarrollador,
                        LocalDate fechaLanzamiento,
                        Double precioBase,
                        Integer descuento,
                        String categoria,
                        ClasificacionJuegoEnum clasificacionPorEdad,
                        String[] idiomas,
                        EstadoJuegoEnum estado) {

        this.idJuego = idJuego;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.desarrollador = desarrollador;
        this.fechaLanzamiento = fechaLanzamiento;
        this.precioBase = precioBase;
        this.descuento = descuento;
        this.categoria = categoria;
        this.clasificacionPorEdad = clasificacionPorEdad;
        this.idiomas = idiomas;
        this.estado = estado;
    }

    public JuegoEntidad() {
    }

    public Long getIdJuego() {
        return idJuego;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDesarrollador() {
        return desarrollador;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public String getCategoria() {
        return categoria;
    }

    public ClasificacionJuegoEnum getClasificacionPorEdad() {
        return clasificacionPorEdad;
    }

    public String[] getIdiomas() {
        return idiomas;
    }

    public EstadoJuegoEnum getEstado() {
        return estado;
    }

}
