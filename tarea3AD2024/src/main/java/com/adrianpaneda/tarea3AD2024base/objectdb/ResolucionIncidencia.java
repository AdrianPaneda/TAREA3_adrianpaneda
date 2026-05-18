package com.adrianpaneda.tarea3AD2024base.objectdb;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * Entidad que documenta la resolución de una incidencia del circo.
 * <p>
 * Cada vez que un coordinador o administrador resuelve una incidencia, se crea
 * un objeto ResolucionIncidencia asociado que registra las acciones realizadas,
 * la fecha y hora de resolución y la persona que la resolvió.
 * </p>
 * <p>
 * La relación con Incidencia es unidireccional: la resolución conoce a su
 * incidencia, pero la incidencia no tiene referencia directa a la resolución.
 * Ambas entidades se persisten exclusivamente en ObjectDB.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Incidencia
 */
@Entity
public class ResolucionIncidencia {

	/** Identificador único de la resolución (generado automáticamente). */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** Fecha y hora en que se resuelve la incidencia (automático). */
	private Date fechahoraResolucion;

	/** Descripción de las acciones realizadas para resolver la incidencia. */
	private String accionesRealizadas;

	/**
	 * Identificador de la persona que resuelve la incidencia.
	 * <p>
	 * Referencia lógica al id de la entidad Persona en MySQL, sin relación directa
	 * entre bases de datos.
	 * </p>
	 */
	private Long idPersonaResuelve;

	/**
	 * Incidencia asociada a esta resolución (relación unidireccional).
	 * <p>
	 * La resolución conoce a su incidencia, pero la incidencia no tiene referencia
	 * a la resolución. Ambas entidades conviven en ObjectDB.
	 * </p>
	 */
	@OneToOne
	private Incidencia incidencia;

	/**
	 * Constructor por defecto requerido por JPA para la instanciación de entidades.
	 */
	public ResolucionIncidencia() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFechahoraResolucion() {
		return fechahoraResolucion;
	}

	public void setFechahoraResolucion(Date fechahoraResolucion) {
		this.fechahoraResolucion = fechahoraResolucion;
	}

	public String getAccionesRealizadas() {
		return accionesRealizadas;
	}

	public void setAccionesRealizadas(String accionesRealizadas) {
		this.accionesRealizadas = accionesRealizadas;
	}

	public Long getIdPersonaResuelve() {
		return idPersonaResuelve;
	}

	public void setIdPersonaResuelve(Long idPersonaResuelve) {
		this.idPersonaResuelve = idPersonaResuelve;
	}

	public Incidencia getIncidencia() {
		return incidencia;
	}

	public void setIncidencia(Incidencia incidencia) {
		this.incidencia = incidencia;
	}
}