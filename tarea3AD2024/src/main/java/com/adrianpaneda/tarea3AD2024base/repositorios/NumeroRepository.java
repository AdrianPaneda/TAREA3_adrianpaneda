package com.adrianpaneda.tarea3AD2024base.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Numero;

/**
 * Repositorio para la gestión de números artísticos de espectáculos.
 * <p>
 * Proporciona operaciones CRUD necesarias para CU5B (Gestionar números)
 * y CU5C (Asignar artistas a números).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Numero
 */
@Repository
public interface NumeroRepository extends JpaRepository<Numero, Long> {
  
}