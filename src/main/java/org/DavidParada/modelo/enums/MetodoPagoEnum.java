package org.DavidParada.modelo.enums;

public enum MetodoPagoEnum {


    SALIR("Salir"),
    TARJETA("Tarjeta de Credito"),
    PAYPAL("Paypal"),
    CARTERA_STEAM("Cartera de Steam"),
    TRANSFERENCIA("Transferencia bancaria"),
    ;

    private final String TEXTO;

    MetodoPagoEnum(String TEXTO) {
        this.TEXTO = TEXTO;
    }

    public String getTexto() {
        return TEXTO;
    }
}

