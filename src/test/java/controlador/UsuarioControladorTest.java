package controlador;

import org.davidparada.controlador.UsuarioControlador;
import org.davidparada.controlador.util.ObtenerEntidadesOptional;
import org.davidparada.excepcion.ValidationException;
import org.davidparada.modelo.dto.UsuarioDto;
import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.enums.PaisEnum;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepoMemoria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioControladorTest {

    private UsuarioControlador usuarioControlador;
    private UsuarioRepoMemoria usuarioRepoMemoria;

    @BeforeEach
    void setUp() {

        usuarioRepoMemoria = new UsuarioRepoMemoria();
        usuarioControlador = new UsuarioControlador(usuarioRepoMemoria);

        new ObtenerEntidadesOptional(null, usuarioRepoMemoria, null, null, null);
    }

    // ==========================
    // REGISTRAR USUARIO
    // ==========================

    @Test
    void registrarUsuario_ok() throws ValidationException {

        UsuarioForm form = crearUsuarioForm();

        UsuarioDto usuario = usuarioControlador.registrarUsuario(form);

        assertNotNull(usuario);
        assertNotNull(usuario.idUsuario());
        assertEquals("usuarioTest", usuario.nombreUsuario());
        assertEquals("usuario@test.com", usuario.email());
    }

    @Test
    void registrarUsuario_nombreNull() {

        UsuarioForm form = new UsuarioForm(
                null,
                "email@test.com",
                "Password1",
                "Nombre",
                PaisEnum.ESPANA,
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                "avatar",
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.registrarUsuario(form)
        );
    }

    @Test
    void crearUsuario_nombreUsuarioNoUnico_lanzaValidationException() throws ValidationException {

        UsuarioForm form1 = crearUsuarioForm();
        usuarioControlador.registrarUsuario(form1);

        UsuarioForm form2 = new UsuarioForm(
                form1.getNombreUsuario(), // mismo nombre
                "otro@email.com",
                "Password1",
                "Nombre Real",
                PaisEnum.ESPANA,
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                null,
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.registrarUsuario(form2)
        );
    }

    @Test
    void crearUsuario_paisNull_lanzaValidationException() {

        UsuarioForm form = new UsuarioForm(
                "usuarioPais",
                "pais@email.com",
                "Password1",
                "Nombre Real",
                null, // pais null
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                null,
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.registrarUsuario(form)
        );
    }

    @Test
    void noPermiteNombreUsuarioDuplicado() throws ValidationException {

        UsuarioForm form = new UsuarioForm(
                "duplicado",
                "email1@test.com",
                "Password1",
                "Nombre",
                PaisEnum.ESPANA,
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                null,
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        usuarioControlador.registrarUsuario(form);

        UsuarioForm duplicado = new UsuarioForm(
                "duplicado",
                "otro@email.com",
                "Password1",
                "Nombre",
                PaisEnum.ESPANA,
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                null,
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.registrarUsuario(duplicado)
        );
    }

    @Test
    void crearUsuario_emailNoUnico_lanzaValidationException() throws ValidationException {

        UsuarioForm form1 = crearUsuarioForm();
        usuarioControlador.registrarUsuario(form1);

        UsuarioForm form2 = new UsuarioForm(
                "usuario2",
                form1.getEmail(), // mismo email
                "Password1",
                "Nombre Real",
                PaisEnum.ESPANA,
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                null,
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.registrarUsuario(form2)
        );
    }

    @Test
    void registrarUsuario_emailDuplicado() throws ValidationException {

        UsuarioForm form = crearUsuarioForm();

        usuarioControlador.registrarUsuario(form);

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.registrarUsuario(form)
        );
    }

    @Test
    void noPermiteUsuarioMenorDeEdad() {

        UsuarioForm form = new UsuarioForm(
                "menor",
                "menor@test.com",
                "Password1",
                "Nombre",
                PaisEnum.ESPANA,
                LocalDate.now().minusYears(10),
                Instant.now(),
                null,
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.registrarUsuario(form)
        );
    }

    // ==========================
    // CONSULTAR PERFIL
    // ==========================

    @Test
    void consultarPerfil_ok() throws ValidationException {

        UsuarioDto usuario = usuarioControlador.registrarUsuario(crearUsuarioForm());

        UsuarioDto consultado =
                usuarioControlador.consultarPerfil(usuario.idUsuario());

        assertEquals(usuario.idUsuario(), consultado.idUsuario());
    }

    @Test
    void consultarPerfil_nombreUsuarioValido_retornaUsuarioDTO() throws ValidationException {

        UsuarioDto usuario =
                usuarioControlador.registrarUsuario(crearUsuarioForm());

        UsuarioDto resultado =
                usuarioControlador.consultarPerfil(usuario.nombreUsuario());

        assertNotNull(resultado);
        assertEquals(usuario.nombreUsuario(), resultado.nombreUsuario());
    }

    @Test
    void consultarPerfil_nombreUsuarioInvalido_retornaNull() {

        UsuarioDto resultado =
                usuarioControlador.consultarPerfil("usuarioInexistente");

        assertNull(resultado);
    }

    @Test
    void consultarPerfil_idNull() {

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.consultarPerfil((Long) null)
        );
    }

    @Test
    void consultarPerfil_usuarioNoExiste() {

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.consultarPerfil(999L)
        );
    }

    // ==========================
    // SALDO
    // ==========================

    @Test
    void anadirSaldo_usuarioNoExiste_lanzaValidationException() {

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.anadirSaldo(999L, 50.0)
        );
    }

    @Test
    void anadirSaldo_acumulaSaldoCorrectamente() throws ValidationException {

        UsuarioDto usuario =
                usuarioControlador.registrarUsuario(crearUsuarioForm());

        usuarioControlador.anadirSaldo(usuario.idUsuario(), 20.0);
        usuarioControlador.anadirSaldo(usuario.idUsuario(), 30.0);

        Double saldo =
                usuarioControlador.consultarSaldo(usuario.idUsuario());

        assertEquals(50.0, saldo);
    }

    @Test
    void registrarUsuario_fechaRegistroNull_seGeneraAutomaticamente() throws ValidationException {

        UsuarioForm form = new UsuarioForm(
                "usuarioFecha",
                "fecha@email.com",
                "Password1",
                "Nombre Real",
                PaisEnum.ESPANA,
                LocalDate.of(2000, 1, 1),
                null, // fecha null
                null,
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        UsuarioDto usuario = usuarioControlador.registrarUsuario(form);

        assertNotNull(usuario.fechaRegistro());
    }

    @Test
    void anadirSaldo_ok() throws ValidationException {

        UsuarioDto usuario =
                usuarioControlador.registrarUsuario(crearUsuarioForm());

        usuarioControlador.anadirSaldo(usuario.idUsuario(), 50.0);

        Double saldo =
                usuarioControlador.consultarSaldo(usuario.idUsuario());

        assertEquals(50.0, saldo);
    }

    @Test
    void anadirSaldo_cantidadNull() throws ValidationException {

        UsuarioDto usuario =
                usuarioControlador.registrarUsuario(crearUsuarioForm());

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.anadirSaldo(usuario.idUsuario(), null)
        );
    }

    @Test
    void anadirSaldo_fueraDeRango() throws ValidationException {

        UsuarioDto usuario =
                usuarioControlador.registrarUsuario(crearUsuarioForm());

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.anadirSaldo(usuario.idUsuario(), 3.0)
        );

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.anadirSaldo(usuario.idUsuario(), 600.0)
        );
    }

    // ==========================
    // CONSULTAR SALDO
    // ==========================

    @Test
    void consultarSaldo_ok() throws ValidationException {

        UsuarioDto usuario =
                usuarioControlador.registrarUsuario(crearUsuarioForm());

        usuarioControlador.anadirSaldo(usuario.idUsuario(), 25.0);

        Double saldo =
                usuarioControlador.consultarSaldo(usuario.idUsuario());

        assertEquals(25.0, saldo);
    }

    @Test
    void consultarSaldo_usuarioNoExiste() {

        assertThrows(
                ValidationException.class,
                () -> usuarioControlador.consultarSaldo(999L)
        );
    }

    @Test
    void consultarSaldoFallaSiIdEsNull() {

        assertThrows(ValidationException.class, () -> {
            usuarioControlador.consultarSaldo(null);
        });
    }

    @Test
    void saldoInicialEsCero() throws ValidationException {

        UsuarioDto usuario = crearUsuarioBase();

        Double saldo = usuarioControlador.consultarSaldo(usuario.idUsuario());

        assertEquals(0.0, saldo);
    }


    // ==========================
    // HELPERS
    // ==========================

    private UsuarioForm crearUsuarioForm() {

        return new UsuarioForm(
                "usuarioTest",
                "usuario@test.com",
                "Password1",
                "Nombre Real",
                PaisEnum.ESPANA,
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                "avatar.png",
                0.0,
                EstadoCuentaEnum.ACTIVA
        );
    }

    private UsuarioDto crearUsuarioBase() throws ValidationException {

        UsuarioForm form = new UsuarioForm(
                "UsuarioBase",                    // válido
                "base" + System.nanoTime() + "@email.com", // único
                "Password1",                      // válido (mayúscula + minúscula + número)
                "Nombre Real",
                PaisEnum.ESPANA,                  // enum, no String
                LocalDate.of(2000, 1, 1),
                Instant.now(),
                "avatar.png",
                0.0,
                EstadoCuentaEnum.ACTIVA
        );

        return usuarioControlador.registrarUsuario(form);
    }
}
