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
 */
@Repository
public class IncidenciaRepository {

	@Autowired
	private ObjectDBConnection objectDBConnection;

	/**
	 * Crea un nuevo EntityManager para operaciones sobre ObjectDB.
	 */
	public EntityManager crearEntityManager() {
		return objectDBConnection.crearEntityManager();
	}

	public void guardar(EntityManager em, Incidencia incidencia) {
		em.persist(incidencia);
	}

	public Incidencia actualizar(EntityManager em, Incidencia incidencia) {
		return em.merge(incidencia);
	}

	public Incidencia buscarPorId(EntityManager em, Long id) {
		return em.find(Incidencia.class, id);
	}

	public void guardarResolucion(EntityManager em, ResolucionIncidencia resolucion) {
		em.persist(resolucion);
	}

	public List<Incidencia> obtenerTodas(EntityManager em) {
		TypedQuery<Incidencia> query = em.createQuery("SELECT i FROM Incidencia i ORDER BY i.fechaHora DESC",
				Incidencia.class);
		return query.getResultList();
	}

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
}