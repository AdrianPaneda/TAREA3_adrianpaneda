package com.adrianpaneda.tarea3AD2024base.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Gestor de la conexión a la base de datos ObjectDB para la gestión de
 * incidencias del circo.
 * <p>
 * Encapsula el EntityManagerFactory de ObjectDB para evitar que Spring Boot lo
 * detecte y desactive la auto-configuración de JPA para Hibernate/MySQL.
 * </p>
 * <p>
 * Sigue un patrón similar al de DB4OConnection: una sola instancia que
 * proporciona EntityManagers bajo demanda. La URL de conexión se obtiene del
 * fichero application.properties.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Component
public class ObjectDBConnection {

	@Value("${objectdb.url}")
	private String url;

	private EntityManagerFactory emf;

	/**
	 * Inicializa el EntityManagerFactory al arrancar la aplicación.
	 * <p>
	 * Según el manual de ObjectDB, una URL que empieza por {@code objectdb://} se
	 * interpreta directamente como URL de conexión, sin necesidad de unidad de
	 * persistencia.
	 * </p>
	 */
	@PostConstruct
	public void inicializar() {
		Map<String, String> properties = new HashMap<>();
		properties.put("jakarta.persistence.provider", "com.objectdb.jpa.Provider");
		emf = Persistence.createEntityManagerFactory(url, properties);
	}

	/**
	 * Crea un nuevo EntityManager para realizar operaciones sobre ObjectDB.
	 *
	 * @return un nuevo EntityManager (debe cerrarse tras su uso)
	 */
	public EntityManager crearEntityManager() {
		return emf.createEntityManager();
	}

	/**
	 * Cierra el EntityManagerFactory al cerrar la aplicación, liberando los
	 * recursos asociados.
	 */
	@PreDestroy
	public void cerrar() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}
}