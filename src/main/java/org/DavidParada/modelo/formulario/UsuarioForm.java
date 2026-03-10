package org.DavidParada.modelo.formulario;

import org.DavidParada.modelo.enums.EstadoCuentaEnum;
import org.DavidParada.modelo.enums.PaisEnum;

import java.time.Instant;
import java.time.LocalDate;

public class UsuarioForm {
    private String nombreUsuario;
    private String email;
    private String password;
    private String nombreReal;
    private PaisEnum pais;
    private LocalDate fechaNacimiento;
    private Instant fechaRegistro;
    private String avatar;
    private Double saldo;
    private EstadoCuentaEnum estadoCuenta;

    public UsuarioForm() {
    }

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

