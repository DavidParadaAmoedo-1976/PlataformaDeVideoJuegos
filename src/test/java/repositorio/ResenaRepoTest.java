package repositorio;

import org.davidparada.modelo.entidad.ResenaEntidad;
import org.davidparada.modelo.enums.EstadoPublicacionEnum;
import org.davidparada.modelo.formulario.ResenaForm;
import org.davidparada.repositorio.implementacionMemoria.ResenaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResenaRepoTest {

    private ResenaRepo repo;

    @BeforeEach
    void setUp() {
        repo = new ResenaRepo();
    }

    @Test
    void buscarPorId_noExiste() {

        Optional<ResenaEntidad> resena =
                repo.buscarPorId(999L);

        assertTrue(resena.isEmpty());
    }

    @Test
    void buscarPorId_existe() {

        ResenaEntidad entidad = repo.crear(
                new ResenaForm(
                        1L,
                        1L,
                        true,
                        "Texto suficientemente largo para cumplir validacion",
                        10.0,
                        Instant.now(),
                        null,
                        EstadoPublicacionEnum.PUBLICADA
                )
        );

        Optional<ResenaEntidad> resultado =
                repo.buscarPorId(entidad.getIdResena());

        assertTrue(resultado.isPresent());
    }
}
