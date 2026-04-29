package com.adrianpaneda.tarea3AD2024base.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion;

/**
 * Repositorio para la gestión de personas de coordinación del circo.
 * <p>
 * Proporciona operaciones CRUD necesarias para Gestionar personas y Asignar
 * coordinador a espectáculo.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Coordinacion
 */
@Repository
public interface CoordinacionRepository extends JpaRepository<Coordinacion, Long> {

}