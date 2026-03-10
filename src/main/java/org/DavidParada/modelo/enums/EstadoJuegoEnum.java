package org.DavidParada.modelo.enums;

public enum EstadoJuegoEnum {

    SALIR("Salir"),
    NO_DISPONIBLE("No disponible"),
    DISPONIBLE("Disponible"),
    PREVENTA("Preventa"),
    ACCESO_ANTICIPADO("Acceso Anticipado");

    private final String TEXTO;

    EstadoJuegoEnum(String TEXTO) {
        this.TEXTO = TEXTO;
    }

    public String getTexto() {
        return TEXTO;
    }
}

