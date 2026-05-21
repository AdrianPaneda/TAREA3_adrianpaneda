package com.adrianpaneda.tarea3AD2024base.objectdb;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidad que representa una incidencia registrada en el sistema del circo.
 * <p>
 * Las incidencias pueden ser de tipo técnico, artístico u organizativo y se
 * asocian opcionalmente a un espectáculo o número concreto. Se persisten
 * exclusivamente en la base de datos ObjectDB, independiente de la base de
 * datos relacional MySQL y de la base de datos embebida DB4O.
 * </p>
 * <p>
 * Las relaciones con entidades existentes (Persona, Espectaculo, Numero) se
 * realizan mediante identificadores lógicos (Long), no mediante referencias
 * directas a objetos persistidos en MySQL, tal como exige la especificación.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see TipoIncidencia
 * @see ResolucionIncidencia
 */
@Entity
public class Incidencia {

	/** Identificador único de la incidencia (generado automáticamente). */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** Fecha y hora en que se registra la incidencia (automático). */
	private Date fechaHora;

	/** Tipo de incidencia (TECNICA, ARTISTICA, ORGANIZATIVA). */
	@Enumerated(EnumType.STRING)
	private TipoIncidencia tipo;

	/** Descripción de la incidencia (hasta 1000 caracteres). */
	private String descripcion;

	/** Indica si la incidencia está resuelta. Por defecto, false. */
	private boolean resuelta;

	/**
	 * Identificador de la persona que reporta la incidencia (obligatorio).
	 * <p>
	 * Referencia lógica al id de la entidad Persona en MySQL, sin relación directa
	 * entre bases de datos.
	 * </p>
	 */
	private Long idPersonaReporta;

	/**
	 * Identificador del espectáculo asociado (opcional).
	 * <p>
	 * Referencia lógica al id de la entidad Espectaculo en MySQL. Puede ser null si
	 * la incidencia no está asociada a ningún espectáculo.
	 * </p>
	 */
	private Long idEspectaculo;

	/**
	 * Identificador del número artístico asociado (opcional).
	 * <p>
	 * Referencia lógica al id de la entidad Numero en MySQL. Puede ser null si la
	 * incidencia no está asociada a ningún número.
	 * </p>
	 */
	private Long idNumero;

	/**
	 * Constructor por defecto requerido por JPA para la instanciación de entidades.
	 */
	public Incidencia() {
	}

	/**
	 * Obtiene el identificador único de la incidencia.
	 *
	 * @return el identificador de la incidencia
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único de la incidencia.
	 *
	 * @param id el identificador a asignar
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene la fecha y hora en que se registró la incidencia.
	 *
	 * @return la fecha y hora de registro
	 */
	public Date getFechaHora() {
		return fechaHora;
	}

	/**
	 * Establece la fecha y hora de registro de la incidencia.
	 *
	 * @param fechaHora la fecha y hora a asignar
	 */
	public void setFechaHora(Date fechaHora) {
		this.fechaHora = fechaHora;
	}

	/**
	 * Obtiene el tipo de incidencia.
	 *
	 * @return el tipo (TECNICA, ARTISTICA u ORGANIZATIVA)
	 */
	public TipoIncidencia getTipo() {
		return tipo;
	}

	/**
	 * Establece el tipo de incidencia.
	 *
	 * @param tipo el tipo a asignar
	 */
	public void setTipo(TipoIncidencia tipo) {
		this.tipo = tipo;
	}

	/**
	 * Obtiene la descripción de la incidencia.
	 *
	 * @return la descripción (hasta 1000 caracteres)
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece la descripción de la incidencia.
	 *
	 * @param descripcion la descripción a asignar (hasta 1000 caracteres)
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Indica si la incidencia está resuelta.
	 *
	 * @return {@code true} si la incidencia ha sido resuelta
	 */
	public boolean isResuelta() {
		return resuelta;
	}

	/**
	 * Establece el estado de resolución de la incidencia.
	 *
	 * @param resuelta {@code true} para marcarla como resuelta
	 */
	public void setResuelta(boolean resuelta) {
		this.resuelta = resuelta;
	}

	/**
	 * Obtiene el identificador de la persona que reporta la incidencia.
	 *
	 * @return el identificador de la persona que reporta
	 */
	public Long getIdPersonaReporta() {
		return idPersonaReporta;
	}

	/**
	 * Establece el identificador de la persona que reporta la incidencia.
	 *
	 * @param idPersonaReporta el identificador a asignar
	 */
	public void setIdPersonaReporta(Long idPersonaReporta) {
		this.idPersonaReporta = idPersonaReporta;
	}

	/**
	 * Obtiene el identificador del espectáculo asociado a la incidencia.
	 *
	 * @return el identificador del espectáculo, o {@code null} si no aplica
	 */
	public Long getIdEspectaculo() {
		return idEspectaculo;
	}

	/**
	 * Establece el identificador del espectáculo asociado a la incidencia.
	 *
	 * @param idEspectaculo el identificador a asignar, puede ser {@code null}
	 */
	public void setIdEspectaculo(Long idEspectaculo) {
		this.idEspectaculo = idEspectaculo;
	}

	/**
	 * Obtiene el identificador del número artístico asociado a la incidencia.
	 *
	 * @return el identificador del número, o {@code null} si no aplica
	 */
	public Long getIdNumero() {
		return idNumero;
	}

	/**
	 * Establece el identificador del número artístico asociado a la incidencia.
	 *
	 * @param idNumero el identificador a asignar, puede ser {@code null}
	 */
	public void setIdNumero(Long idNumero) {
		this.idNumero = idNumero;
	}
}