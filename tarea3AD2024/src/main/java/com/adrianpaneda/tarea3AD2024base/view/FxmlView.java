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
	 * Pantalla de autenticación (login).
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
	 * Pantalla principal que lista todos los espectáculos del circo.
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
	 * Pantalla de detalle de un espectáculo mostrando sus números.
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
	 * números). Accesible para coordinación y administrador.
	 */
	GESTIONAR_ESPECTACULOS {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("fichaGestionEspectaculos.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionEspectaculos.fxml";
		}
	},

	/**
	 * Pantalla de gestión de números de un espectáculo seleccionado. Se accede
	 * desde {@link #GESTIONAR_ESPECTACULOS}.
	 */
	GESTIONAR_NUMEROS {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("fichaGestionNumeros.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionNumeros.fxml";
		}
	},
	/**
	 * Pantalla de gestión de personas (artistas y coordinaciones). Solo accesible
	 * para el administrador.
	 */
	GESTION_PERSONAS {

		@Override
		public String getTitle() {
			return getStringFromResourceBundle("fichaGestionPersonas.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/GestionPersonas.fxml";
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
	},

	/**
	 * Pantalla de consulta del historial de operaciones del sistema (CU10). Solo
	 * accesible para el administrador. Los datos se persisten en DB4O.
	 */
	HISTORIAL {
		@Override
		public String getTitle() {
			return getStringFromResourceBundle("historial.title");
		}

		@Override
		public String getFxmlFile() {
			return "/fxml/Historial.fxml";
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