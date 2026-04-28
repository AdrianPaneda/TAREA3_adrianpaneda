package com.luisdbb.tarea3AD2024base.modelo;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Entidad que representa a una persona de coordinación del circo.
 * <p>
 * Extiende {@link Persona} añadiendo los datos profesionales propios de este
 * perfil. Una persona de coordinación puede dirigir uno o varios espectáculos,
 * y puede tener categoría senior con una fecha desde la que ostenta dicha
 * categoría.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Persona
 * @see Espectaculo
 */
@Entity
@Table(name = "coordinacion")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Coordinacion extends Persona {

	/**
	 * Indica si la persona de coordinación tiene categoría senior. Por defecto es
	 * {@code false}.
	 */
	@Column(nullable = false)
	private boolean senior = false;

	/**
	 * Fecha desde la que ostenta la categoría senior. Es {@code null} si
	 * {@code senior} es {@code false}.
	 */
	@Column(name = "fecha_senior")
	private LocalDate fechaSenior;

	/**
	 * Lista de espectáculos que dirige esta persona de coordinación. Una persona de
	 * coordinación puede dirigir varios espectáculos.
	 */
	@OneToMany(mappedBy = "Coordinacion", fetch = FetchType.LAZY)
	private List<Espectaculo> espectaculos;

	/**
	 * Obtiene la lista de espectáculos que dirige esta persona de coordinación.
	 *
	 * @return lista de espectáculos dirigidos
	 */
	public List<Espectaculo> getEspectaculos() {
		return espectaculos;
	}

	/**
	 * Establece la lista de espectáculos que dirige esta persona de coordinación.
	 *
	 * @param espectaculos la lista de espectáculos a asignar
	 */
	public void setEspectaculos(List<Espectaculo> espectaculos) {
		this.espectaculos = espectaculos;
	}

	/**
	 * Indica si la persona de coordinación tiene categoría senior.
	 *
	 * @return {@code true} si es senior, {@code false} en caso contrario
	 */
	public boolean isSenior() {
		return senior;
	}

	/**
	 * Establece si la persona de coordinación tiene categoría senior.
	 *
	 * @param senior {@code true} para indicar que es senior
	 */
	public void setSenior(boolean senior) {
		this.senior = senior;
	}

	/**
	 * Obtiene la fecha desde la que ostenta la categoría senior.
	 *
	 * @return la fecha senior, o {@code null} si no es senior
	 */
	public LocalDate getFechaSenior() {
		return fechaSenior;
	}

	/**
	 * Establece la fecha desde la que ostenta la categoría senior.
	 *
	 * @param fechaSenior la fecha a asignar, {@code null} si no es senior
	 */
	public void setFechaSenior(LocalDate fechaSenior) {
		this.fechaSenior = fechaSenior;
	}
}