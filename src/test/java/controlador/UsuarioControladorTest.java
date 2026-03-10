package controlador;

import org.DavidParada.controlador.UsuarioControlador;
import org.DavidParada.excepcion.ValidationException;
import org.DavidParada.modelo.dto.UsuarioDto;
import org.DavidParada.modelo.enums.EstadoCuentaEnum;
import org.DavidParada.modelo.enums.PaisEnum;
import org.DavidParada.modelo.formulario.UsuarioForm;
import org.DavidParada.modelo.formulario.validacion.UsuarioFormValidador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.DavidParada.repositorio.implementacionMemoria.UsuarioRepo;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioControladorTest {

    private UsuarioControlador usuarioControlador;

    @BeforeEach
    void setUp() {
        UsuarioRepo usuarioRepo = new UsuarioRepo();
        UsuarioFormValidador.setUsuarioRepo(usuarioRepo);
        usuarioControlador = new UsuarioControlador(usuarioRepo);
    }

    // Registrar nuevo usuario

    @Test
    void registrarUsuarioCorrectamente() throws ValidationException {

        UsuarioForm form = new UsuarioForm(
                "usuario1",
                "usuario1@email.com",
                "Password1",
                "Nombre Real",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                Instant.now(),
                "avatar.png",
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        UsuarioDto usuario = usuarioControlador.registrarUsuario(form);

        assertNotNull(usuario);
        assertNotNull(usuario.idUsuario());
        assertEquals("usuario1", usuario.nombreUsuario());
        assertEquals("usuario1@email.com", usuario.email());
    }

    @Test
    void registrarUsuarioFallaSiNombreEsNull() {

        UsuarioForm form = new UsuarioForm(
                null,
                "usuario@email.com",
                "Password1",
                "Nombre Real",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                Instant.now(),
                "avatar.png",
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.registrarUsuario(form);
        });
    }

    // Consultar perfil

    @Test
    void consultarPerfilDevuelveUsuarioCorrecto() throws ValidationException {

        UsuarioDto usuario = crearUsuarioBase();

        UsuarioDto consultado = usuarioControlador.consultarPerfil(usuario.idUsuario());

        assertEquals(usuario.idUsuario(), consultado.idUsuario());
    }

    @Test
    void consultarPerfilFallaSiIdEsNull() {

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.consultarPerfil(null);
        });
    }

    @Test
    void consultarPerfilFallaSiNoExiste() {

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.consultarPerfil(999L);
        });
    }

    // Añadir saldo a cartera

    @Test
    void anadirSaldoCorrectamente() throws ValidationException {

        UsuarioDto usuario = crearUsuarioBase();

        usuarioControlador.anadirSaldo(usuario.idUsuario(), 50.0);

        Double saldoActual = usuarioControlador.consultarSaldo(usuario.idUsuario());

        assertEquals(50.0, saldoActual);
    }

    @Test
    void anadirSaldoFallaSiCantidadEsNull() throws ValidationException {

        UsuarioDto usuario = crearUsuarioBase();

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.anadirSaldo(usuario.idUsuario(), null);
        });
    }

    @Test
    void anadirSaldoFallaSiFueraDeRango() throws ValidationException {

        UsuarioDto usuario = crearUsuarioBase();

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.anadirSaldo(usuario.idUsuario(), 3.0);
        });

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.anadirSaldo(usuario.idUsuario(), 600.0);
        });
    }

    // Consultar saldo

    @Test
    void consultarSaldoDevuelveCorrecto() throws ValidationException {

        UsuarioDto usuario = crearUsuarioBase();

        usuarioControlador.anadirSaldo(usuario.idUsuario(), 25.0);

        Double saldo = usuarioControlador.consultarSaldo(usuario.idUsuario());

        assertEquals(25.0, saldo);
    }

    @Test
    void consultarSaldoFallaSiIdEsNull() {

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.consultarSaldo(null);
        });
    }

    @Test
    void consultarSaldoFallaSiUsuarioNoExiste() {

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.consultarSaldo(999L);
        });
    }

    @Test
    void noPermiteEmailDuplicado() throws ValidationException {

        UsuarioForm form = new UsuarioForm(
                "usuario1",
                "duplicado@email.com",
                "Password1",
                "Juan Perez",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                Instant.parse("2000-01-01T00:00:00Z"),
                null,
                0.0,
                null
        );

        usuarioControlador.registrarUsuario(form);

        assertThrows(ValidationException.class, () ->
                usuarioControlador.registrarUsuario(form)
        );
    }


    @Test
    void noPermiteSaldoFueraDeRango() throws ValidationException {

        UsuarioForm form = new UsuarioForm(
                "usuarioRango",
                "rango@email.com",
                "Password1",
                "Juan Perez",
                PaisEnum.ESPANA,
                LocalDate.of(2000,1,1),
                Instant.parse("2000-01-01T00:00:00Z"),
                null,
                0.0,
                null
        );

        UsuarioDto usuario = usuarioControlador.registrarUsuario(form);

        assertThrows(ValidationException.class, () ->
                usuarioControlador.anadirSaldo(
                        usuario.idUsuario(),
                        1.0
                )
        );
    }

    private UsuarioDto crearUsuarioBase() throws ValidationException {

        UsuarioForm form = new UsuarioForm(
                "UsuarioBase",                    // válido
                "base" + System.nanoTime() + "@email.com", // único
                "Password1",                      // válido (mayúscula + minúscula + número)
                "Nombre Real",
                PaisEnum.ESPANA,                  // enum, no String
                LocalDate.of(2000,1,1),
                Instant.now(),
                "avatar.png",
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        return usuarioControlador.registrarUsuario(form);
    }


}

