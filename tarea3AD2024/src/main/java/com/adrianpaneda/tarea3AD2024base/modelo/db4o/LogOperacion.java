package com.adrianpaneda.tarea3AD2024base.modelo.db4o;

import java.util.Date;

/**
 * Clase que representa una operación registrada en el historial (log) del
 * sistema. Sus objetos se persisten exclusivamente en la base de datos
 * orientada a objetos embebida DB4O.
 * <p>
 * Cada vez que se realiza una operación de alta, modificación o borrado sobre
 * las entidades principales del sistema (Persona, Espectaculo, Numero...), se
 * registra automáticamente un objeto LogOperacion con los datos de la operación
 * para fines de auditoría y seguimiento.
 * </p>
 * <p>
 * Esta clase NO lleva anotaciones JPA porque su persistencia se gestiona
 * mediante DB4O, no a través de Hibernate.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see TipoOperacion
 */
public class LogOperacion {

	/** Identificador único de la operación registrada. */
	private Long id;

	/** Fecha y hora en que se produce la operación. */
	private Date fechaHora;

	/** Nombre de usuario que realiza la operación. */
	private String usuario;

	/** Tipo de operación realizada (NUEVO, ACTUALIZACION, BORRADO). */
	private TipoOperacion tipoOperacion;

	/**
	 * Descripción breve de la operación realizada. Indica el nombre de la entidad
	 * afectada y su identificador.
	 * <p>
	 * Ejemplos: "Se ha actualizado la información del id 3 de Persona", "Se ha
	 * insertado un nuevo Artista de id 21".
	 * </p>
	 */
	private String resumen;

	/**
	 * Constructor por defecto requerido por DB4O para la deserialización de
	 * objetos.
	 */
	public LogOperacion() {
	}

	/**
	 * Constructor completo para crear un registro de operación.
	 *
	 * @param id            el identificador único
	 * @param fechaHora     la fecha y hora de la operación
	 * @param usuario       el nombre de usuario que realiza la operación
	 * @param tipoOperacion el tipo de operación
	 * @param resumen       la descripción breve de la operación
	 */
	public LogOperacion(Long id, Date fechaHora, String usuario, TipoOperacion tipoOperacion, String resumen) {
		this.id = id;
		this.fechaHora = fechaHora;
		this.usuario = usuario;
		this.tipoOperacion = tipoOperacion;
		this.resumen = resumen;
	}

	/**
	 * Obtiene el identificador único de la operación.
	 *
	 * @return el identificador
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único de la operación.
	 *
	 * @param id el identificador a asignar
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene la fecha y hora en que se produjo la operación.
	 *
	 * @return la fecha y hora de la operación
	 */
	public Date getFechaHora() {
		return fechaHora;
	}

	/**
	 * Establece la fecha y hora de la operación.
	 *
	 * @param fechaHora la fecha y hora a asignar
	 */
	public void setFechaHora(Date fechaHora) {
		this.fechaHora = fechaHora;
	}

	/**
	 * Obtiene el nombre de usuario que realizó la operación.
	 *
	 * @return el nombre de usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * Establece el nombre de usuario que realizó la operación.
	 *
	 * @param usuario el nombre de usuario a asignar
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * Obtiene el tipo de operación realizada.
	 *
	 * @return el tipo de operación
	 */
	public TipoOperacion getTipoOperacion() {
		return tipoOperacion;
	}

	/**
	 * Establece el tipo de operación realizada.
	 *
	 * @param tipoOperacion el tipo de operación a asignar
	 */
	public void setTipoOperacion(TipoOperacion tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	/**
	 * Obtiene el resumen descriptivo de la operación.
	 *
	 * @return el resumen de la operación
	 */
	public String getResumen() {
		return resumen;
	}

	/**
	 * Establece el resumen descriptivo de la operación.
	 *
	 * @param resumen el resumen a asignar
	 */
	public void setResumen(String resumen) {
		this.resumen = resumen;
	}
}