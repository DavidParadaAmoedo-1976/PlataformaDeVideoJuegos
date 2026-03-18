package org.davidparada.modelo.enums;

public enum OrdenarResenaEnum {
    RECIENTES("Recientes"),
    HORAS_JUGADAS("Mas Horas Jugadas"),
    ACTUALIZADAS("Actualizadas");
    private final String descripcion;

    OrdenarResenaEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }
}
