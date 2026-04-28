package com.adrianpaneda.tarea3AD2024base.modelo;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa un espectáculo organizado por el circo.
 * <p>
 * Un espectáculo tiene un nombre único, fechas de inicio y fin, y está dirigido
 * por una persona de coordinación. Está compuesto por un mínimo de 3 números
 * artísticos.
 * </p>
 * <p>
 * La duración máxima de un espectáculo es de 1 año desde su fecha de inicio.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Coordinacion
 * @see Numero
 */
@Entity
@Table(name = "espectaculo")
public class Espectaculo {

	/**
	 * Identificador único del espectáculo, generado automáticamente por la base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Nombre del espectáculo. Debe ser único en el sistema y tener un máximo de 25
	 * caracteres.
	 */
	@Column(unique = true, nullable = false, length = 25)
	private String nombre;

	/**
	 * Fecha de inicio del espectáculo.
	 */
	@Column(nullable = false)
	private LocalDate fechaInicio;

	/**
	 * Fecha de fin del espectáculo. No puede exceder 1 año desde la fecha de
	 * inicio.
	 */
	@Column(nullable = false)
	private LocalDate fechaFin;

	/**
	 * Lista de números artísticos que componen este espectáculo. Un espectáculo
	 * debe tener un mínimo de 3 números.
	 */
	@OneToMany(mappedBy = "espectaculo", fetch = FetchType.LAZY)
	private List<Numero> numeros;

	/**
	 * Obtiene la lista de números que componen este espectáculo.
	 *
	 * @return lista de números artísticos
	 */
	public List<Numero> getNumeros() {
		return numeros;
	}

	/**
	 * Establece la lista de números que componen este espectáculo.
	 *
	 * @param numeros la lista de números a asignar (mínimo 3)
	 */
	public void setNumeros(List<Numero> numeros) {
		this.numeros = numeros;
	}

	/**
	 * Persona de coordinación que dirige este espectáculo. La FK
	 * {@code id_coordinacion} se crea en la tabla {@code espectaculo}.
	 */
	@ManyToOne
	@JoinColumn(name = "id_coordinacion")
	private Coordinacion coordinacion;

	/**
	 * Obtiene la persona de coordinación que dirige este espectáculo.
	 *
	 * @return la persona de coordinación
	 */
	public Coordinacion getCoordinacion() {
		return coordinacion;
	}

	/**
	 * Establece la persona de coordinación que dirige este espectáculo.
	 *
	 * @param coordinacion la persona de coordinación a asignar
	 */
	public void setCoordinacion(Coordinacion coordinacion) {
		this.coordinacion = coordinacion;
	}

	/**
	 * Obtiene el identificador único del espectáculo.
	 *
	 * @return el identificador del espectáculo
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único del espectáculo.
	 *
	 * @param id el identificador a asignar
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre del espectáculo.
	 *
	 * @return el nombre del espectáculo
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del espectáculo.
	 *
	 * @param nombre el nombre a asignar (máximo 25 caracteres)
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Obtiene la fecha de inicio del espectáculo.
	 *
	 * @return la fecha de inicio
	 */
	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	/**
	 * Establece la fecha de inicio del espectáculo.
	 *
	 * @param fechaInicio la fecha de inicio a asignar
	 */
	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	/**
	 * Obtiene la fecha de fin del espectáculo.
	 *
	 * @return la fecha de fin
	 */
	public LocalDate getFechaFin() {
		return fechaFin;
	}

	/**
	 * Establece la fecha de fin del espectáculo.
	 *
	 * @param fechaFin la fecha de fin a asignar (máximo 1 año desde inicio)
	 */
	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
}