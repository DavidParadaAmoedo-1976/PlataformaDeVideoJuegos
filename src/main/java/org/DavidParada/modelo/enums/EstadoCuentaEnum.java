package org.DavidParada.modelo.enums;

public enum EstadoCuentaEnum {

    SALIR("Salir"),
    ACTIVA("Activa"),
    SUSPENDIDA("Suspendida"),
    BANEADA("Baneada");


    private final String TEXTO;

    EstadoCuentaEnum(String TEXTO) {
        this.TEXTO = TEXTO;
    }

    public String getTexto() {
        return TEXTO;
    }
}

