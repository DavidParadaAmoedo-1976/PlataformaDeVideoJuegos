package org.DavidParada.modelo.enums;

public enum TipoErrorEnum {

    OBLIGATORIO("Campo obligatorio"),
    FORMATO_INVALIDO("Formato inválido"),
    VALOR_NEGATIVO("Valor negativo"),
    LONGITUD_EXCEDIDA("Longitud excedida"),
    VALOR_EXCEDIDO("Valor excedido"),
    RANGO_INVALIDO("Fuera de rango"),
    DUPLICADO("Duplicado"),
    NO_ENCONTRADO("No encontrado"),
    ESTADO_INCORRECTO("Estado incorrecto"),
    NO_PERMITIDO("No permitido"),
    SALDO_INSUFICIENTE("Saldo insuficiente"),
    OTRO("Otro");

    private final String descripcion;

    TipoErrorEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }
}

