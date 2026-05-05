package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.services.ArtistaService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador para la pantalla de ficha de artista (CU6).
 * <p>
 * Permite a un artista autenticado visualizar su información personal,
 * profesional y su trayectoria en el circo (espectáculos y números en los que
 * ha participado).
 * </p>
 * <p>
 * Valida el acceso al cargar la pantalla: PT-61: invitado no puede acceder.
 * PT-60: solo artistas pueden acceder.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class ArtistaController implements Initializable {

	// Labels datos personales
	@FXML
	private Label lblNombre;

	@FXML
	private Label lblEmail;

	@FXML
	private Label lblNacionalidad;

	@FXML
	private Label lblApodo;

	@FXML
	private Label lblEspecialidades;

	// Tabla trayectoria
	@FXML
	private TableView<Numero> tablaTrayectoria;

	@FXML
	private TableColumn<Numero, String> colEspectaculo;

	@FXML
	private TableColumn<Numero, String> colNumero;

	// Botones
	@FXML
	private Button btnVerEspectaculos;

	@FXML
	private Button btnCerrarSesion;

	@Autowired
	private ArtistaService artistaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	private ObservableList<Numero> listaTrayectoria = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// PT-61: Invitado no puede acceder
		// PT-60: Solo artistas pueden acceder
		if (!validarAcceso()) {
			return;
		}

		// Obtener el nombreUsuario de la sesión activa
		String nombreUsuario = SessionManager.getCurrentUser().getNombreUsuario();

		// Buscar el Artista en BD por su nombreUsuario
		Optional<Artista> artistaOpt = artistaService.obtenerPorNombreUsuario(nombreUsuario);

		if (artistaOpt.isEmpty()) {
			stageManager.switchScene(FxmlView.LOGIN);
			return;
		}

		Artista artista = artistaOpt.get();

		// Cargar datos en pantalla
		cargarDatosPersonales(artista);
		configurarColumnas();
		cargarTrayectoria(artista);
	}

	/**
	 * Valida que el usuario tenga permiso para acceder a esta pantalla.
	 *
	 * @return true si puede acceder, false si debe redirigir al login
	 */
	private boolean validarAcceso() {
		Credenciales user = SessionManager.getCurrentUser();

		if (user == null || user.getPerfil() != Perfil.artista) {
			stageManager.switchScene(FxmlView.LOGIN);
			return false;
		}

		return true;
	}

	/**
	 * Carga los datos personales y profesionales del artista en los labels.
	 *
	 * @param artista el artista cuya información se mostrará
	 */
	private void cargarDatosPersonales(Artista artista) {
		lblNombre.setText(artista.getNombre());
		lblEmail.setText(artista.getEmail());
		lblNacionalidad.setText(artista.getNacionalidad());

		// PT-58: si no tiene apodo mostramos "Sin apodo"
		String apodo = artista.getApodo();
		lblApodo.setText((apodo != null && !apodo.isEmpty()) ? apodo : "Sin apodo");

		// Convertir Set<Especialidad> a texto legible separado por comas
		lblEspecialidades.setText(convertirEspecialidades(artista.getEspecialidades()));
	}

	/**
	 * Convierte el conjunto de especialidades a un texto legible.
	 * <p>
	 * Ejemplo: [ACROBACIA, EQUILIBRISMO] → "Acrobacia, Equilibrismo"
	 * </p>
	 *
	 * @param especialidades el conjunto de especialidades del artista
	 * @return texto con las especialidades separadas por comas
	 */
	private String convertirEspecialidades(Set<Especialidad> especialidades) {
		if (especialidades == null || especialidades.isEmpty()) {
			return "Ninguna";
		}

		return especialidades.stream()
				.map(e -> e.name().substring(0, 1).toUpperCase() + e.name().substring(1).toLowerCase())
				.collect(Collectors.joining(", "));
	}

	/**
	 * Configura las columnas de la tabla de trayectoria.
	 */
	private void configurarColumnas() {
		// Columna Espectáculo: propiedad anidada, requiere lambda
		colEspectaculo.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getEspectaculo().getNombre()));

		// Columna Número: propiedad simple
		colNumero.setCellValueFactory(new PropertyValueFactory<>("nombre"));
	}

	/**
	 * Carga la trayectoria del artista en la tabla.
	 * <p>
	 * muestra espectáculos y números en los que participó. si no tiene
	 * participaciones, la tabla queda vacía pero legible.
	 * </p>
	 *
	 * @param artista el artista cuya trayectoria se cargará
	 */
	private void cargarTrayectoria(Artista artista) {
		listaTrayectoria.clear();
		List<Numero> numeros = artistaService.obtenerNumerosPorArtista(artista.getId());
		listaTrayectoria.addAll(numeros);
		tablaTrayectoria.setItems(listaTrayectoria);
	}

	/**
	 * Maneja el evento de click en el botón "Ver Espectáculos".
	 * <p>
	 * Navega a la pantalla de listado de espectáculos.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleVerEspectaculos(ActionEvent event) {
		stageManager.switchScene(FxmlView.ESPECTACULOS);
	}

	/**
	 * Maneja el evento de click en el botón "Cerrar Sesión".
	 * <p>
	 * Cierra la sesión del usuario y regresa al login.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleCerrarSesion(ActionEvent event) {
		SessionManager.logout();
		stageManager.switchScene(FxmlView.LOGIN);
	}
}
