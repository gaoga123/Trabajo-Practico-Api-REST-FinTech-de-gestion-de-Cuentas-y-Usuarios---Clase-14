package com.tpfintech.tpcuentas.model;

// Defino un contrato común para los objetos cuyos estados pueden activarse o desactivarse.
public interface Activable {

    void activar();

    void desactivar();

    // Expongo una consulta común para comprobar el estado sin acceder al atributo
    // directamente.
    boolean estaActivo();
}