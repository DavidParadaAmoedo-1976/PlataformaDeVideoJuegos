package org.davidparada.modelo.enums;

public enum EstadoJuegoEnum {

    SALIR("Salir"),
    NO_DISPONIBLE("No disponible"),
    DISPONIBLE("Disponible"),
    PREVENTA("Preventa"),
    ACCESO_ANTICIPADO("Acceso Anticipado");

    private final String descripcion;

    EstadoJuegoEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

