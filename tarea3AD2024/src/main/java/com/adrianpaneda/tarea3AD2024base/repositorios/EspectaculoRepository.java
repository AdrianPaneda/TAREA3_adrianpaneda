package com.adrianpaneda.tarea3AD2024base.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;

/**
 * Repositorio para la gestión de espectáculos del circo.
 * <p>
 * Proporciona operaciones CRUD necesarias para CU1 Ver espectáculos, Ver
 * detalle de espectáculo y Gestionar espectáculos.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Espectaculo
 */
@Repository
public interface EspectaculoRepository extends JpaRepository<Espectaculo, Long> {

	/**
	 * Verifica si existe un espectáculo con el nombre especificado.
	 * <p>
	 * Necesario para validar unicidad de nombre en CU5A (Crear espectáculo).
	 * </p>
	 *
	 * @param nombre el nombre del espectáculo a verificar
	 * @return {@code true} si el nombre ya está registrado
	 */
	boolean existsByNombre(String nombre);
}