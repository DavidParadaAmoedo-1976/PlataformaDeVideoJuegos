package org.davidparada;

import org.davidparada.controlador.UsuarioControlador;
import org.davidparada.modelo.formulario.validacion.JuegoFormValidador;
import org.davidparada.modelo.formulario.validacion.UsuarioFormValidador;
import org.davidparada.repositorio.implementacionMemoria.JuegoRepo;
import org.davidparada.repositorio.implementacionMemoria.UsuarioRepo;

public class Program {

    static void main(String[] args) {

        // Repositorios
        JuegoRepo juegoRepo = new JuegoRepo();
        UsuarioRepo usuarioRepo = new UsuarioRepo();

        // Inyección en validadores
        JuegoFormValidador.setJuegoRepo(juegoRepo);
        UsuarioFormValidador.setUsuarioRepo(usuarioRepo);

        // Controladores
        UsuarioControlador usuarioControlador =
                new UsuarioControlador(usuarioRepo);

    }

}

