package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.services.EspectaculoService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador para la pantalla de listado de espectáculos.
 * <p>
 * Muestra todos los espectáculos registrados en el sistema ordenados por fecha
 * de inicio (más recientes primero). Accesible para todos los usuarios
 * (invitado y autenticados).
 * </p>
 * <p>
 * El botón de navegación es dinámico: redirige a la pantalla principal de cada
 * perfil (ficha artista, gestionar espectáculos o personas) o al login si es
 * invitado.
 * </p>
 * <p>
 * Los usuarios autenticados disponen de una columna adicional con un botón "Ver
 * Detalle" en cada fila para acceder al detalle completo del espectáculo (CU4).
 * Esta columna no se muestra a los usuarios invitados.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class EspectaculosController implements Initializable {

	@FXML
	private TableView<Espectaculo> tablaEspectaculos;

	@FXML
	private TableColumn<Espectaculo, String> colNombre;

	@FXML
	private TableColumn<Espectaculo, String> colFechaInicio;

	@FXML
	private TableColumn<Espectaculo, String> colFechaFin;

	@FXML
	private Button btnVolver;

	@Autowired
	private EspectaculoService espectaculoService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	private ObservableList<Espectaculo> listaEspectaculos = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configurarColumnas();
		cargarEspectaculos();
		configurarBotonVolver();

		// La columna de detalle se añade aquí porque en configurarColumnas()
		// el controller aún no tiene la sesión correctamente inicializada
		// al ser un singleton de Spring creado al arrancar la app
		if (SessionManager.isLoggedIn() && tablaEspectaculos.getColumns().size() == 3) {
			añadirColumnaDetalle();
		}
	}

	/**
	 * Configura las columnas fijas de la tabla vinculándolas con las propiedades de
	 * la entidad Espectaculo.
	 */
	private void configurarColumnas() {
		colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
		colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));

		System.out.println("¿Logueado? " + SessionManager.isLoggedIn());
		System.out.println("Usuario: " + SessionManager.getCurrentUsername());

		if (SessionManager.isLoggedIn()) {
			System.out.println("Añadiendo columna detalle...");
			// ...
		}
	}

	/**
	 * Añade dinámicamente la columna "Ver Detalle" a la tabla de espectáculos.
	 * <p>
	 * Esta columna solo se añade cuando hay un usuario autenticado. Cada fila
	 * contiene un botón que guarda el ID del espectáculo en sesión y navega a la
	 * pantalla de detalle (CU4). Se añade desde {@link #initialize} y no desde
	 * {@link #configurarColumnas} porque el controller es un singleton de Spring y
	 * la sesión no está disponible en el momento de su creación.
	 * </p>
	 */
	private void añadirColumnaDetalle() {
		TableColumn<Espectaculo, Void> colDetalle = new TableColumn<>("Detalle");
		colDetalle.setPrefWidth(200.0);
		colDetalle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER;");

		colDetalle.setCellFactory(param -> new TableCell<>() {
			private final Button btn = new Button("Ver Detalle");
			{
				btn.setStyle(
						"-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 12 6 12; -fx-font-weight: bold; -fx-cursor: hand;");
				btn.setOnAction(event -> {
					Espectaculo espectaculo = getTableView().getItems().get(getIndex());
					handleVerDetalle(espectaculo);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : btn);
			}
		});

		tablaEspectaculos.getColumns().add(colDetalle);
	}

	/**
	 * Carga todos los espectáculos desde la base de datos y los muestra en la
	 * tabla.
	 */
	private void cargarEspectaculos() {
		listaEspectaculos.clear();
		listaEspectaculos.addAll(espectaculoService.obtenerTodos());

		// Ordenar espectaculos por fecha de inicio
		Collections.sort(listaEspectaculos, (e1, e2) -> e2.getFechaInicio().compareTo(e1.getFechaInicio()));
		tablaEspectaculos.setItems(listaEspectaculos);
	}

	/**
	 * Configura el texto del botón volver según el perfil del usuario activo.
	 * <p>
	 * Artista → "VOLVER A MI FICHA" Coordinación → "VOLVER A GESTIÓN" Admin →
	 * "VOLVER A PERSONAS" Invitado → "VOLVER AL LOGIN"
	 * </p>
	 */
	private void configurarBotonVolver() {
		if (!SessionManager.isLoggedIn()) {
			btnVolver.setText("VOLVER AL LOGIN");
			return;
		}

		Perfil perfil = SessionManager.getCurrentPerfil();

		if (perfil == Perfil.artista) {
			btnVolver.setText("VOLVER A MI FICHA");
		} else if (perfil == Perfil.coordinacion) {
			btnVolver.setText("VOLVER A GESTIÓN");
		} else if (perfil == Perfil.admin) {
			btnVolver.setText("VOLVER A PERSONAS");
		}
	}

	/**
	 * Guarda el ID del espectáculo seleccionado en sesión y navega al detalle.
	 * <p>
	 * Solo accesible para usuarios autenticados (CU4).
	 * </p>
	 *
	 * @param espectaculo el espectáculo cuyo detalle se quiere ver
	 */
	private void handleVerDetalle(Espectaculo espectaculo) {
		SessionManager.setSelectedEspectaculo(espectaculo.getId());
		stageManager.switchScene(FxmlView.DETALLE_ESPECTACULO);
	}

	/**
	 * Maneja el evento de click en el botón "Volver".
	 * <p>
	 * Redirige a la pantalla principal según el perfil del usuario activo: artista
	 * a su ficha, coordinación a gestionar espectáculos, admin a personas e
	 * invitado al login.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleVolver(ActionEvent event) {
		if (!SessionManager.isLoggedIn()) {
			stageManager.switchScene(FxmlView.LOGIN);
			return;
		}

		Perfil perfil = SessionManager.getCurrentPerfil();

		if (perfil == Perfil.artista) {
			stageManager.switchScene(FxmlView.FICHA_ARTISTA);
		} else if (perfil == Perfil.coordinacion) {
			stageManager.switchScene(FxmlView.GESTIONAR_ESPECTACULOS);
		} else if (perfil == Perfil.admin) {
			stageManager.switchScene(FxmlView.PERSONAS);
		}
	}
}