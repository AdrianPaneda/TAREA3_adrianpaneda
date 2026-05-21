package com.adrianpaneda.tarea3AD2024base.repositorios.objectdb;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.adrianpaneda.tarea3AD2024base.config.ObjectDBConnection;
import com.adrianpaneda.tarea3AD2024base.objectdb.Incidencia;
import com.adrianpaneda.tarea3AD2024base.objectdb.ResolucionIncidencia;
import com.adrianpaneda.tarea3AD2024base.objectdb.TipoIncidencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Repositorio para el acceso a datos de incidencias y resoluciones persistidas
 * en la base de datos ObjectDB.
 * <p>
 * A diferencia de los repositorios JPA para MySQL (que extienden
 * {@code JpaRepository}), este repositorio gestiona manualmente el
 * {@link EntityManager} de ObjectDB, ya que Spring Data JPA no está configurado
 * para ObjectDB en esta aplicación. Los {@code EntityManager} se crean y
 * cierran desde el servicio llamante.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see com.adrianpaneda.tarea3AD2024base.services.objectdb.IncidenciaService
 * @see com.adrianpaneda.tarea3AD2024base.config.ObjectDBConnection
 */
@Repository
public class IncidenciaRepository {

	@Autowired
	private ObjectDBConnection objectDBConnection;

	/**
	 * Crea un nuevo EntityManager para realizar operaciones sobre ObjectDB.
	 * <p>
	 * El llamante es responsable de cerrar el EntityManager tras su uso mediante
	 * {@code em.close()}.
	 * </p>
	 *
	 * @return un nuevo EntityManager conectado a ObjectDB
	 */
	public EntityManager crearEntityManager() {
		return objectDBConnection.crearEntityManager();
	}

	/**
	 * Persiste una nueva incidencia en ObjectDB.
	 *
	 * @param em         el EntityManager con la transacción activa
	 * @param incidencia la incidencia a persistir
	 */
	public void guardar(EntityManager em, Incidencia incidencia) {
		em.persist(incidencia);
	}

	/**
	 * Actualiza una incidencia existente en ObjectDB.
	 *
	 * @param em         el EntityManager con la transacción activa
	 * @param incidencia la incidencia con los datos actualizados
	 * @return la incidencia gestionada tras la operación de merge
	 */
	public Incidencia actualizar(EntityManager em, Incidencia incidencia) {
		return em.merge(incidencia);
	}

	/**
	 * Busca una incidencia por su identificador.
	 *
	 * @param em el EntityManager activo
	 * @param id el identificador de la incidencia
	 * @return la incidencia encontrada, o {@code null} si no existe
	 */
	public Incidencia buscarPorId(EntityManager em, Long id) {
		return em.find(Incidencia.class, id);
	}

	/**
	 * Persiste una nueva resolución de incidencia en ObjectDB.
	 *
	 * @param em         el EntityManager con la transacción activa
	 * @param resolucion la resolución a persistir
	 */
	public void guardarResolucion(EntityManager em, ResolucionIncidencia resolucion) {
		em.persist(resolucion);
	}

	/**
	 * Obtiene todas las incidencias registradas, ordenadas por fecha descendente.
	 *
	 * @param em el EntityManager activo
	 * @return lista de todas las incidencias ordenadas de más reciente a más
	 *         antigua
	 */
	public List<Incidencia> obtenerTodas(EntityManager em) {
		TypedQuery<Incidencia> query = em.createQuery("SELECT i FROM Incidencia i ORDER BY i.fechaHora DESC",
				Incidencia.class);
		return query.getResultList();
	}

	/**
	 * Consulta incidencias aplicando los filtros indicados.
	 * <p>
	 * Construye dinámicamente una consulta JPQL añadiendo solo las cláusulas
	 * {@code WHERE} correspondientes a los parámetros no nulos. El resultado se
	 * ordena por fecha descendente.
	 * </p>
	 *
	 * @param em            el EntityManager activo
	 * @param tipo          tipo de incidencia, o {@code null} para no filtrar
	 * @param resuelta      estado de resolución, o {@code null} para todas
	 * @param idEspectaculo id del espectáculo, o {@code null} para no filtrar
	 * @param idNumero      id del número, o {@code null} para no filtrar
	 * @param fechaInicio   límite inferior de fecha, o {@code null} para sin límite
	 * @param fechaFin      límite superior de fecha, o {@code null} para sin límite
	 * @return lista de incidencias que cumplen los criterios
	 */
	public List<Incidencia> consultarConFiltros(EntityManager em, TipoIncidencia tipo, Boolean resuelta,
			Long idEspectaculo, Long idNumero, Date fechaInicio, Date fechaFin) {

		StringBuilder jpql = new StringBuilder("SELECT i FROM Incidencia i WHERE 1=1");

		if (tipo != null)
			jpql.append(" AND i.tipo = :tipo");
		if (resuelta != null)
			jpql.append(" AND i.resuelta = :resuelta");
		if (idEspectaculo != null)
			jpql.append(" AND i.idEspectaculo = :idEspectaculo");
		if (idNumero != null)
			jpql.append(" AND i.idNumero = :idNumero");
		if (fechaInicio != null)
			jpql.append(" AND i.fechaHora >= :fechaInicio");
		if (fechaFin != null)
			jpql.append(" AND i.fechaHora <= :fechaFin");

		jpql.append(" ORDER BY i.fechaHora DESC");

		TypedQuery<Incidencia> query = em.createQuery(jpql.toString(), Incidencia.class);

		if (tipo != null)
			query.setParameter("tipo", tipo);
		if (resuelta != null)
			query.setParameter("resuelta", resuelta);
		if (idEspectaculo != null)
			query.setParameter("idEspectaculo", idEspectaculo);
		if (idNumero != null)
			query.setParameter("idNumero", idNumero);
		if (fechaInicio != null)
			query.setParameter("fechaInicio", fechaInicio);
		if (fechaFin != null)
			query.setParameter("fechaFin", fechaFin);

		return query.getResultList();
	}

	/**
	 * Obtiene todas las resoluciones de incidencias almacenadas en la base de
	 * datos.
	 *
	 * @param em EntityManager activo para realizar la consulta.
	 * @return Lista con todas las ResolucionIncidencia; lista vacía si no hay
	 *         registros.
	 */
	public List<ResolucionIncidencia> obtenerTodasResoluciones(EntityManager em) {

		TypedQuery<ResolucionIncidencia> query = em.createQuery("SELECT r FROM ResolucionIncidencia r",
				ResolucionIncidencia.class);

		return query.getResultList();
	}

}
