package org.davidparada.modelo.enums;

public enum PaisEnum {

    ESPANA("España"),
    FRANCIA("Francia"),
    ALEMANIA("Alemania"),
    ITALIA("Italia"),
    PORTUGAL("Portugal");

    private final String descripcion;

    PaisEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }
}

