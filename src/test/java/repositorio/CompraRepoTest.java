package repositorio;

import org.davidparada.modelo.entidad.CompraEntidad;
import org.davidparada.modelo.enums.EstadoCompraEnum;
import org.davidparada.modelo.enums.MetodoPagoEnum;
import org.davidparada.modelo.formulario.CompraForm;
import org.davidparada.repositorio.implementacionMemoria.CompraRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CompraRepoTest {

    private CompraRepo repo;

    @BeforeEach
    void setUp() {
        repo = new CompraRepo();
    }

    @Test
    void buscarPorId_noExiste() {

        Optional<CompraEntidad> compra =
                repo.buscarPorId(999L);

        assertTrue(compra.isEmpty());
    }

    @Test
    void buscarPorId_existe() {

        CompraEntidad entidad = repo.crear(
                new CompraForm(
                        1L,
                        1L,
                        Instant.now(),
                        MetodoPagoEnum.PAYPAL,
                        50.0,
                        50.0,
                        EstadoCompraEnum.PENDIENTE
                )
        );

        Optional<CompraEntidad> resultado =
                repo.buscarPorId(entidad.getIdCompra());

        assertTrue(resultado.isPresent());
    }
}
