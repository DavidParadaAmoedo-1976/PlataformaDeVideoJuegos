package repositorio;

import org.davidparada.modelo.entidad.BibliotecaEntidad;
import org.davidparada.modelo.formulario.BibliotecaForm;
import org.davidparada.repositorio.implementacionMemoria.BibliotecaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BibliotecaRepoTest {

    private BibliotecaRepo repo;

    @BeforeEach
    void setUp() {
        repo = new BibliotecaRepo();
    }

    @Test
    void buscarPorUsuarioYJuego_noExiste() {

        Optional<BibliotecaEntidad> resultado =
                repo.buscarPorUsuarioYJuego(1L, 1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorUsuarioYJuego_existe() {

        BibliotecaEntidad entidad = repo.crear(
                new BibliotecaForm(
                        1L,
                        1L,
                        Instant.now(),
                        0.0,
                        null,
                        false
                )
        );

        Optional<BibliotecaEntidad> resultado =
                repo.buscarPorUsuarioYJuego(1L, 1L);

        assertTrue(resultado.isPresent());
        assertEquals(entidad.getIdBiblioteca(), resultado.get().getIdBiblioteca());
    }
}