package org.davidparada.modelo.formulario;

import org.davidparada.modelo.enums.EstadoCuentaEnum;
import org.davidparada.modelo.enums.PaisEnum;

import java.time.Instant;
import java.time.LocalDate;

public class UsuarioForm {
    private final String nombreUsuario;
    private final String email;
    private final String password;
    private final String nombreReal;
    private final PaisEnum pais;
    private final LocalDate fechaNacimiento;
    private final Instant fechaRegistro;
    private final String avatar;
    private final Double saldo;
    private final EstadoCuentaEnum estadoCuenta;

    public UsuarioForm(String nombreUsuario,
                       String email,
                       String password,
                       String nombreReal,
                       PaisEnum pais,
                       LocalDate fechaNacimiento,
                       Instant fechaRegistro,
                       String avatar,
                       Double saldo,
                       EstadoCuentaEnum estadoCuenta) {

        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.nombreReal = nombreReal;
        this.pais = pais;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaRegistro = fechaRegistro;
        this.avatar = avatar;
        this.saldo = saldo;
        this.estadoCuenta = estadoCuenta;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public PaisEnum getPais() {
        return pais;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public String getAvatar() {
        return avatar;
    }

    public Double getSaldo() {
        return saldo;
    }

    public EstadoCuentaEnum getEstadoCuenta() {
        return estadoCuenta;
    }

}

