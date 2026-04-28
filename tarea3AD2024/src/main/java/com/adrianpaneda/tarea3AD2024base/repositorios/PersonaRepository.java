package com.adrianpaneda.tarea3AD2024base.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Persona;

/**
 * Repositorio para la gestión de personas del circo.
 * <p>
 * Proporciona operaciones CRUD y validación de unicidad de email
 * necesaria para CU3 (Gestionar personas).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Persona
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    /**
     * Verifica si existe una persona con el email especificado.
     * <p>
     * Necesario para validar unicidad de email en CU3 (Registrar persona).
     * </p>
     *
     * @param email el correo electrónico a verificar
     * @return {@code true} si el email ya está registrado
     */
    boolean existsByEmail(String email);
}