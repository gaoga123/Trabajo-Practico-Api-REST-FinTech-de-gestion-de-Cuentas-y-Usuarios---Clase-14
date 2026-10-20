package com.tpfintech.tpcuentas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// Modelo al usuario como entidad y reutilizo los atributos comunes definidos en Persona.
@Entity
@Table(name = "usuarios")
public class Usuario extends Persona implements Activable {

    // Exijo que el teléfono esté informado y sea único en la base de datos.
    @Column(nullable = false, unique = true)
    private String telefono;

    @Column(nullable = false)
    private LocalDateTime fechaAlta;

    @Column(nullable = false)
    private boolean activo;

    // Mantengo el constructor sin argumentos para que JPA pueda crear instancias.
    protected Usuario() {
    }

    public Usuario(
            String nombre,
            String apellido,
            String email,
            String telefono) {
        // Reutilizo el constructor de Persona para inicializar los atributos heredados.
        super(nombre, apellido, email);
        this.telefono = telefono;

        // Doy de alta al usuario como activo por defecto.
        this.activo = true;
    }

    // Registro la fecha de alta automáticamente antes de persistir al usuario por
    // primera vez.
    @PrePersist
    protected void antesDePersistir() {
        if (fechaAlta == null) {
            fechaAlta = LocalDateTime.now();
        }
    }

    @Override
    public void activar() {
        this.activo = true;
    }

    @Override
    public void desactivar() {
        this.activo = false;
    }

    @Override
    public boolean estaActivo() {
        return activo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public boolean isActivo() {
        return activo;
    }
}