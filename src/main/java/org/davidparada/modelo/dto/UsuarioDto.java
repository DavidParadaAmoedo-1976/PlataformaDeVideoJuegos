package org.davidparada.modelo.dto;

import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.enums.PaisEnum;

import java.time.Instant;
import java.time.LocalDate;

public record UsuarioDto(Long idUsuario,
                         String nombreUsuario,
                         String email,
                         String password,
                         String nombreReal,
                         PaisEnum pais,
                         LocalDate fechaNacimiento,
                         Instant fechaRegistro,
                         String avatar,
                         Double saldo,
                         EstadoCuentaEnum estadoCuenta) {
}
