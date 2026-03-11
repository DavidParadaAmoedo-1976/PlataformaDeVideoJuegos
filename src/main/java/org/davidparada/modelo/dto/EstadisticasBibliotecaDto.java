package org.davidparada.modelo.dto;

import java.util.List;

public record EstadisticasBibliotecaDto(Integer totalDeJuegos,
                                        Double horasTotales,
                                        List<String> juegosInstalados,
                                        String juegoMasJugado,
                                        Double valorTotal,
                                        List<String> juegosNuncaJugados) {
}

