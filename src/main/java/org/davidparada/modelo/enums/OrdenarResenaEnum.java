package org.davidparada.modelo.enums;

public enum OrdenarResenaEnum {
    RECIENTES("Recientes"),
    UTILES("Utiles");
    private final String descripcion;

    OrdenarResenaEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }
}
