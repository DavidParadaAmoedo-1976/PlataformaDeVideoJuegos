package repositorio;

import org.davidparada.modelo.entidad.JuegoEntidad;
import org.davidparada.modelo.enums.ClasificacionJuegoEnum;
import org.davidparada.modelo.enums.EstadoJuegoEnum;
import org.davidparada.modelo.formulario.JuegoForm;
import org.davidparada.repositorio.implementacionMemoria.JuegoRepoMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JuegoRepoMemoriaTest {

    private JuegoRepoMemoria juegoRepoMemoria;

    @BeforeEach
    void setUp() {
        juegoRepoMemoria = new JuegoRepoMemoria();
    }

    @Test
    void buscarPorId_vacio() {

        Optional<JuegoEntidad> resultado =
                juegoRepoMemoria.buscarPorId(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_encontrado() {

        JuegoEntidad juego = juegoRepoMemoria.crear(
                new JuegoForm(
                        "Zelda",
                        "desc",
                        "Nintendo",
                        LocalDate.now(),
                        50.0,
                        0,
                        "Aventura",
                        ClasificacionJuegoEnum.PEGI_12,
                        new String[]{"ES"},
                        EstadoJuegoEnum.DISPONIBLE
                )
        );

        Optional<JuegoEntidad> resultado =
                juegoRepoMemoria.buscarPorId(juego.getIdJuego());

        assertTrue(resultado.isPresent());
    }

}
