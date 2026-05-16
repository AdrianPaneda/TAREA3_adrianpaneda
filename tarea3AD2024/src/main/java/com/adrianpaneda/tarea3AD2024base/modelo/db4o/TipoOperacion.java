package com.adrianpaneda.tarea3AD2024base.modelo.db4o;

/**
 * Enumeración de los tipos de operaciones registradas en el historial (log) del
 * sistema, persistido en la base de datos embebida DB4O.
 * <p>
 * Cada vez que se realiza una operación sobre las entidades principales del
 * sistema, se registra un objeto {@link LogOperacion} indicando el tipo
 * mediante uno de los valores de esta enumeración.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see LogOperacion
 */
public enum TipoOperacion {

	/** Operación de alta de una nueva entidad. */
	NUEVO,

	/** Operación de modificación de una entidad existente. */
	ACTUALIZACION,

	/** Operación de borrado de una entidad existente. */
	BORRADO

}
