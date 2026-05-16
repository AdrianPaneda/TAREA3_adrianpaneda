package com.adrianpaneda.tarea3AD2024base.config.db4o;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

/**
 * Clase singleton encargada de gestionar la conexión a la base de datos
 * embebida DB4O utilizada para el historial de operaciones del sistema.
 * <p>
 * El fichero de base de datos se almacena en la ruta {@code ficheros/log.db4o}
 * relativa al directorio raíz del proyecto, tal como exige la especificación de
 * la tarea.
 * </p>
 * <p>
 * Sigue el patrón Singleton para garantizar una única instancia de conexión
 * durante el ciclo de vida de la aplicación, evitando bloqueos del fichero al
 * abrirse desde múltiples puntos del código.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
public class DB4OConnection {

	/** Ruta del fichero de base de datos DB4O. */
	private static final String RUTA_BD = "ficheros/log.db4o";

	/** Instancia única de la conexión (Singleton). */
	private static DB4OConnection instancia;

	/** Contenedor de objetos de DB4O para realizar operaciones de persistencia. */
	private ObjectContainer contenedor;

	/**
	 * Constructor privado que impide la instanciación externa de la clase. Abre el
	 * contenedor de DB4O sobre el fichero {@code log.db4o}.
	 */
	private DB4OConnection() {
		// Crea una configuracion por defecto db4o.
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();

		// Abre el fichero de la ruta, si no existe lo crea.
		contenedor = Db4oEmbedded.openFile(config, RUTA_BD);
	}

	/**
	 * Devuelve la instancia única de la conexión a DB4O. Si no existe, la crea.
	 *
	 * @return la instancia única de {@link DB4OConnection}
	 */
	public static synchronized DB4OConnection getInstancia() {
		if (instancia == null) {
			instancia = new DB4OConnection();
		}
		return instancia;
	}

	/**
	 * Obtiene el contenedor de objetos de DB4O para realizar operaciones de
	 * lectura, escritura, actualización o borrado.
	 *
	 * @return el contenedor de objetos de DB4O
	 */
	public ObjectContainer getContenedor() {
		return contenedor;
	}

	/**
	 * Cierra la conexión con la base de datos DB4O. Debe llamarse al finalizar la
	 * aplicación para liberar el fichero y evitar bloqueos.
	 */
	public void cerrar() {
		if (contenedor != null && !contenedor.ext().isClosed()) {
			contenedor.close();
		}
		instancia = null;
	}
}