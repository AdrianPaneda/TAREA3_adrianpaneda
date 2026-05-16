package com.adrianpaneda.tarea3AD2024base.services.db4o;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.config.db4o.DB4OConnection;
import com.adrianpaneda.tarea3AD2024base.modelo.db4o.LogOperacion;
import com.adrianpaneda.tarea3AD2024base.modelo.db4o.TipoOperacion;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

/**
 * Servicio para la gestión del historial de operaciones del sistema persistido
 * en la base de datos orientada a objetos embebida DB4O.
 * <p>
 * Proporciona métodos para registrar automáticamente operaciones de alta,
 * modificación o borrado sobre las entidades principales del sistema (CU7) y
 * para consultar el historial aplicando distintos criterios de filtrado (CU10).
 * </p>
 * <p>
 * Todas las operaciones se realizan de forma transaccional mediante los métodos
 * {@code store}, {@code commit} y {@code rollback} del contenedor de DB4O.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see LogOperacion
 * @see DB4OConnection
 */
@Service
public class LogOperacionService {

	/**
	 * Registra una nueva operación en el historial.
	 * <p>
	 * Genera automáticamente el ID (último ID + 1) y la fecha y hora actual del
	 * sistema. La operación se persiste de forma transaccional: si ocurre algún
	 * error, se hace rollback y no queda nada registrado.
	 * </p>
	 *
	 * @param usuario       el nombre de usuario que realiza la operación
	 * @param tipoOperacion el tipo de operación (NUEVO, ACTUALIZACION, BORRADO)
	 * @param resumen       la descripción breve de la operación
	 */
	public void registrar(String usuario, TipoOperacion tipoOperacion, String resumen) {
		ObjectContainer db = DB4OConnection.getInstancia().getContenedor();

		try {
			LogOperacion log = new LogOperacion();
			log.setId(generarNuevoId(db));
			log.setFechaHora(LocalDateTime.now());
			log.setUsuario(usuario);
			log.setTipoOperacion(tipoOperacion);
			log.setResumen(resumen);

			db.store(log);
			db.commit();
		} catch (Exception e) {
			db.rollback();
			throw new RuntimeException("Error al registrar operación en el log: " + e.getMessage(), e);
		}
	}

	/**
	 * Genera un nuevo identificador para una operación basándose en el último ID
	 * existente en la base de datos.
	 * <p>
	 * Como DB4O no proporciona generación automática de IDs, se calcula manualmente
	 * como el máximo ID existente más uno. Si la base de datos está vacía, devuelve
	 * 1.
	 * </p>
	 *
	 * @param db el contenedor de objetos de DB4O
	 * @return el nuevo identificador único
	 */
	private Long generarNuevoId(ObjectContainer db) {
		ObjectSet<LogOperacion> todos = db.query(LogOperacion.class);
		long maxId = 0;
		for (LogOperacion log : todos) {
			if (log.getId() != null && log.getId() > maxId) {
				maxId = log.getId();
			}
		}
		return maxId + 1;
	}

	/**
	 * Obtiene todas las operaciones registradas en el historial.
	 *
	 * @return lista de todas las operaciones registradas
	 */
	public List<LogOperacion> obtenerTodas() {
		ObjectContainer db = DB4OConnection.getInstancia().getContenedor();
		ObjectSet<LogOperacion> resultado = db.query(LogOperacion.class);
		return resultado;
	}

	/**
	 * Consulta las operaciones de un usuario concreto utilizando Query By Example
	 * (QBE) de DB4O.
	 * <p>
	 * Construye un objeto plantilla con el nombre de usuario y DB4O devuelve todas
	 * las operaciones que coincidan con ese valor.
	 * </p>
	 *
	 * @param usuario el nombre de usuario a buscar
	 * @return lista de operaciones del usuario indicado
	 */
	public List<LogOperacion> consultarPorUsuario(String usuario) {
		ObjectContainer db = DB4OConnection.getInstancia().getContenedor();

		LogOperacion plantilla = new LogOperacion();
		plantilla.setUsuario(usuario);

		ObjectSet<LogOperacion> resultado = db.queryByExample(plantilla);
		return resultado;
	}

	/**
	 * Consulta las operaciones del historial aplicando los filtros indicados
	 * mediante una Native Query de DB4O.
	 * <p>
	 * Permite filtrar simultáneamente por usuario (obligatorio según la
	 * especificación), por uno o varios tipos de operación y por un rango de
	 * fechas. Los parámetros nulos no se aplican como filtro.
	 * </p>
	 *
	 * @param usuario     el nombre de usuario (obligatorio)
	 * @param tipos       lista de tipos de operación a filtrar, o {@code null} para
	 *                    no filtrar por tipo
	 * @param fechaInicio fecha inicial del rango, o {@code null} para no aplicar
	 *                    límite inferior
	 * @param fechaFin    fecha final del rango, o {@code null} para no aplicar
	 *                    límite superior
	 * @return lista de operaciones que cumplen los criterios indicados
	 */
	public List<LogOperacion> consultarConFiltros(String usuario, List<TipoOperacion> tipos, LocalDateTime fechaInicio,
			LocalDateTime fechaFin) {
		ObjectContainer db = DB4OConnection.getInstancia().getContenedor();

		ObjectSet<LogOperacion> resultado = db.query(new Predicate<LogOperacion>() {

			@Override
			public boolean match(LogOperacion log) {
				if (usuario != null && !usuario.isEmpty() && !usuario.equals(log.getUsuario())) {
					return false;
				}
				if (tipos != null && !tipos.isEmpty() && !tipos.contains(log.getTipoOperacion())) {
					return false;
				}
				if (fechaInicio != null && log.getFechaHora().isBefore(fechaInicio)) {
					return false;
				}
				if (fechaFin != null && log.getFechaHora().isAfter(fechaFin)) {
					return false;
				}
				return true;
			}
		});

		return resultado;
	}
}