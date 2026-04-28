package com.luisdbb.tarea3AD2024base.modelo;

/**
 * Enumeración que representa los perfiles de usuario disponibles en el sistema
 * de gestión del circo.
 * <p>
 * Cada perfil determina las funcionalidades accesibles para el usuario
 * autenticado. El perfil {@code admin} es único en el sistema y tiene acceso
 * completo a todas las funcionalidades.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see com.luisdbb.tarea3AD2024base.modelo.Credenciales
 */
public enum Perfil {

	/**
	 * Perfil de administrador general. Es único en el sistema y tiene acceso
	 * completo a todas las funcionalidades: gestión de personas, credenciales,
	 * espectáculos y números.
	 */
	admin,

	/**
	 * Perfil de coordinación. Permite gestionar espectáculos, los números que los
	 * componen y asignar artistas a dichos números.
	 */
	coordinacion,

	/**
	 * Perfil de artista. Permite visualizar la ficha personal del artista en el
	 * circo, incluyendo sus participaciones en números de espectáculos.
	 */
	artista
}