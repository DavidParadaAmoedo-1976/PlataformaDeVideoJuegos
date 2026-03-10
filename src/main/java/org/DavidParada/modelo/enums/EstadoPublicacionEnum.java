package org.DavidParada.modelo.enums;

public enum EstadoPublicacionEnum {

    SALIR("Salir"),
    PUBLICADA("Publicada"),
    OCULTA("Oculta"),
    ELIMINADA("Eliminada");

    private final String TEXTO;

    EstadoPublicacionEnum(String TEXTO) {
        this.TEXTO = TEXTO;
    }

    public String getTexto() {
        return TEXTO;
    }
}

