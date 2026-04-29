package com.adrianpaneda.tarea3AD2024base.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;

/**
 * Repositorio para la gestión de credenciales de acceso al sistema.
 * <p>
 * Proporciona operaciones CRUD y métodos necesarios para la autenticación de
 * usuarios y validación de unicidad de nombres de usuario .
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Credenciales
 */
@Repository
public interface CredencialesRepository extends JpaRepository<Credenciales, Long> {

	/**
	 * Busca credenciales por nombre de usuario.
	 * <p>
	 * Necesario para la autenticación en CU2 (Login).
	 * </p>
	 *
	 * @param nombreUsuario el nombre de usuario a buscar (en minúsculas)
	 * @return un {@link Optional} con las credenciales si existen
	 */
	Optional<Credenciales> findByNombreUsuario(String nombreUsuario);

	/**
	 * Verifica si existe un nombre de usuario en el sistema.
	 * <p>
	 * Necesario para validar unicidad en CU3 (Registrar persona).
	 * </p>
	 *
	 * @param nombreUsuario el nombre de usuario a verificar
	 * @return {@code true} si el nombre de usuario ya existe
	 */
	boolean existsByNombreUsuario(String nombreUsuario);
}