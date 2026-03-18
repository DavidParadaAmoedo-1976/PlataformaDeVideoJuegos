package repositorio;

import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.enums.PaisEnum;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepoMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioRepoMemoriaTest {

    private UsuarioRepoMemoria usuarioRepoMemoria;

    @BeforeEach
    void setUp() {
        usuarioRepoMemoria = new UsuarioRepoMemoria();
    }

    @Test
    void buscarPorId_devuelveOptionalVacio() {

        Optional<UsuarioEntidad> resultado =
                usuarioRepoMemoria.buscarPorId(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_devuelveOptionalConUsuario() {

        UsuarioEntidad usuario = usuarioRepoMemoria.crear(
                new UsuarioForm(
                        "user",
                        "email@test.com",
                        "Password1",
                        "Nombre",
                        PaisEnum.ESPANA,
                        LocalDate.of(2000, 1, 1),
                        Instant.now(),
                        null,
                        0.0,
                        EstadoCuentaEnum.ACTIVA
                )
        );

        Optional<UsuarioEntidad> resultado =
                usuarioRepoMemoria.buscarPorId(usuario.getIdUsuario());

        assertTrue(resultado.isPresent());
        assertEquals("user", resultado.get().getNombreUsuario());
    }

}
