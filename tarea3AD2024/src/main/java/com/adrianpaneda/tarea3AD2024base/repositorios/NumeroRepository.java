package com.adrianpaneda.tarea3AD2024base.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Numero;

/**
 * Repositorio para la gestión de números artísticos de espectáculos.
 * <p>
 * Proporciona operaciones CRUD necesarias para Gestionar números y Asignar
 * artistas a números.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Numero
 */
@Repository
public interface NumeroRepository extends JpaRepository<Numero, Long> {

	/**
	 * Obtiene los números de un espectáculo ordenados por orden ascendente.
	 *
	 * @param espectaculoId el id del espectáculo
	 * @return lista de números ordenados
	 */
	List<Numero> findByEspectaculoIdOrderByOrdenAsc(Long espectaculoId);

	/**
	 * Obtiene un número con sus artistas cargados.
	 *
	 * @param id el id del número
	 * @return Optional con el número y sus artistas
	 */
	@Query("SELECT n FROM Numero n LEFT JOIN FETCH n.artistas WHERE n.id = :id")
	Optional<Numero> findByIdConArtistas(@Param("id") Long id);

}