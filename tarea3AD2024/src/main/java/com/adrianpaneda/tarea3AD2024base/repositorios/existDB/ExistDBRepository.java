package com.adrianpaneda.tarea3AD2024base.repositorios.existDB;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import com.adrianpaneda.tarea3AD2024base.config.existDB.ExistDBManager;

@Repository
public class ExistDBRepository {

	@Autowired
	ExistDBManager existDBM;

	public void storeDocument(String nombreFichero, String XML) {

		Collection col = existDBM.getOrCreateCollection();
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
