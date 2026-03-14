package org.davidparada.modelo.enums;

public enum CriterioPopularidadEnum {
            MAS_VENDIDOS("Mas vendidos"),
            MEJOR_VALORADOS("Mejor valorados"),
            MAS_JUGADOS("Mas jugados"),;

    private final String descripcion;

    CriterioPopularidadEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }

}
