package com.adrianpaneda.tarea3AD2024base.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;

/**
 * Repositorio para la entidad Artista.
 * <p>
 * Proporciona operaciones CRUD y consultas personalizadas para gestionar
 * artistas del circo.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

	/**
	 * Obtiene el artista asociado a unas credenciales por nombre de usuario.
	 * <p>
	 * Navega desde Artista hasta sus Credenciales para encontrar el artista que
	 * tiene ese nombre de usuario registrado.
	 * </p>
	 *
	 * @param nombreUsuario el nombre de usuario de las credenciales
	 * @return Optional con el artista si existe
	 */
	@Query("SELECT a FROM Artista a WHERE a.credenciales.nombreUsuario = :nombreUsuario")
	Optional<Artista> findByCredencialesNombreUsuario(@Param("nombreUsuario") String nombreUsuario);

	/**
	 * Obtiene todos los números en los que ha participado un artista.
	 * <p>
	 * Devuelve una lista con los números ordenados por fecha de inicio del
	 * espectáculo al que pertenecen (más recientes primero). Cada número incluye la
	 * referencia a su espectáculo completo.
	 * </p>
	 *
	 * @param artistaId el id del artista
	 * @return lista de números en los que participa el artista
	 */
	@Query("SELECT n FROM Numero n " + "JOIN n.artistas a " + "WHERE a.id = :artistaId "
			+ "ORDER BY n.espectaculo.fechaInicio DESC")
	List<Numero> findNumerosByArtistaId(@Param("artistaId") Long artistaId);
}
