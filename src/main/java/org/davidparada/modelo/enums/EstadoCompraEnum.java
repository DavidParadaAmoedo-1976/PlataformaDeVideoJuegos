package org.davidparada.modelo.enums;

public enum EstadoCompraEnum {

    SALIR("Salir"),
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada"),
    REEMBOLSADA("Reembolsada");

    private final String descripcion;

    EstadoCompraEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

