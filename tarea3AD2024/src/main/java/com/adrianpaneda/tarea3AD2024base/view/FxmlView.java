package com.adrianpaneda.tarea3AD2024base.view;

import java.util.ResourceBundle;

/**
 * Enumeración que define todas las vistas (pantallas) disponibles en la
 * aplicación.
 * <p>
 * Cada vista tiene asociado un archivo FXML y un título que se muestra en la
 * ventana. Los títulos se obtienen desde el archivo Bundle.properties para
 * facilitar la internacionalización.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
public enum FxmlView {

	/**
	 * Pantalla de autenticación (login). CU2 - Login/Logout.
	 */
	LOGIN {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("login.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/Login.fxml";
		}
	},

	/**
	 * Pantalla principal que lista todos los espectáculos del circo. CU1 - Ver
	 * espectáculos.
	 */
	ESPECTACULOS {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("espectaculos.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/Espectaculos.fxml";
		}
	},

	/**
	 * Pantalla de gestión de personas (artistas y coordinaciones). CU3 - Gestionar
	 * personas. Solo accesible para el administrador.
	 */
	PERSONAS {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("personas.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/Personas.fxml";
		}
	},

	/**
	 * Pantalla de detalle de un espectáculo mostrando sus números. CU4 - Ver
	 * detalle de espectáculo.
	 */
	DETALLE_ESPECTACULO {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("detalleEspectaculo.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/DetalleEspectaculo.fxml";
		}
	},

	/**
	 * Pantalla de gestión completa de espectáculos (crear, modificar, asignar
	 * números). CU5 - Gestionar espectáculos. Accesible para coordinación y
	 * administrador.
	 */
	GESTIONAR_ESPECTACULOS {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("gestionarEspectaculos.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionarEspectaculos.fxml";
		}
	},

	/**
	 * Pantalla que muestra la ficha completa de un artista. CU6 - Ver ficha de
	 * artista.
	 */
	FICHA_ARTISTA {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("fichaArtista.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/FichaArtista.fxml";
		}
	};

	/**
	 * Obtiene el título de la ventana para esta vista.
	 *
	 * @return el título de la ventana
	 */
	public abstract String getTitle();

	/**
	 * Obtiene la ruta al archivo FXML de esta vista.
	 *
	 * @return la ruta al archivo FXML (relativa a resources)
	 */
	public abstract String getFxmlFile();

	/**
	 * Obtiene un texto del archivo de recursos Bundle.properties.
	 *
	 * @param key la clave del recurso a obtener
	 * @return el texto asociado a la clave
	 */
	String getStringFromResourceBundle(String key) {
		return ResourceBundle.getBundle("Bundle").getString(key);
	}
}