// Diplomatura en Desarrollo de Software FinTech: IA y Microservicios -----------------------------------
// Clase 14 - Trabajo Práctico: Api REST de gestión de Cuentas y Usuarios -------------------------------
// Profesor: Nicolás Ramos ------------------------------------------------------------------------------
// Alumno: Gastón Ariel Ocampo --------------------------------------------------------------------------
// Fecha de Entrega: 21-10-2026 -------------------------------------------------------------------------

package com.tpfintech.tpcuentas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marco el punto de entrada de la aplicación y habilito la configuración automática de Spring Boot.
@SpringBootApplication
public class TpApiCuentasApplication {

    public static void main(String[] args) {
        // Inicio el contexto de Spring y levanto la aplicación.
        SpringApplication.run(TpApiCuentasApplication.class, args);

        // Muestro accesos útiles para facilitar el acceso y las pruebas.
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("  TP API FINTECH - API REST de usuarios y cuentas bancarias");
        System.out.println("  App:      \thttp://localhost:8080/usuarios");
        System.out.println("  Swagger:  \thttp://localhost:8080/swagger-ui/index.html");
        System.out.println("  JDBC URL: \tjdbc:mysql://localhost:3306/tp_fintech  user: root");
        System.out.println("==================================================================");
        System.out.println();
    }

}