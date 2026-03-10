package org.DavidParada.modelo.dto;

import org.DavidParada.modelo.enums.EstadoCuentaEnum;
import org.DavidParada.modelo.enums.PaisEnum;

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
