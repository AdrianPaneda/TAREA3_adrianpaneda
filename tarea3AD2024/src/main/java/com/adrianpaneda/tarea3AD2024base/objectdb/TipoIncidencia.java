package com.adrianpaneda.tarea3AD2024base.objectdb;

/**
 * Enumeración de los tipos de incidencias que pueden registrarse en el sistema
 * del circo, persistidas en la base de datos ObjectDB.
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Incidencia
 */
public enum TipoIncidencia {

	/** Incidencia relacionada con equipamiento, infraestructura o logística. */
	TECNICA,

	/** Incidencia relacionada con los artistas o los números del espectáculo. */
	ARTISTICA,

	/** Incidencia relacionada con la organización o coordinación del circo. */
	ORGANIZATIVA
}