package com.adrianpaneda.tarea3AD2024base.modelo;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa un número artístico dentro de un espectáculo del
 * circo.
 * <p>
 * Cada número tiene un orden de aparición dentro del espectáculo al que
 * pertenece, un nombre descriptivo y una duración en minutos. Varios artistas
 * pueden participar en un mismo número.
 * </p>
 * <p>
 * La duración debe expresarse en formato x.y donde y solo puede ser 0 o 5 (por
 * ejemplo: 12.0, 8.5, 15.0).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Espectaculo
 * @see Artista
 */
@Entity
@Table(name = "numero")
public class Numero {

	/**
	 * Identificador único del número, generado automáticamente por la base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Posición del número dentro del espectáculo al que pertenece. Determina el
	 * orden de aparición.
	 */
	@Column(nullable = false)
	private int orden;

	/**
	 * Nombre descriptivo del número artístico.
	 */
	@Column(nullable = false)
	private String nombre;

	/**
	 * Duración del número en minutos. Debe expresarse en formato x.y donde y solo
	 * puede ser 0 o 5 (ejemplo: 12.0, 8.5, 15.0).
	 */
	@Column(nullable = false)
	private double duracion;

	/**
	 * Espectáculo al que pertenece este número artístico. La FK
	 * {@code id_espectaculo} se crea en la tabla {@code numero}.
	 */
	@ManyToOne
	@JoinColumn(name = "id_espectaculo")
	private Espectaculo espectaculo;

	/**
	 * Lista de artistas que participan en este número. Varios artistas pueden
	 * actuar en un mismo número. Hibernate crea automáticamente la tabla intermedia
	 * {@code numero_artista}.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "numero_artista", joinColumns = @JoinColumn(name = "id_numero"), inverseJoinColumns = @JoinColumn(name = "id_artista"))
	private List<Artista> artistas;

	/**
	 * Obtiene la lista de artistas que participan en este número.
	 *
	 * @return lista de artistas
	 */
	public List<Artista> getArtistas() {
		return artistas;
	}

	/**
	 * Establece la lista de artistas que participan en este número.
	 *
	 * @param artistas la lista de artistas a asignar
	 */
	public void setArtistas(List<Artista> artistas) {
		this.artistas = artistas;
	}

	/**
	 * Obtiene el espectáculo al que pertenece este número.
	 *
	 * @return el espectáculo al que pertenece
	 */
	public Espectaculo getEspectaculo() {
		return espectaculo;
	}

	/**
	 * Establece el espectáculo al que pertenece este número.
	 *
	 * @param espectaculo el espectáculo a asignar
	 */
	public void setEspectaculo(Espectaculo espectaculo) {
		this.espectaculo = espectaculo;
	}

	/**
	 * Obtiene el identificador único del número.
	 *
	 * @return el identificador del número
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único del número.
	 *
	 * @param id el identificador a asignar
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene la posición del número dentro del espectáculo.
	 *
	 * @return el orden de aparición
	 */
	public int getOrden() {
		return orden;
	}

	/**
	 * Establece la posición del número dentro del espectáculo.
	 *
	 * @param orden el orden de aparición a asignar
	 */
	public void setOrden(int orden) {
		this.orden = orden;
	}

	/**
	 * Obtiene el nombre del número artístico.
	 *
	 * @return el nombre del número
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del número artístico.
	 *
	 * @param nombre el nombre a asignar
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene la duración del número en minutos.
	 *
	 * @return la duración en formato x.0 o x.5
	 */
	public double getDuracion() {
		return duracion;
	}

	/**
	 * Establece la duración del número en minutos.
	 *
	 * @param duracion la duración a asignar (formato x.0 o x.5)
	 */
	public void setDuracion(double duracion) {
		this.duracion = duracion;
	}
}