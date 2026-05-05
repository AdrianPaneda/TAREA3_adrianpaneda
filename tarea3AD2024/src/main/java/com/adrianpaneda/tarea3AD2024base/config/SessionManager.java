package com.adrianpaneda.tarea3AD2024base.config;

import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;

/**
 * Gestor de sesión de usuario.
 * <p>
 * Mantiene en memoria el usuario actualmente autenticado en el sistema y el
 * espectáculo seleccionado para ver su detalle. Implementa el patrón Singleton
 * mediante variables y métodos estáticos.
 * </p>
 * <p>
 * La sesión se inicia cuando un usuario hace login correctamente y se almacena
 * su objeto Credenciales completo. La sesión se cierra cuando se hace logout,
 * estableciendo el usuario actual a null.
 * </p>
 * <p>
 * Esta clase permite que diferentes controladores puedan acceder a la
 * información del usuario logueado y al espectáculo seleccionado sin necesidad
 * de pasar parámetros entre pantallas.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
public class SessionManager {

	/**
	 * Usuario actualmente autenticado en el sistema. Null si no hay ningún usuario
	 * logueado (sesión de invitado).
	 */
	private static Credenciales currentUser = null;

	/**
	 * ID del espectáculo seleccionado para ver su detalle. Null si no hay ningún
	 * espectáculo seleccionado.
	 */
	private static Long selectedEspectaculoId = null;

	/**
	 * Constructor privado para prevenir instanciación. Esta clase solo debe usarse
	 * mediante sus métodos estáticos.
	 */
	private SessionManager() {
		throw new IllegalStateException("Clase de utilidad - no debe instanciarse");
	}

	/**
	 * Establece el usuario actual del sistema.
	 * <p>
	 * Se debe llamar después de un login exitoso para guardar la sesión.
	 * </p>
	 *
	 * @param user las credenciales del usuario autenticado
	 */
	public static void setCurrentUser(Credenciales user) {
		currentUser = user;
	}

	/**
	 * Obtiene el usuario actualmente autenticado.
	 *
	 * @return las credenciales del usuario logueado, o null si no hay sesión activa
	 */
	public static Credenciales getCurrentUser() {
		return currentUser;
	}

	/**
	 * Cierra la sesión actual.
	 * <p>
	 * Establece el usuario actual a null y limpia el espectáculo seleccionado,
	 * retornando al estado de invitado.
	 * </p>
	 */
	public static void logout() {
		currentUser = null;
		selectedEspectaculoId = null;
	}

	/**
	 * Verifica si hay un usuario autenticado.
	 *
	 * @return true si hay sesión activa, false si es invitado
	 */
	public static boolean isLoggedIn() {
		return currentUser != null;
	}

	/**
	 * Obtiene el perfil del usuario logueado.
	 *
	 * @return el perfil (artista, coordinacion, admin) o null si es invitado
	 */
	public static Perfil getCurrentPerfil() {
		return currentUser != null ? currentUser.getPerfil() : null;
	}

	/**
	 * Obtiene el nombre de usuario del usuario logueado.
	 *
	 * @return el nombre de usuario o null si es invitado
	 */
	public static String getCurrentUsername() {
		return currentUser != null ? currentUser.getNombreUsuario() : null;
	}

	/**
	 * Establece el ID del espectáculo seleccionado para ver su detalle.
	 * <p>
	 * Se debe llamar antes de navegar a la pantalla de detalle de espectáculo.
	 * </p>
	 *
	 * @param id el identificador del espectáculo seleccionado
	 */
	public static void setSelectedEspectaculo(Long id) {
		selectedEspectaculoId = id;
	}

	/**
	 * Obtiene el ID del espectáculo seleccionado para ver su detalle.
	 *
	 * @return el identificador del espectáculo seleccionado, o null si no hay
	 *         ninguno
	 */
	public static Long getSelectedEspectaculo() {
		return selectedEspectaculoId;
	}
}