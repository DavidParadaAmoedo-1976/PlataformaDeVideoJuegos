package repositorio;

import org.davidparada.modelo.entidad.UsuarioEntidad;
import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.enums.PaisEnum;
import org.davidparada.modelo.formulario.UsuarioForm;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioRepoTest {

    private UsuarioRepo usuarioRepo;

    @BeforeEach
    void setUp() {
        usuarioRepo = new UsuarioRepo();
    }

    @Test
    void buscarPorId_devuelveOptionalVacio() {

        Optional<UsuarioEntidad> resultado =
                usuarioRepo.buscarPorId(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_devuelveOptionalConUsuario() {

        UsuarioEntidad usuario = usuarioRepo.crear(
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
                usuarioRepo.buscarPorId(usuario.getIdUsuario());

        assertTrue(resultado.isPresent());
        assertEquals("user", resultado.get().getNombreUsuario());
    }

}
