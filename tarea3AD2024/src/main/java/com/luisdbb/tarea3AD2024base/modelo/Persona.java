package com.luisdbb.tarea3AD2024base.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa a una persona que trabaja en el circo.
 * <p>
 * Es la clase base del modelo de herencia del sistema. Las subclases
 * {@link Artista} y {@link Coordinacion} extienden esta clase añadiendo sus
 * datos profesionales específicos.
 * </p>
 * <p>
 * Toda persona dispone de unas {@link Credenciales} de acceso que le otorgan un
 * perfil en el sistema.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Artista
 * @see Coordinacion
 * @see Credenciales
 */
@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona {

	/**
	 * Identificador único de la persona, generado automáticamente por la base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Correo electrónico de la persona. Debe ser único en el sistema y seguir
	 * formato de email.
	 * 
	 */
	@Column(unique = true, nullable = false)
	private String email;

	/**
	 * Nombre real de la persona.
	 */
	@Column(nullable = false)
	private String nombre;

	/**
	 * Nacionalidad de la persona.
	 */
	@Column(nullable = false)
	private String nacionalidad;

	/**
	 * Credenciales de acceso asociadas a esta persona. La columna FK se crea en la
	 * tabla {@code persona}. Con {@code CascadeType.ALL} las operaciones sobre
	 * Persona se propagan automáticamente a sus Credenciales.
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_credenciales", referencedColumnName = "id")
	private Credenciales credenciales;

	/**
	 * Obtiene el identificador único de la persona.
	 *
	 * @return el identificador de la persona
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único de la persona.
	 *
	 * @param id el identificador a asignar
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el correo electrónico de la persona.
	 *
	 * @return el email de la persona
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Establece el correo electrónico de la persona.
	 *
	 * @param email el email a asignar
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Obtiene el nombre real de la persona.
	 *
	 * @return el nombre de la persona
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre real de la persona.
	 *
	 * @param nombre el nombre a asignar
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene la nacionalidad de la persona.
	 *
	 * @return la nacionalidad de la persona
	 */
	public String getNacionalidad() {
		return nacionalidad;
	}

	/**
	 * Establece la nacionalidad de la persona.
	 *
	 * @param nacionalidad la nacionalidad a asignar
	 */
	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	/**
	 * Obtiene las credenciales de acceso de la persona.
	 *
	 * @return las credenciales de la persona
	 */
	public Credenciales getCredenciales() {
		return credenciales;
	}

	/**
	 * Establece las credenciales de acceso de la persona.
	 *
	 * @param credenciales las credenciales a asignar
	 */
	public void setCredenciales(Credenciales credenciales) {
		this.credenciales = credenciales;
	}
}