package com.adrianpaneda.tarea3AD2024base.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Artista;

/**
 * Repositorio para la gestión de artistas del circo.
 * <p>
 * Proporciona operaciones CRUD necesarias para CU3 (Gestionar personas),
 * CU5C (Asignar artistas a números) y CU6 (Ver ficha de artista).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Artista
 */
@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {
  
}