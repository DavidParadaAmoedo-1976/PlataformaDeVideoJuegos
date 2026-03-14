package org.davidparada.modelo.enums;

public enum EstadoCuentaEnum {

    SALIR("Salir"),
    ACTIVA("Activa"),
    SUSPENDIDA("Suspendida"),
    BANEADA("Baneada");


    private final String descripcion;

    EstadoCuentaEnum(String texto) {
        this.descripcion = texto;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

