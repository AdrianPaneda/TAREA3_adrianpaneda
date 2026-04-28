package com.adrianpaneda.tarea3AD2024base.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa las credenciales de acceso de una persona al sistema
 * de gestión del circo.
 * <p>
 * Cada persona registrada en el sistema dispone de unas credenciales únicas
 * compuestas por un nombre de usuario y una contraseña, asociadas a un perfil
 * que determina las funcionalidades a las que tiene acceso.
 * </p>
 * <p>
 * Las credenciales del administrador son fijas: {@code admin} / {@code admin},
 * y su perfil es {@link Perfil#admin}.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Perfil
 * @see com.adrianpaneda.tarea3AD2024base.modelo.Persona
 */
@Entity
@Table(name = "credenciales")
public class Credenciales {

	/**
	 * Identificador único de las credenciales, generado automáticamente por la base
	 * de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Nombre de usuario. Debe ser único en el sistema, no puede contener espacios
	 * en blanco, debe tener más de 2 caracteres y solo acepta letras sin tildes ni
	 * diéresis. Se almacena siempre en minúsculas.
	 */
	@Column(unique = true, nullable = false)
	private String nombreUsuario;

	/**
	 * Contraseña de acceso. No puede contener espacios en blanco y debe tener más
	 * de 2 caracteres.
	 */
	@Column(nullable = false)
	private String password;

	/**
	 * Perfil asociado a estas credenciales. Determina las funcionalidades
	 * disponibles para el usuario autenticado.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Perfil perfil;

	/**
	 * Obtiene el identificador único de las credenciales.
	 *
	 * @return el identificador de las credenciales
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único de las credenciales.
	 *
	 * @param id el identificador a asignar
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre de usuario.
	 *
	 * @return el nombre de usuario en minúsculas
	 */
	public String getNombre() {
		return nombreUsuario;
	}

	/**
	 * Establece el nombre de usuario. Se recomienda pasarlo ya en minúsculas antes
	 * de llamar a este método.
	 *
	 * @param nombre el nombre de usuario a asignar
	 */
	public void setNombre(String nombre) {
		this.nombreUsuario = nombre;
	}

	/**
	 * Obtiene la contraseña de acceso.
	 *
	 * @return la contraseña
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Establece la contraseña de acceso.
	 *
	 * @param password la contraseña a asignar
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Obtiene el perfil asociado a estas credenciales.
	 *
	 * @return el perfil del usuario
	 */
	public Perfil getPerfil() {
		return perfil;
	}

	/**
	 * Establece el perfil asociado a estas credenciales.
	 *
	 * @param perfil el perfil a asignar
	 */
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
}