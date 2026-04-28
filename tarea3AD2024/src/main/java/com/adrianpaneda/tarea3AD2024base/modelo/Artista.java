package com.adrianpaneda.tarea3AD2024base.modelo;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Entidad que representa a un artista del circo.
 * <p>
 * Extiende {@link Persona} añadiendo los datos profesionales propios de este
 * perfil. Un artista puede tener un apodo artístico, maneja una o varias
 * especialidades circenses, y puede participar en múltiples números de
 * diferentes espectáculos.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Persona
 * @see Especialidad
 * @see Numero
 */
@Entity
@Table(name = "artista")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Artista extends Persona {

	/**
	 * Apodo artístico del artista. Es opcional y puede ser {@code null}.
	 */
	private String apodo;

	/**
	 * Conjunto de especialidades circenses que maneja el artista. Un artista debe
	 * tener al menos una especialidad.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "artista_especialidades", joinColumns = @JoinColumn(name = "id_artista"))
	@Column(name = "especialidad")
	@Enumerated(EnumType.STRING)
	private Set<Especialidad> especialidades;

	/**
	 * Lista de números artísticos en los que participa este artista. Un artista
	 * puede participar en múltiples números de diferentes espectáculos.
	 */
	@ManyToMany(mappedBy = "artistas", fetch = FetchType.LAZY)
	private List<Numero> numeros;

	/**
	 * Obtiene la lista de números en los que participa este artista.
	 *
	 * @return lista de números artísticos
	 */
	public List<Numero> getNumeros() {
		return numeros;
	}

	/**
	 * Establece la lista de números en los que participa este artista.
	 *
	 * @param numeros la lista de números a asignar
	 */
	public void setNumeros(List<Numero> numeros) {
		this.numeros = numeros;
	}

	/**
	 * Obtiene el apodo artístico del artista.
	 *
	 * @return el apodo, o {@code null} si no tiene
	 */
	public String getApodo() {
		return apodo;
	}

	/**
	 * Establece el apodo artístico del artista.
	 *
	 * @param apodo el apodo a asignar, puede ser {@code null}
	 */
	public void setApodo(String apodo) {
		this.apodo = apodo;
	}

	/**
	 * Obtiene el conjunto de especialidades del artista.
	 *
	 * @return conjunto de especialidades
	 */
	public Set<Especialidad> getEspecialidades() {
		return especialidades;
	}

	/**
	 * Establece el conjunto de especialidades del artista.
	 *
	 * @param especialidades conjunto de especialidades a asignar
	 */
	public void setEspecialidades(Set<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}
}