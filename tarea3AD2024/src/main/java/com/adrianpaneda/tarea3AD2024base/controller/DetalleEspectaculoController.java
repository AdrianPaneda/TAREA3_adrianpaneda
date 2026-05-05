package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion;
import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;
import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.services.EspectaculoService;
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
 * Controlador para la pantalla de detalle de espectáculo (CU4).
 * <p>
 * Muestra el detalle completo de un espectáculo seleccionado: datos básicos,
 * coordinación, números y artistas de cada número. Solo accesible para usuarios
 * autenticados.
 * </p>
 * <p>
 * El espectáculo a mostrar se obtiene del ID guardado en {@link SessionManager}
 * al pulsar "Ver Detalle" en la pantalla de espectáculos.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class DetalleEspectaculoController implements Initializable {

	// Labels datos del espectáculo
	@FXML
	private Label lblId;

	@FXML
	private Label lblNombre;

	@FXML
	private Label lblFechaInicio;

	@FXML
	private Label lblFechaFin;

	// Labels coordinación
	@FXML
	private Label lblCoordNombre;

	@FXML
	private Label lblCoordEmail;

	@FXML
	private Label lblCoordSenior;

	// Tabla números
	@FXML
	private TableView<Numero> tablaNumeros;

	@FXML
	private TableColumn<Numero, Long> colNumeroId;

	@FXML
	private TableColumn<Numero, String> colNumeroNombre;

	@FXML
	private TableColumn<Numero, Double> colNumeroDuracion;

	// Tabla artistas
	@FXML
	private TableView<Artista> tablaArtistas;

	@FXML
	private TableColumn<Artista, String> colArtistaNombre;

	@FXML
	private TableColumn<Artista, String> colArtistaNacionalidad;

	@FXML
	private TableColumn<Artista, String> colArtistaEspecialidades;

	@FXML
	private TableColumn<Artista, String> colArtistaApodo;

	// Botón
	@FXML
	private Button btnVolver;

	@Autowired
	private EspectaculoService espectaculoService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	private ObservableList<Numero> listaNumeros = FXCollections.observableArrayList();
	private ObservableList<Artista> listaArtistas = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Obtener ID del espectáculo guardado en sesión
		Long id = SessionManager.getSelectedEspectaculo();

		// Cargar espectáculo con todos sus datos relacionados
		Espectaculo espectaculo = espectaculoService.obtenerConDetalle(id);

		// Aqui por seguridad volvemos a la pantalla Espectaculos por si no encuentra el
		// espectaculo.
		if (espectaculo == null) {
			stageManager.switchScene(FxmlView.ESPECTACULOS);
			return;
		}

		// Cargar datos en pantalla
		cargarDatosEspectaculo(espectaculo);
		cargarCoordinacion(espectaculo.getCoordinacion());
		configurarTablaNumeros();
		configurarTablaArtistas();
		cargarNumeros(espectaculo);
		configurarListenerNumeros();
	}

	/**
	 * Carga los datos básicos del espectáculo en los labels.
	 *
	 * @param espectaculo el espectáculo a mostrar
	 */
	private void cargarDatosEspectaculo(Espectaculo espectaculo) {
		lblId.setText(String.valueOf(espectaculo.getId()));
		lblNombre.setText(espectaculo.getNombre());
		lblFechaInicio.setText(espectaculo.getFechaInicio().toString());
		lblFechaFin.setText(espectaculo.getFechaFin().toString());
	}

	/**
	 * Carga los datos de la coordinación del espectáculo en los labels.
	 *
	 * @param coordinacion la coordinación a mostrar
	 */
	private void cargarCoordinacion(Coordinacion coordinacion) {
		if (coordinacion == null) {
			lblCoordNombre.setText("Sin asignar");
			lblCoordEmail.setText("-");
			lblCoordSenior.setText("-");
			return;
		}

		lblCoordNombre.setText(coordinacion.getNombre());
		lblCoordEmail.setText(coordinacion.getEmail());
		lblCoordSenior.setText(coordinacion.isSenior() ? "Sí" : "No");
	}

	/**
	 * Configura las columnas de la tabla de números.
	 */
	private void configurarTablaNumeros() {
		colNumeroId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colNumeroNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colNumeroDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
	}

	/**
	 * Configura las columnas de la tabla de artistas.
	 */
	private void configurarTablaArtistas() {
		colArtistaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colArtistaNacionalidad.setCellValueFactory(new PropertyValueFactory<>("nacionalidad"));

		// Especialidades: Set<Especialidad> → texto separado por comas
		colArtistaEspecialidades.setCellValueFactory(
				cellData -> new SimpleStringProperty(convertirEspecialidades(cellData.getValue().getEspecialidades())));

		// Apodo: si no tiene, mostrar "Sin apodo"
		colArtistaApodo.setCellValueFactory(cellData -> {
			String apodo = cellData.getValue().getApodo();
			return new SimpleStringProperty((apodo != null && !apodo.isEmpty()) ? apodo : "Sin apodo");
		});
	}

	/**
	 * Carga los números del espectáculo en la tabla ordenados por su campo orden.
	 *
	 * @param espectaculo el espectáculo cuyos números se cargarán
	 */
	private void cargarNumeros(Espectaculo espectaculo) {
		listaNumeros.clear();
		listaNumeros.addAll(espectaculo.getNumeros());
		listaNumeros.sort((n1, n2) -> Integer.compare(n1.getOrden(), n2.getOrden()));
		tablaNumeros.setItems(listaNumeros);
	}

	/**
	 * Configura el listener de selección de la tabla de números.
	 * <p>
	 * Cuando el usuario selecciona un número, la tabla de artistas se actualiza
	 * automáticamente mostrando los artistas que participan en ese número.
	 * </p>
	 */
	private void configurarListenerNumeros() {
		tablaNumeros.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				cargarArtistas(newVal);
			}
		});
	}

	/**
	 * Carga los artistas del número seleccionado en la tabla de artistas.
	 *
	 * @param numero el número cuios artistas se cargarán
	 */
	private void cargarArtistas(Numero numero) {
		listaArtistas.clear();
		listaArtistas.addAll(numero.getArtistas());
		tablaArtistas.setItems(listaArtistas);
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
	 * Maneja el evento de click en el botón "Volver".
	 * <p>
	 * Regresa a la pantalla de listado de espectáculos.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleVolver(ActionEvent event) {
		stageManager.switchScene(FxmlView.ESPECTACULOS);
	}
}