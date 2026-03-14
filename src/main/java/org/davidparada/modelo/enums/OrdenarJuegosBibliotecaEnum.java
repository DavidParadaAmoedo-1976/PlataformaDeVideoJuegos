package org.davidparada.modelo.enums;

public enum OrdenarJuegosBibliotecaEnum {

    ALFABETICO("Alfabetico"),
    TIEMPO_DE_JUEGO("Tiempo de Juego"),
    ULTIMA_SESION("Ultima sesion"),
    FECHA_DE_ADQUISICION("Fecha de Adquisicion"),;

    private final String descripcion;

    OrdenarJuegosBibliotecaEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }
}
