package com.adrianpaneda.tarea3AD2024base.config.existDB;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

@Component
public class ExistDBManager {

	@Value("${existdb.uri}")
	private String uri;

	@Value("${existdb.user}")
	private String user;

	@Value("${existdb.password}")
	private String password;

	public void inicializarDriver() {

		try {
			// Creamos la clase para cargar el driver.
			Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
			// CRemaos instancia de database
			Database database = (Database) cl.newInstance();
			// Ponemos la propiedad de createdatabase a true para en casoo de que no exista
			// se cree.
			database.setProperty("create-database", "true");
			// La registramos.
			DatabaseManager.registerDatabase(database);

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException e) {

			e.printStackTrace();
		}

	}

	public Collection getOrCreateCollection() {

		Collection col = null;

		try {
			col = DatabaseManager.getCollection(uri, user, password);
			if (col == null) {
				Collection root = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc/db", user,
						password);
				CollectionManagementService mgtService = (CollectionManagementService) root
						.getService("CollectionManagementService", "1.0");
				mgtService.createCollection("Informes");
				root.close();
				// Ahora conectamos a la colección recién creada
				col = DatabaseManager.getCollection(uri, user, password);
			}

		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return col;

	}

	public void storeDocument(String nombreFichero, String XML) {

		Collection col = getOrCreateCollection();
		File file = new File(nombreFichero);
		XMLResource res = null;
		try {
			res = (XMLResource) col.createResource(file.getName(), "XMLResource");
			res.setContent(XML);
			col.storeResource(res);

		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			/// limpiar resultados
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}

		}

	}
}
