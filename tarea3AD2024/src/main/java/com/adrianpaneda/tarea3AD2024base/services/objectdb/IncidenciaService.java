package com.adrianpaneda.tarea3AD2024base.services.objectdb;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.objectdb.Incidencia;
import com.adrianpaneda.tarea3AD2024base.objectdb.ResolucionIncidencia;
import com.adrianpaneda.tarea3AD2024base.objectdb.TipoIncidencia;
import com.adrianpaneda.tarea3AD2024base.repositorios.objectdb.IncidenciaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * Servicio para la gestión de incidencias del circo persistidas en ObjectDB.
 * <p>
 * Proporciona la lógica de negocio para registrar nuevas incidencias (CU8),
 * resolver incidencias existentes (CU9) y consultar incidencias aplicando
 * filtros combinados (CU11).
 * </p>
 * <p>
 * Delega las operaciones JPA puras al {@link IncidenciaRepository} y se encarga
 * de la gestión transaccional (begin, commit, rollback), las validaciones de
 * negocio y la asignación de campos automáticos.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see IncidenciaRepository
 * @see Incidencia
 * @see ResolucionIncidencia
 */
@Service
public class IncidenciaService {

	@Autowired
	private IncidenciaRepository incidenciaRepository;

	/**
	 * Registra una nueva incidencia en ObjectDB (CU8).
	 * <p>
	 * Completa automáticamente el id, la fecha y hora y el estado (resuelta=false).
	 * La operación se realiza dentro de una transacción JPA.
	 * </p>
	 *
	 * @param incidencia la incidencia a registrar (debe traer tipo, descripcion,
	 *                   idPersonaReporta y opcionalmente idEspectaculo/idNumero)
	 * @return la incidencia persistida con su id generado
	 */
	public Incidencia registrar(Incidencia incidencia) {
		EntityManager em = incidenciaRepository.crearEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			incidencia.setFechaHora(new Date());
			incidencia.setResuelta(false);

			incidenciaRepository.guardar(em, incidencia);

			tx.commit();
			return incidencia;
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw new RuntimeException("Error al registrar incidencia: " + e.getMessage(), e);
		} finally {
			em.close();
		}
	}

	/**
	 * Resuelve una incidencia existente (CU9).
	 * <p>
	 * Marca la incidencia como resuelta y persiste un nuevo objeto
	 * ResolucionIncidencia asociado. La operación es transaccional: ambas entidades
	 * se actualizan/insertan en la misma transacción, garantizando la coherencia de
	 * datos.
	 * </p>
	 *
	 * @param idIncidencia       el id de la incidencia a resolver
	 * @param accionesRealizadas la descripción de las acciones realizadas
	 * @param idPersonaResuelve  el id de la persona que resuelve
	 * @return la resolución persistida
	 * @throws IllegalArgumentException si la incidencia no existe o ya está
	 *                                  resuelta
	 */
	public ResolucionIncidencia resolver(Long idIncidencia, String accionesRealizadas, Long idPersonaResuelve) {
		EntityManager em = incidenciaRepository.crearEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			// Buscar y validar
			Incidencia incidencia = incidenciaRepository.buscarPorId(em, idIncidencia);
			if (incidencia == null) {
				throw new IllegalArgumentException("La incidencia con ID " + idIncidencia + " no existe");
			}
			if (incidencia.isResuelta()) {
				throw new IllegalArgumentException("La incidencia ya está resuelta");
			}

			// Marcar como resuelta
			incidencia.setResuelta(true);

			// Crear resolución asociada
			ResolucionIncidencia resolucion = new ResolucionIncidencia();
			resolucion.setFechahoraResolucion(new Date());
			resolucion.setAccionesRealizadas(accionesRealizadas);
			resolucion.setIdPersonaResuelve(idPersonaResuelve);
			resolucion.setIncidencia(incidencia);
			incidenciaRepository.guardarResolucion(em, resolucion);

			tx.commit();
			return resolucion;
		} catch (IllegalArgumentException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw new RuntimeException("Error al resolver incidencia: " + e.getMessage(), e);
		} finally {
			em.close();
		}
	}

	/**
	 * Consulta incidencias aplicando filtros combinados (CU11).
	 *
	 * @param tipo          el tipo de incidencia, o {@code null} para no filtrar
	 * @param resuelta      el estado, o {@code null} para todas
	 * @param idEspectaculo el id del espectáculo, o {@code null} para no filtrar
	 * @param idNumero      el id del número, o {@code null} para no filtrar
	 * @param fechaInicio   fecha inicial del rango, o {@code null}
	 * @param fechaFin      fecha final del rango, o {@code null}
	 * @return lista de incidencias que cumplen los criterios
	 */
	public List<Incidencia> consultarConFiltros(TipoIncidencia tipo, Boolean resuelta, Long idEspectaculo,
			Long idNumero, Date fechaInicio, Date fechaFin) {
		EntityManager em = incidenciaRepository.crearEntityManager();

		try {
			return incidenciaRepository.consultarConFiltros(em, tipo, resuelta, idEspectaculo, idNumero, fechaInicio,
					fechaFin);
		} finally {
			em.close();
		}
	}

	/**
	 * Obtiene todas las incidencias registradas en el sistema.
	 *
	 * @return lista de todas las incidencias ordenadas por fecha descendente
	 */
	public List<Incidencia> obtenerTodas() {
		EntityManager em = incidenciaRepository.crearEntityManager();

		try {
			return incidenciaRepository.obtenerTodas(em);
		} finally {
			em.close();
		}
	}

	/**
	 * Busca una incidencia por su identificador.
	 *
	 * @param id el identificador de la incidencia
	 * @return la incidencia encontrada, o {@code null} si no existe
	 */
	public Incidencia buscarPorId(Long id) {
		EntityManager em = incidenciaRepository.crearEntityManager();

		try {
			return incidenciaRepository.buscarPorId(em, id);
		} finally {
			em.close();
		}
	}
}