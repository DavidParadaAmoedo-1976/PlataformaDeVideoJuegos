package org.davidparada.modelo.dto;

public record EstadisticasResenasJuegoDto(int totalResenas,
                                          double porcentajePositivas,
                                          double porcentajeNegativas,
                                          double promedioHorasJugadas,
                                          String tendencia) {
}
