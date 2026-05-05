package com.adrianpaneda.tarea3AD2024base.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	 * Necesario para validar la unicidad del nombre de espectaculo.
	 * </p>
	 *
	 * @param nombre el nombre del espectáculo a verificar
	 * @return {@code true} si el nombre ya está registrado
	 */
	boolean existsByNombre(String nombre);

	/**
	 * Obtiene un espectáculo por su ID cargando todos sus datos relacionados.
	 * <p>
	 * Utiliza JOIN FETCH para cargar en una sola consulta la coordinación, los
	 * números y los artistas de cada número, evitando problemas de
	 * LazyInitializationException al acceder a colecciones fuera de la transacción
	 * JPA.
	 * </p>
	 *
	 * @param id el identificador del espectáculo
	 * @return Optional con el espectáculo y todos sus datos relacionados
	 */
	@Query("SELECT e FROM Espectaculo e " + "LEFT JOIN FETCH e.coordinacion " + "LEFT JOIN FETCH e.numeros n "
			+ "LEFT JOIN FETCH n.artistas " + "WHERE e.id = :id")
	Optional<Espectaculo> findByIdConDetalle(@Param("id") Long id);
}