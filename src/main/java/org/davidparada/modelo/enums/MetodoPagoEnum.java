package org.davidparada.modelo.enums;

public enum MetodoPagoEnum {


    SALIR("Salir"),
    TARJETA("Tarjeta de Credito"),
    PAYPAL("Paypal"),
    CARTERA_STEAM("Cartera de Steam"),
    TRANSFERENCIA("Transferencia bancaria");

    private final String descripcion;

    MetodoPagoEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

