package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion;
import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.modelo.Persona;
import com.adrianpaneda.tarea3AD2024base.services.PersonaService;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controlador para la pantalla de gestión de personas (CU3).
 * <p>
 * Permite al administrador registrar, modificar y eliminar artistas y
 * coordinadores del sistema, incluyendo sus credenciales de acceso.
 * </p>
 * <p>
 * La pantalla se divide en dos paneles: uno con la lista de personas
 * registradas y otro con un formulario dinámico que se adapta según el tipo de
 * persona (artista o coordinación) y el modo (registro o edición).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class GestionPersonasController implements Initializable {

	// ═══════════════ TABLA Y BOTONES PRINCIPALES ═══════════════

	@FXML
	private TableView<Persona> tablaPersonas;

	@FXML
	private TableColumn<Persona, String> colNombre;

	@FXML
	private TableColumn<Persona, String> colEmail;

	@FXML
	private TableColumn<Persona, String> colPerfil;

	@FXML
	private Button btnRegistrarArtista;

	@FXML
	private Button btnRegistrarCoordinacion;

	@FXML
	private Button btnCerrarSesion;

	// ═══════════════ FORMULARIO ═══════════════

	@FXML
	private ScrollPane scrollFormulario;

	@FXML
	private VBox panelFormulario;

	@FXML
	private Label lblTituloFormulario;

	// Datos personales
	@FXML
	private TextField txtNombre;

	@FXML
	private Label lblErrorNombre;

	@FXML
	private TextField txtEmail;

	@FXML
	private Label lblErrorEmail;

	@FXML
	private TextField txtNacionalidad;

	@FXML
	private Label lblErrorNacionalidad;

	// Panel artista
	@FXML
	private VBox panelArtista;

	@FXML
	private TextField txtApodo;

	@FXML
	private CheckBox chkAcrobacia;

	@FXML
	private CheckBox chkHumor;

	@FXML
	private CheckBox chkMagia;

	@FXML
	private CheckBox chkEquilibrismo;

	@FXML
	private CheckBox chkMalabarismo;

	@FXML
	private Label lblErrorEspecialidades;

	// Panel coordinación
	@FXML
	private VBox panelCoordinacion;

	@FXML
	private CheckBox chkSenior;

	@FXML
	private DatePicker dateFechaSenior;

	@FXML
	private Label lblErrorFechaSenior;

	// Panel credenciales
	@FXML
	private VBox panelCredenciales;

	@FXML
	private TextField txtUsuario;

	@FXML
	private Label lblErrorUsuario;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Label lblErrorPassword;

	@FXML
	private Button btnGuardar;

	@FXML
	private Button btnCancelar;

	// ═══════════════ DEPENDENCIAS ═══════════════

	@Autowired
	private PersonaService personaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	private ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();

	/**
	 * Modo actual del formulario. Determina qué tipo de persona se está registrando
	 * o editando.
	 */
	private enum Modo {
		REGISTRO_ARTISTA, REGISTRO_COORDINACION, EDITAR_ARTISTA, EDITAR_COORDINACION
	}

	private Modo modoActual;

	/**
	 * Persona que se está editando actualmente. Null si está en modo registro.
	 */
	private Persona personaEnEdicion;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		validarAcceso();
		configurarTabla();
		cargarPersonas();
		ocultarFormulario();
	}

	// ═══════════════ VALIDACIÓN DE ACCESO ═══════════════

	/**
	 * Valida que el usuario tenga perfil de administrador.
	 * <p>
	 * Si no es admin, redirige al login.
	 * </p>
	 */
	private void validarAcceso() {
		Credenciales user = SessionManager.getCurrentUser();
		if (user == null || user.getPerfil() != Perfil.admin) {
			stageManager.switchScene(FxmlView.LOGIN);
		}
	}

	// ═══════════════ TABLA DE PERSONAS ═══════════════

	/**
	 * Configura las columnas de la tabla y añade la columna de acciones.
	 */
	private void configurarTabla() {
		colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

		// Columna perfil: muestra "Artista" o "Coordinación" según el tipo
		colPerfil.setCellValueFactory(cellData -> {
			Persona p = cellData.getValue();
			String perfil = (p instanceof Artista) ? "Artista" : "Coordinación";
			return new SimpleStringProperty(perfil);
		});

		añadirColumnaAcciones();
	}

	/**
	 * Añade dinámicamente una columna con botones "Editar" y "Eliminar" en cada
	 * fila de la tabla.
	 */
	private void añadirColumnaAcciones() {
		TableColumn<Persona, Void> colAcciones = new TableColumn<>("Acciones");
		colAcciones.setPrefWidth(220.0);
		colAcciones.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-alignment: CENTER;");

		colAcciones.setCellFactory(param -> new TableCell<>() {
			private final Button btnEditar = new Button("Editar");
			private final Button btnEliminar = new Button("Eliminar");
			private final HBox contenedor = new HBox(8, btnEditar, btnEliminar);

			{
				btnEditar.setStyle(
						"-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 5 10 5 10; -fx-font-weight: bold; -fx-cursor: hand;");
				btnEliminar.setStyle(
						"-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 5 10 5 10; -fx-font-weight: bold; -fx-cursor: hand;");
				contenedor.setStyle("-fx-alignment: CENTER;");

				btnEditar.setOnAction(event -> {
					Persona persona = getTableView().getItems().get(getIndex());
					handleEditar(persona);
				});

				btnEliminar.setOnAction(event -> {
					Persona persona = getTableView().getItems().get(getIndex());
					handleEliminar(persona);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : contenedor);
			}
		});

		tablaPersonas.getColumns().add(colAcciones);
	}

	/**
	 * Carga todas las personas desde la base de datos y las muestra en la tabla.
	 */
	private void cargarPersonas() {
		listaPersonas.clear();
		listaPersonas.addAll(personaService.obtenerTodas());
		tablaPersonas.setItems(listaPersonas);
	}

	// ═══════════════ MODOS DEL FORMULARIO ═══════════════

	/**
	 * Maneja el evento de click en el botón "Registrar Artista".
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleRegistrarArtista(ActionEvent event) {
		modoActual = Modo.REGISTRO_ARTISTA;
		personaEnEdicion = null;
		limpiarFormulario();
		mostrarFormulario();
		mostrarFormularioArtista();
		lblTituloFormulario.setText("REGISTRAR ARTISTA");
		mostrarCredenciales(true);
	}

	/**
	 * Maneja el evento de click en el botón "Registrar Coordinación".
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleRegistrarCoordinacion(ActionEvent event) {
		modoActual = Modo.REGISTRO_COORDINACION;
		personaEnEdicion = null;
		limpiarFormulario();
		mostrarFormulario();
		mostrarFormularioCoordinacion();
		lblTituloFormulario.setText("REGISTRAR COORDINACIÓN");
		mostrarCredenciales(true);
	}

	/**
	 * Activa el modo edición rellenando el formulario con los datos de la persona
	 * seleccionada.
	 *
	 * @param persona la persona a editar
	 */
	private void handleEditar(Persona persona) {
		personaEnEdicion = persona;
		limpiarFormulario();
		mostrarFormulario();

		if (persona instanceof Artista) {
			modoActual = Modo.EDITAR_ARTISTA;
			mostrarFormularioArtista();
			lblTituloFormulario.setText("EDITAR ARTISTA");
			rellenarDatosArtista((Artista) persona);
		} else if (persona instanceof Coordinacion) {
			modoActual = Modo.EDITAR_COORDINACION;
			mostrarFormularioCoordinacion();
			lblTituloFormulario.setText("EDITAR COORDINACIÓN");
			rellenarDatosCoordinacion((Coordinacion) persona);
		}

		// Datos personales comunes
		txtNombre.setText(persona.getNombre());
		txtEmail.setText(persona.getEmail());
		txtNacionalidad.setText(persona.getNacionalidad());

		// En modo edición no se muestran las credenciales
		mostrarCredenciales(false);
	}

	/**
	 * Muestra el panel del formulario.
	 */
	private void mostrarFormulario() {
		scrollFormulario.setVisible(true);
		scrollFormulario.setManaged(true);
	}

	/**
	 * Muestra el panel del formulario de artista y oculta el de coordinación.
	 */
	private void mostrarFormularioArtista() {
		panelArtista.setVisible(true);
		panelArtista.setManaged(true);
		panelCoordinacion.setVisible(false);
		panelCoordinacion.setManaged(false);
	}

	/**
	 * Muestra el panel del formulario de coordinación y oculta el de artista.
	 */
	private void mostrarFormularioCoordinacion() {
		panelCoordinacion.setVisible(true);
		panelCoordinacion.setManaged(true);
		panelArtista.setVisible(false);
		panelArtista.setManaged(false);
	}

	/**
	 * Muestra u oculta el panel de credenciales.
	 *
	 * @param mostrar true para mostrar, false para ocultar
	 */
	private void mostrarCredenciales(boolean mostrar) {
		panelCredenciales.setVisible(mostrar);
		panelCredenciales.setManaged(mostrar);
	}

	/**
	 * Rellena los campos del formulario con los datos de un artista.
	 *
	 * @param artista el artista cuyos datos se cargarán
	 */
	private void rellenarDatosArtista(Artista artista) {
		txtApodo.setText(artista.getApodo() != null ? artista.getApodo() : "");

		Set<Especialidad> esp = artista.getEspecialidades();
		if (esp != null) {
			chkAcrobacia.setSelected(esp.contains(Especialidad.ACROBACIA));
			chkHumor.setSelected(esp.contains(Especialidad.HUMOR));
			chkMagia.setSelected(esp.contains(Especialidad.MAGIA));
			chkEquilibrismo.setSelected(esp.contains(Especialidad.EQUILIBRISMO));
			chkMalabarismo.setSelected(esp.contains(Especialidad.MALABARISMO));
		}
	}

	/**
	 * Rellena los campos del formulario con los datos de una coordinación.
	 *
	 * @param coordinacion la coordinación cuyos datos se cargarán
	 */
	private void rellenarDatosCoordinacion(Coordinacion coordinacion) {
		chkSenior.setSelected(coordinacion.isSenior());
		dateFechaSenior.setDisable(!coordinacion.isSenior());
		dateFechaSenior.setValue(coordinacion.getFechaSenior());
	}

	// ═══════════════ TOGGLE SENIOR ═══════════════

	/**
	 * Maneja el evento de cambio del checkbox senior.
	 * <p>
	 * Habilita o deshabilita el DatePicker de fecha senior según el estado del
	 * checkbox.
	 * </p>
	 *
	 * @param event el evento de acción del checkbox
	 */
	@FXML
	private void handleToggleSenior(ActionEvent event) {
		boolean esSenior = chkSenior.isSelected();
		dateFechaSenior.setDisable(!esSenior);
		if (!esSenior) {
			dateFechaSenior.setValue(null);
		}
	}

	// ═══════════════ GUARDAR ═══════════════

	/**
	 * Maneja el evento de click en el botón "Guardar".
	 * <p>
	 * Valida los datos y llama al método correspondiente según el modo actual.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleGuardar(ActionEvent event) {
		limpiarErrores();

		if (modoActual == Modo.REGISTRO_ARTISTA) {
			registrarArtista();
		} else if (modoActual == Modo.REGISTRO_COORDINACION) {
			registrarCoordinacion();
		} else if (modoActual == Modo.EDITAR_ARTISTA) {
			actualizarArtista();
		} else if (modoActual == Modo.EDITAR_COORDINACION) {
			actualizarCoordinacion();
		}
	}

	/**
	 * Registra un nuevo artista validando todos los datos.
	 */
	private void registrarArtista() {
		if (!validarDatosPersonales() || !validarEspecialidades() || !validarCredenciales()) {
			return;
		}

		try {
			Artista artista = new Artista();
			artista.setNombre(txtNombre.getText().trim());
			artista.setEmail(txtEmail.getText().trim());
			artista.setNacionalidad(txtNacionalidad.getText().trim());
			artista.setApodo(txtApodo.getText().trim().isEmpty() ? null : txtApodo.getText().trim());
			artista.setEspecialidades(obtenerEspecialidadesSeleccionadas());

			Credenciales credenciales = crearCredenciales(Perfil.artista);
			artista.setCredenciales(credenciales);

			personaService.validarEmailUnico(artista.getEmail());
			personaService.guardar(artista);

			cargarPersonas();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			mostrarErrorGeneral(e.getMessage());
		}
	}

	/**
	 * Registra una nueva coordinación validando todos los datos.
	 */
	private void registrarCoordinacion() {
		if (!validarDatosPersonales() || !validarFechaSenior() || !validarCredenciales()) {
			return;
		}

		try {
			Coordinacion coordinacion = new Coordinacion();
			coordinacion.setNombre(txtNombre.getText().trim());
			coordinacion.setEmail(txtEmail.getText().trim());
			coordinacion.setNacionalidad(txtNacionalidad.getText().trim());
			coordinacion.setSenior(chkSenior.isSelected());
			coordinacion.setFechaSenior(chkSenior.isSelected() ? dateFechaSenior.getValue() : null);

			Credenciales credenciales = crearCredenciales(Perfil.coordinacion);
			coordinacion.setCredenciales(credenciales);

			personaService.validarEmailUnico(coordinacion.getEmail());
			personaService.guardar(coordinacion);

			cargarPersonas();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			mostrarErrorGeneral(e.getMessage());
		}
	}

	/**
	 * Actualiza los datos de un artista existente.
	 */
	private void actualizarArtista() {
		if (!validarDatosPersonales() || !validarEspecialidades()) {
			return;
		}

		try {
			Artista artista = (Artista) personaEnEdicion;
			artista.setNombre(txtNombre.getText().trim());
			artista.setEmail(txtEmail.getText().trim());
			artista.setNacionalidad(txtNacionalidad.getText().trim());
			artista.setApodo(txtApodo.getText().trim().isEmpty() ? null : txtApodo.getText().trim());
			artista.setEspecialidades(obtenerEspecialidadesSeleccionadas());

			personaService.actualizar(artista);

			cargarPersonas();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			lblErrorEmail.setText(e.getMessage());
		}
	}

	/**
	 * Actualiza los datos de una coordinación existente.
	 */
	private void actualizarCoordinacion() {
		if (!validarDatosPersonales() || !validarFechaSenior()) {
			return;
		}

		try {
			Coordinacion coordinacion = (Coordinacion) personaEnEdicion;
			coordinacion.setNombre(txtNombre.getText().trim());
			coordinacion.setEmail(txtEmail.getText().trim());
			coordinacion.setNacionalidad(txtNacionalidad.getText().trim());
			coordinacion.setSenior(chkSenior.isSelected());
			coordinacion.setFechaSenior(chkSenior.isSelected() ? dateFechaSenior.getValue() : null);

			personaService.actualizar(coordinacion);

			cargarPersonas();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			lblErrorEmail.setText(e.getMessage());
		}
	}

	// VALIDACIONES

	/**
	 * Valida los datos personales (nombre, email, nacionalidad).
	 *
	 * @return true si todos los datos personales son válidos
	 */
	private boolean validarDatosPersonales() {
		boolean valido = true;

		if (txtNombre.getText().trim().isEmpty()) {
			lblErrorNombre.setText("El nombre es obligatorio");
			valido = false;
		}

		String email = txtEmail.getText().trim();
		if (email.isEmpty()) {
			lblErrorEmail.setText("El email es obligatorio");
			valido = false;
		} else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			lblErrorEmail.setText("El formato del email no es válido");
			valido = false;
		}

		if (txtNacionalidad.getText().trim().isEmpty()) {
			lblErrorNacionalidad.setText("La nacionalidad es obligatoria");
			valido = false;
		}

		return valido;
	}

	/**
	 * Valida que se haya seleccionado al menos una especialidad.
	 *
	 * @return true si hay al menos una especialidad seleccionada
	 */
	private boolean validarEspecialidades() {
		if (obtenerEspecialidadesSeleccionadas().isEmpty()) {
			lblErrorEspecialidades.setText("Debe seleccionar al menos una especialidad");
			return false;
		}
		return true;
	}

	/**
	 * Valida que si el coordinador es senior, tenga fecha senior asignada y la
	 * fecha no sea superior a la actual
	 *
	 * @return true si la fecha senior es válida
	 */
	private boolean validarFechaSenior() {
		if (chkSenior.isSelected() && dateFechaSenior.getValue() == null) {
			lblErrorFechaSenior.setText("Si es senior, debe indicar la fecha");
			return false;
		}
		if (chkSenior.isSelected() && dateFechaSenior.getValue().isAfter(java.time.LocalDate.now())) {
			lblErrorFechaSenior.setText("La fecha senior no puede ser posterior a hoy");
			return false;
		}
		return true;
	}

	/**
	 * Valida las credenciales (usuario y contraseña).
	 *
	 * @return true si las credenciales son válidas
	 */
	private boolean validarCredenciales() {
		boolean valido = true;
		String usuario = txtUsuario.getText();
		String password = txtPassword.getText();

		if (usuario.isEmpty()) {
			lblErrorUsuario.setText("El nombre de usuario es obligatorio");
			valido = false;
		}

		if (password.isEmpty()) {
			lblErrorPassword.setText("La contraseña es obligatoria");
			valido = false;
		}

		if (!valido) {
			return false;
		}

		try {
			personaService.validarCredenciales(usuario, password);
		} catch (IllegalArgumentException e) {
			String msg = e.getMessage();
			if (msg.contains("usuario") || msg.contains("Usuario")) {
				lblErrorUsuario.setText(msg);
			} else {
				lblErrorPassword.setText(msg);
			}
			valido = false;
		}

		return valido;
	}

	// HELPERS

	/**
	 * Obtiene el conjunto de especialidades seleccionadas en los checkboxes.
	 *
	 * @return conjunto de especialidades marcadas
	 */
	private Set<Especialidad> obtenerEspecialidadesSeleccionadas() {
		Set<Especialidad> especialidades = new HashSet<>();
		if (chkAcrobacia.isSelected())
			especialidades.add(Especialidad.ACROBACIA);
		if (chkHumor.isSelected())
			especialidades.add(Especialidad.HUMOR);
		if (chkMagia.isSelected())
			especialidades.add(Especialidad.MAGIA);
		if (chkEquilibrismo.isSelected())
			especialidades.add(Especialidad.EQUILIBRISMO);
		if (chkMalabarismo.isSelected())
			especialidades.add(Especialidad.MALABARISMO);
		return especialidades;
	}

	/**
	 * Crea un objeto Credenciales con los datos del formulario.
	 *
	 * @param perfil el perfil a asignar a las credenciales
	 * @return el objeto Credenciales creado
	 */
	private Credenciales crearCredenciales(Perfil perfil) {
		Credenciales credenciales = new Credenciales();
		credenciales.setNombreUsuario(txtUsuario.getText().trim().toLowerCase());
		credenciales.setPassword(txtPassword.getText());
		credenciales.setPerfil(perfil);
		return credenciales;
	}

	/**
	 * Limpia todos los campos y mensajes de error del formulario.
	 */
	private void limpiarFormulario() {
		txtNombre.clear();
		txtEmail.clear();
		txtNacionalidad.clear();
		txtApodo.clear();
		txtUsuario.clear();
		txtPassword.clear();

		chkAcrobacia.setSelected(false);
		chkHumor.setSelected(false);
		chkMagia.setSelected(false);
		chkEquilibrismo.setSelected(false);
		chkMalabarismo.setSelected(false);

		chkSenior.setSelected(false);
		dateFechaSenior.setValue(null);
		dateFechaSenior.setDisable(true);

		limpiarErrores();
	}

	/**
	 * Limpia todos los mensajes de error.
	 */
	private void limpiarErrores() {
		lblErrorNombre.setText("");
		lblErrorEmail.setText("");
		lblErrorNacionalidad.setText("");
		lblErrorEspecialidades.setText("");
		lblErrorFechaSenior.setText("");
		lblErrorUsuario.setText("");
		lblErrorPassword.setText("");
	}

	/**
	 * Muestra un mensaje de error en el label correspondiente según el contenido
	 * del mensaje.
	 *
	 * @param mensaje el mensaje de error a mostrar
	 */
	private void mostrarErrorGeneral(String mensaje) {
		if (mensaje.toLowerCase().contains("email")) {
			lblErrorEmail.setText(mensaje);
		} else if (mensaje.toLowerCase().contains("usuario")) {
			lblErrorUsuario.setText(mensaje);
		} else {
			lblErrorEmail.setText(mensaje);
		}
	}

	/**
	 * Oculta el formulario completo (modo inicial).
	 */
	private void ocultarFormulario() {
		scrollFormulario.setVisible(false);
		scrollFormulario.setManaged(false);
		modoActual = null;
		personaEnEdicion = null;
	}

	// HANDLERS BOTONES

	/**
	 * Maneja el evento de click en el botón "Cancelar".
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleCancelar(ActionEvent event) {
		ocultarFormulario();
	}

	/**
	 * Muestra una confirmación y elimina la persona si se acepta.
	 *
	 * @param persona la persona a eliminar
	 */
	private void handleEliminar(Persona persona) {
		Alert confirmacion = new Alert(AlertType.CONFIRMATION);
		confirmacion.setTitle("Confirmar eliminación");
		confirmacion.setHeaderText("¿Eliminar a " + persona.getNombre() + "?");
		confirmacion.setContentText("Esta acción no se puede deshacer. Se eliminarán también sus credenciales.");

		Optional<ButtonType> resultado = confirmacion.showAndWait();
		if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
			try {
				personaService.eliminar(persona.getId());
				cargarPersonas();
				if (personaEnEdicion != null && personaEnEdicion.getId().equals(persona.getId())) {
					ocultarFormulario();
				}
			} catch (Exception e) {
				Alert error = new Alert(AlertType.ERROR);
				error.setTitle("Error");
				error.setHeaderText("No se pudo eliminar");
				error.setContentText(e.getMessage());
				error.showAndWait();
			}
		}
	}

	/**
	 * Maneja el evento de click en el botón "Cerrar Sesión".
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleCerrarSesion(ActionEvent event) {
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