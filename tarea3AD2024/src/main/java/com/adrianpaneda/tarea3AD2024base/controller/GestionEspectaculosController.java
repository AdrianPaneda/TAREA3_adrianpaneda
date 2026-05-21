package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.HelpStageManager;
import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion;
import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.services.CoordinacionService;
import com.adrianpaneda.tarea3AD2024base.services.EspectaculoService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controlador para la pantalla de gestión de espectáculos (CU5A).
 * <p>
 * Permite a usuarios con perfil de Coordinación o Admin crear y modificar
 * espectáculos del circo.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class GestionEspectaculosController implements Initializable {

	@FXML
	private TableView<Espectaculo> tablaEspectaculos;

	@FXML
	private TableColumn<Espectaculo, String> colNombre;

	@FXML
	private TableColumn<Espectaculo, String> colFechaInicio;

	@FXML
	private TableColumn<Espectaculo, String> colFechaFin;

	@FXML
	private TableColumn<Espectaculo, String> colCoordinador;

	@FXML
	private VBox contenedorFormulario;

	@FXML
	private ScrollPane scrollFormulario;

	@FXML
	private VBox panelFormulario;

	@FXML
	private Label lblTituloFormulario;

	@FXML
	private TextField txtNombre;

	@FXML
	private Label lblErrorNombre;

	@FXML
	private DatePicker dateFechaInicio;

	@FXML
	private Label lblErrorFechaInicio;

	@FXML
	private DatePicker dateFechaFin;

	@FXML
	private Label lblErrorFechaFin;

	@FXML
	private VBox panelCoordinador;

	@FXML
	private ComboBox<Coordinacion> comboCoordinador;

	@FXML
	private Label lblErrorCoordinador;

	@FXML
	private Button btnCrear;

	@FXML
	private Button btnVerEspectaculos;

	@FXML
	private Button btnCerrarSesion;

	@FXML
	private Button btnIncidencias;

	@FXML
	private Button btnGuardar;

	@FXML
	private Button btnCancelar;

	@Autowired
	private EspectaculoService espectaculoService;

	@Autowired
	private CoordinacionService coordinacionService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private HelpStageManager helpStageManager;

	private ObservableList<Espectaculo> listaEspectaculos = FXCollections.observableArrayList();

	private enum Modo {
		CREAR, EDITAR
	}

	private Modo modoActual;
	private Espectaculo espectaculoEnEdicion;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		validarAcceso();
		configurarTabla();
		cargarEspectaculos();
		ocultarFormulario();
		configurarBotonVolver();
	}

	/**
	 * Verifica que el usuario tenga perfil de coordinación o administrador.
	 * <p>
	 * Si no cumple el requisito, redirige al login.
	 * </p>
	 */
	private void validarAcceso() {
		Perfil perfil = SessionManager.getCurrentPerfil();
		if (perfil != Perfil.coordinacion && perfil != Perfil.admin) {
			stageManager.switchScene(FxmlView.LOGIN);
		}
	}

	/**
	 * Configura las columnas de la tabla de espectáculos y añade la columna de
	 * acciones (Editar / Números).
	 */
	private void configurarTabla() {
		colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
		colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
		colCoordinador.setCellValueFactory(cellData -> {
			Coordinacion coord = cellData.getValue().getCoordinacion();
			return new SimpleStringProperty(coord != null ? coord.getNombre() : "Sin asignar");
		});
		añadirColumnaAcciones();
	}

	/**
	 * Añade dinámicamente la columna "Acciones" con los botones Editar y Números.
	 */
	private void añadirColumnaAcciones() {
		TableColumn<Espectaculo, Void> colAcciones = new TableColumn<>("Acciones");
		colAcciones.setPrefWidth(220.0);
		colAcciones.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-alignment: CENTER;");

		colAcciones.setCellFactory(param -> new TableCell<>() {
			private final Button btnEditar = new Button("Editar");
			private final Button btnNumeros = new Button("Números");
			private final HBox contenedor = new HBox(8, btnEditar, btnNumeros);

			{
				btnEditar.setStyle(
						"-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 5 10 5 10; -fx-font-weight: bold; -fx-cursor: hand;");
				btnNumeros.setStyle(
						"-fx-background-color: #7c3aed; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 5 10 5 10; -fx-font-weight: bold; -fx-cursor: hand;");
				contenedor.setStyle("-fx-alignment: CENTER;");

				btnEditar.setOnAction(event -> {
					Espectaculo espectaculo = getTableView().getItems().get(getIndex());
					handleEditar(espectaculo);
				});
				btnNumeros.setOnAction(event -> {
					Espectaculo espectaculo = getTableView().getItems().get(getIndex());
					handleNumeros(espectaculo);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : contenedor);
			}
		});

		tablaEspectaculos.getColumns().add(colAcciones);
	}

	/**
	 * Carga todos los espectáculos desde la base de datos y los muestra en la
	 * tabla.
	 */
	private void cargarEspectaculos() {
		listaEspectaculos.clear();
		listaEspectaculos.addAll(espectaculoService.obtenerTodos());
		tablaEspectaculos.setItems(listaEspectaculos);
	}

	/**
	 * Configura el ComboBox de coordinadores con todos los coordinadores del
	 * sistema, mostrando su nombre como etiqueta.
	 */
	private void configurarComboCoordinador() {
		comboCoordinador.setItems(FXCollections.observableArrayList(coordinacionService.obtenerTodas()));
		comboCoordinador.setButtonCell(new ListCell<>() {
			@Override
			protected void updateItem(Coordinacion item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? "Seleccione coordinador" : item.getNombre());
			}
		});
		comboCoordinador.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(Coordinacion item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? null : item.getNombre());
			}
		});
	}

	/**
	 * Maneja el evento de click en el botón "Crear espectáculo".
	 * <p>
	 * Muestra el formulario en modo creación y lo configura con los campos vacíos.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleCrear(ActionEvent event) {
		modoActual = Modo.CREAR;
		espectaculoEnEdicion = null;
		limpiarFormulario();
		mostrarFormulario();
		lblTituloFormulario.setText("CREAR ESPECTÁCULO");
		configurarPanelCoordinador();
	}

	/**
	 * Abre el formulario en modo edición con los datos del espectáculo indicado.
	 *
	 * @param espectaculo el espectáculo cuyos datos se cargarán en el formulario
	 */
	private void handleEditar(Espectaculo espectaculo) {
		modoActual = Modo.EDITAR;
		espectaculoEnEdicion = espectaculo;
		limpiarFormulario();
		mostrarFormulario();
		lblTituloFormulario.setText("EDITAR ESPECTÁCULO");
		configurarPanelCoordinador();

		txtNombre.setText(espectaculo.getNombre());
		dateFechaInicio.setValue(espectaculo.getFechaInicio());
		dateFechaFin.setValue(espectaculo.getFechaFin());

		if (SessionManager.getCurrentPerfil() == Perfil.admin && espectaculo.getCoordinacion() != null) {
			for (Coordinacion c : comboCoordinador.getItems()) {
				if (c.getId().equals(espectaculo.getCoordinacion().getId())) {
					comboCoordinador.setValue(c);
					break;
				}
			}
		}
	}

	/**
	 * Navega a la pantalla de gestión de números del espectáculo seleccionado.
	 * <p>
	 * Si hay una operación sin guardar en el formulario, muestra un aviso y no
	 * navega.
	 * </p>
	 *
	 * @param espectaculo el espectáculo cuyos números se gestionarán
	 */
	private void handleNumeros(Espectaculo espectaculo) {
		if (modoActual != null) {
			Alert aviso = new Alert(AlertType.WARNING);
			aviso.setTitle("Operación en curso");
			aviso.setHeaderText("Tiene una operación sin guardar");
			aviso.setContentText("Debe guardar o cancelar la operación actual antes de continuar.");
			aviso.showAndWait();
			return;
		}
		SessionManager.setSelectedEspectaculo(espectaculo.getId());
		stageManager.switchScene(FxmlView.GESTIONAR_NUMEROS);
	}

	/**
	 * Muestra u oculta el panel de selección de coordinador según el perfil.
	 * <p>
	 * Solo el perfil {@code admin} puede asignar un coordinador al espectáculo.
	 * </p>
	 */
	private void configurarPanelCoordinador() {
		if (SessionManager.getCurrentPerfil() == Perfil.admin) {
			panelCoordinador.setVisible(true);
			panelCoordinador.setManaged(true);
			configurarComboCoordinador();
		} else {
			panelCoordinador.setVisible(false);
			panelCoordinador.setManaged(false);
		}
	}

	/**
	 * Maneja el evento de click en el botón "Guardar".
	 * <p>
	 * Valida el formulario y, si es correcto, ejecuta la operación de creación o
	 * actualización según el modo activo.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleGuardar(ActionEvent event) {
		limpiarErrores();
		if (!validarFormulario()) {
			return;
		}
		if (modoActual == Modo.CREAR) {
			crearEspectaculo();
		} else if (modoActual == Modo.EDITAR) {
			actualizarEspectaculo();
		}
	}

	/**
	 * Crea un nuevo espectáculo con los datos del formulario y lo persiste.
	 *
	 * @throws IllegalArgumentException si alguna validación de negocio falla
	 */
	private void crearEspectaculo() {
		try {
			Espectaculo espectaculo = new Espectaculo();
			espectaculo.setNombre(txtNombre.getText().trim());
			espectaculo.setFechaInicio(dateFechaInicio.getValue());
			espectaculo.setFechaFin(dateFechaFin.getValue());
			asignarCoordinador(espectaculo);
			espectaculoService.validarLongitudNombre(espectaculo.getNombre());
			espectaculoService.validarNombreUnico(espectaculo.getNombre());
			espectaculoService.validarDuracionMaxima(espectaculo.getFechaInicio(), espectaculo.getFechaFin());
			espectaculoService.guardarSinValidarNumeros(espectaculo);
			cargarEspectaculos();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			mostrarErrorGeneral(e.getMessage());
		}
	}

	/**
	 * Actualiza el espectáculo en edición con los datos del formulario y lo
	 * persiste.
	 *
	 * @throws IllegalArgumentException si alguna validación de negocio falla
	 */
	private void actualizarEspectaculo() {
		try {
			espectaculoEnEdicion.setNombre(txtNombre.getText().trim());
			espectaculoEnEdicion.setFechaInicio(dateFechaInicio.getValue());
			espectaculoEnEdicion.setFechaFin(dateFechaFin.getValue());
			asignarCoordinador(espectaculoEnEdicion);
			espectaculoService.validarLongitudNombre(espectaculoEnEdicion.getNombre());
			espectaculoService.validarDuracionMaxima(espectaculoEnEdicion.getFechaInicio(),
					espectaculoEnEdicion.getFechaFin());
			espectaculoService.guardarSinValidarNumeros(espectaculoEnEdicion);
			cargarEspectaculos();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			mostrarErrorGeneral(e.getMessage());
		}
	}

	/**
	 * Asigna el coordinador al espectáculo según el perfil activo.
	 * <p>
	 * El administrador selecciona el coordinador en el ComboBox; la coordinación
	 * se asigna automáticamente a sí misma buscando por su nombre de usuario en
	 * sesión.
	 * </p>
	 *
	 * @param espectaculo el espectáculo al que se asignará el coordinador
	 */
	private void asignarCoordinador(Espectaculo espectaculo) {
		if (SessionManager.getCurrentPerfil() == Perfil.admin) {
			espectaculo.setCoordinacion(comboCoordinador.getValue());
		} else {
			String username = SessionManager.getCurrentUser().getNombreUsuario();
			Coordinacion coord = coordinacionService.obtenerTodas().stream()
					.filter(c -> c.getCredenciales() != null && c.getCredenciales().getNombreUsuario().equals(username))
					.findFirst().orElse(null);
			espectaculo.setCoordinacion(coord);
		}
	}

	/**
	 * Valida los datos introducidos en el formulario de espectáculo.
	 *
	 * @return {@code true} si todos los campos son válidos
	 */
	private boolean validarFormulario() {
		boolean valido = true;
		String nombre = txtNombre.getText().trim();
		if (nombre.isEmpty()) {
			lblErrorNombre.setText("El nombre es obligatorio");
			valido = false;
		} else if (nombre.length() > 25) {
			lblErrorNombre.setText("El nombre no puede exceder 25 caracteres");
			valido = false;
		}
		if (dateFechaInicio.getValue() == null) {
			lblErrorFechaInicio.setText("La fecha de inicio es obligatoria");
			valido = false;
		}
		if (dateFechaFin.getValue() == null) {
			lblErrorFechaFin.setText("La fecha de fin es obligatoria");
			valido = false;
		}
		if (dateFechaInicio.getValue() != null && dateFechaFin.getValue() != null) {
			LocalDate inicio = dateFechaInicio.getValue();
			LocalDate fin = dateFechaFin.getValue();
			if (fin.isBefore(inicio)) {
				lblErrorFechaFin.setText("La fecha de fin no puede ser anterior a la de inicio");
				valido = false;
			} else if (ChronoUnit.DAYS.between(inicio, fin) > 365) {
				lblErrorFechaFin.setText("La duración no puede exceder 1 año (365 días)");
				valido = false;
			}
		}
		if (SessionManager.getCurrentPerfil() == Perfil.admin && comboCoordinador.getValue() == null) {
			lblErrorCoordinador.setText("Debe seleccionar un coordinador");
			valido = false;
		}
		return valido;
	}

	/**
	 * Muestra el formulario y bloquea los botones principales.
	 */
	private void mostrarFormulario() {
		contenedorFormulario.setVisible(true);
		contenedorFormulario.setManaged(true);
		bloquearBotones(true);
	}

	/**
	 * Oculta el formulario y desbloquea los botones principales.
	 */
	private void ocultarFormulario() {
		contenedorFormulario.setVisible(false);
		contenedorFormulario.setManaged(false);
		modoActual = null;
		espectaculoEnEdicion = null;
		bloquearBotones(false);
	}

	/**
	 * Bloquea o desbloquea los botones principales durante la edición.
	 *
	 * @param bloquear true para bloquear, false para desbloquear
	 */
	private void bloquearBotones(boolean bloquear) {
		btnCrear.setDisable(bloquear);
		btnVerEspectaculos.setDisable(bloquear);
		tablaEspectaculos.setDisable(bloquear);
		btnIncidencias.setDisable(bloquear);
	}

	/**
	 * Limpia todos los campos del formulario de espectáculo.
	 */
	private void limpiarFormulario() {
		txtNombre.clear();
		dateFechaInicio.setValue(null);
		dateFechaFin.setValue(null);
		comboCoordinador.setValue(null);
		limpiarErrores();
	}

	/**
	 * Limpia los mensajes de error del formulario.
	 */
	private void limpiarErrores() {
		lblErrorNombre.setText("");
		lblErrorFechaInicio.setText("");
		lblErrorFechaFin.setText("");
		lblErrorCoordinador.setText("");
	}

	/**
	 * Muestra un mensaje de error en el campo del formulario más relacionado con
	 * el contenido del mensaje.
	 *
	 * @param mensaje el mensaje de error a mostrar
	 */
	private void mostrarErrorGeneral(String mensaje) {
		if (mensaje.toLowerCase().contains("nombre")) {
			lblErrorNombre.setText(mensaje);
		} else if (mensaje.toLowerCase().contains("fecha") || mensaje.toLowerCase().contains("duración")) {
			lblErrorFechaFin.setText(mensaje);
		} else {
			lblErrorNombre.setText(mensaje);
		}
	}

	/**
	 * Maneja el evento de click en el botón "Cancelar".
	 * <p>
	 * Oculta el formulario descartando los cambios en curso.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleCancelar(ActionEvent event) {
		ocultarFormulario();
	}

	/**
	 * Maneja el evento de click en el botón "Ver espectáculos".
	 * <p>
	 * Navega a la vista de listado de espectáculos. Si hay una operación en curso,
	 * muestra un aviso y no navega.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleVerEspectaculos(ActionEvent event) {
		if (modoActual != null) {
			Alert aviso = new Alert(AlertType.WARNING);
			aviso.setTitle("Operación en curso");
			aviso.setHeaderText("Tiene una operación sin guardar");
			aviso.setContentText("Debe guardar o cancelar la operación actual antes de continuar.");
			aviso.showAndWait();
			return;
		}
		stageManager.switchScene(FxmlView.ESPECTACULOS);
	}

	/**
	 * Configura el botón inferior según el perfil del usuario.
	 * <p>
	 * Admin → "VOLVER A PERSONAS" (navega a gestión de personas). Coordinación →
	 * "CERRAR SESIÓN" (hace logout).
	 * </p>
	 */
	private void configurarBotonVolver() {
		if (SessionManager.getCurrentPerfil() == Perfil.admin) {
			btnCerrarSesion.setText("VOLVER A PERSONAS");
		}
	}

	/**
	 * Maneja el evento del botón inferior, cuyo comportamiento varía según el
	 * perfil:
	 * <ul>
	 * <li>Coordinación → cierra sesión y vuelve al login.</li>
	 * <li>Admin → navega a gestión de personas.</li>
	 * </ul>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleCerrarSesion(ActionEvent event) {
		if (modoActual != null) {
			Alert aviso = new Alert(AlertType.WARNING);
			aviso.setTitle("Operación en curso");
			aviso.setHeaderText("Tiene una operación sin guardar");
			aviso.setContentText("Debe guardar o cancelar la operación actual antes de continuar.");
			aviso.showAndWait();
			return;
		}

		if (SessionManager.getCurrentPerfil() == Perfil.admin) {
			// Admin → volver a gestión de personas
			stageManager.switchScene(FxmlView.GESTION_PERSONAS);
		} else {
			// Coordinación → cerrar sesión
			Alert confirmacion = new Alert(AlertType.CONFIRMATION);
			confirmacion.setTitle("Confirmar cierre de sesión");
			confirmacion.setHeaderText("¿Desea cerrar sesión?");
			confirmacion.setContentText("Volverá a la pantalla de inicio de sesión.");

			Optional<ButtonType> resultado = confirmacion.showAndWait();
			if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
				SessionManager.logout();
				stageManager.switchScene(FxmlView.LOGIN);
			}
		}
	}

	/**
	 * Maneja el evento de click en el botón "Incidencias".
	 * <p>
	 * Navega a la pantalla de consulta y gestión de incidencias.
	 * </p>
	 */
	@FXML
	private void handleIncidencias() {
		stageManager.switchScene(FxmlView.INCIDENCIAS);
	}

	/**
	 * Abre la ventana de ayuda contextual mostrando la sección de gestión de
	 * espectáculos.
	 */
	@FXML
	private void handleAyuda() {
		helpStageManager.showHelp(FxmlView.GESTIONAR_ESPECTACULOS);
	}
}
