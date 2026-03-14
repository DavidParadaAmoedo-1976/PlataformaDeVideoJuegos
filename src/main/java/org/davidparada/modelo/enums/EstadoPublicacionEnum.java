package org.davidparada.modelo.enums;

public enum EstadoPublicacionEnum {

    SALIR("Salir"),
    PUBLICADA("Publicada"),
    OCULTA("Oculta"),
    ELIMINADA("Eliminada");

    private final String descripcion;

    EstadoPublicacionEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

