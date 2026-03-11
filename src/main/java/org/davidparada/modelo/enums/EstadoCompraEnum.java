package org.davidparada.modelo.enums;

public enum EstadoCompraEnum {

    SALIR("Salir"),
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada"),
    REEMBOLSADA("Reembolsada");

    private final String TEXTO;

    EstadoCompraEnum(String TEXTO) {
        this.TEXTO = TEXTO;
    }

    public String getTexto() {
        return TEXTO;
    }
}

