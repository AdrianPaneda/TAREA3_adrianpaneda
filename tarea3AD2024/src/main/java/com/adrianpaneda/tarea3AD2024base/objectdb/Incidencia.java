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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(Date fechaHora) {
		this.fechaHora = fechaHora;
	}

	public TipoIncidencia getTipo() {
		return tipo;
	}

	public void setTipo(TipoIncidencia tipo) {
		this.tipo = tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isResuelta() {
		return resuelta;
	}

	public void setResuelta(boolean resuelta) {
		this.resuelta = resuelta;
	}

	public Long getIdPersonaReporta() {
		return idPersonaReporta;
	}

	public void setIdPersonaReporta(Long idPersonaReporta) {
		this.idPersonaReporta = idPersonaReporta;
	}

	public Long getIdEspectaculo() {
		return idEspectaculo;
	}

	public void setIdEspectaculo(Long idEspectaculo) {
		this.idEspectaculo = idEspectaculo;
	}

	public Long getIdNumero() {
		return idNumero;
	}

	public void setIdNumero(Long idNumero) {
		this.idNumero = idNumero;
	}
}