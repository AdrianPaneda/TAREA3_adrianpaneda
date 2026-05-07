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
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class GestionPersonasController implements Initializable {

	@FXML
	private TableView<Persona> tablaPersonas;

	// @FXML
	// private TableColumn<Persona, String> colNombre;

	@FXML
	private TableColumn<Persona, String> colEmail;

	@FXML
	private TableColumn<Persona, String> colPerfil;

	@FXML
	private TableColumn<Persona, String> colUsuario;

	@FXML
	private Button btnRegistrarArtista;

	@FXML
	private Button btnRegistrarCoordinacion;

	@FXML
	private Button btnCerrarSesion;

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
	private TextField txtEmail;

	@FXML
	private Label lblErrorEmail;

	@FXML
	private TextField txtNacionalidad;

	@FXML
	private Label lblErrorNacionalidad;

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

	@FXML
	private VBox panelCoordinacion;

	@FXML
	private CheckBox chkSenior;

	@FXML
	private DatePicker dateFechaSenior;

	@FXML
	private Label lblErrorFechaSenior;

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

	@FXML
	private Button btnGestionEspectaculos;

	@FXML
	private Button btnVerEspectaculos;

	@Autowired
	private PersonaService personaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	private ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();

	private enum Modo {
		REGISTRO_ARTISTA, REGISTRO_COORDINACION, EDITAR_ARTISTA, EDITAR_COORDINACION
	}

	private Modo modoActual;
	private Persona personaEnEdicion;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		validarAcceso();
		configurarTabla();
		cargarPersonas();
		ocultarFormulario();
	}

	private void validarAcceso() {
		Credenciales user = SessionManager.getCurrentUser();
		if (user == null || user.getPerfil() != Perfil.admin) {
			stageManager.switchScene(FxmlView.LOGIN);
		}
	}

	private void configurarTabla() {
		// colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colPerfil.setCellValueFactory(cellData -> {
			Persona p = cellData.getValue();
			String perfil = (p instanceof Artista) ? "Artista" : "Coordinación";
			return new SimpleStringProperty(perfil);
		});
		colUsuario.setCellValueFactory(cellData -> {
			Persona p = cellData.getValue();
			String usuario = (p.getCredenciales() != null) ? p.getCredenciales().getNombreUsuario() : "-";
			return new SimpleStringProperty(usuario);
		});
		añadirColumnaAcciones();
	}

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

	private void cargarPersonas() {
		listaPersonas.clear();
		listaPersonas.addAll(personaService.obtenerTodas());
		tablaPersonas.setItems(listaPersonas);
	}

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

		txtNombre.setText(persona.getNombre());
		txtEmail.setText(persona.getEmail());
		txtNacionalidad.setText(persona.getNacionalidad());
		mostrarCredenciales(false);
	}

	/**
	 * Muestra el panel del formulario y bloquea los botones principales.
	 */
	private void mostrarFormulario() {
		contenedorFormulario.setVisible(true);
		contenedorFormulario.setManaged(true);
		bloquearBotones(true);
	}

	/**
	 * Oculta el formulario completo y desbloquea los botones principales.
	 */
	private void ocultarFormulario() {
		contenedorFormulario.setVisible(false);
		contenedorFormulario.setManaged(false);
		modoActual = null;
		personaEnEdicion = null;
		bloquearBotones(false);
	}

	/**
	 * Bloquea o desbloquea los botones principales durante la edición o registro.
	 *
	 * @param bloquear true para bloquear, false para desbloquear
	 */
	private void bloquearBotones(boolean bloquear) {
		btnRegistrarArtista.setDisable(bloquear);
		btnRegistrarCoordinacion.setDisable(bloquear);
		btnGestionEspectaculos.setDisable(bloquear);
		btnVerEspectaculos.setDisable(bloquear);
		tablaPersonas.setDisable(bloquear);
	}

	private void mostrarFormularioArtista() {
		panelArtista.setVisible(true);
		panelArtista.setManaged(true);
		panelCoordinacion.setVisible(false);
		panelCoordinacion.setManaged(false);
	}

	private void mostrarFormularioCoordinacion() {
		panelCoordinacion.setVisible(true);
		panelCoordinacion.setManaged(true);
		panelArtista.setVisible(false);
		panelArtista.setManaged(false);
	}

	private void mostrarCredenciales(boolean mostrar) {
		panelCredenciales.setVisible(mostrar);
		panelCredenciales.setManaged(mostrar);
	}

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

	private void rellenarDatosCoordinacion(Coordinacion coordinacion) {
		chkSenior.setSelected(coordinacion.isSenior());
		dateFechaSenior.setDisable(!coordinacion.isSenior());
		dateFechaSenior.setValue(coordinacion.getFechaSenior());
	}

	@FXML
	private void handleToggleSenior(ActionEvent event) {
		boolean esSenior = chkSenior.isSelected();
		dateFechaSenior.setDisable(!esSenior);
		if (!esSenior) {
			dateFechaSenior.setValue(null);
		}
	}

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

	private boolean validarEspecialidades() {
		if (obtenerEspecialidadesSeleccionadas().isEmpty()) {
			lblErrorEspecialidades.setText("Debe seleccionar al menos una especialidad");
			return false;
		}
		return true;
	}

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

	private Credenciales crearCredenciales(Perfil perfil) {
		Credenciales credenciales = new Credenciales();
		credenciales.setNombreUsuario(txtUsuario.getText().trim().toLowerCase());
		credenciales.setPassword(txtPassword.getText());
		credenciales.setPerfil(perfil);
		return credenciales;
	}

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

	private void limpiarErrores() {
		lblErrorNombre.setText("");
		lblErrorEmail.setText("");
		lblErrorNacionalidad.setText("");
		lblErrorEspecialidades.setText("");
		lblErrorFechaSenior.setText("");
		lblErrorUsuario.setText("");
		lblErrorPassword.setText("");
	}

	private void mostrarErrorGeneral(String mensaje) {
		if (mensaje.toLowerCase().contains("email")) {
			lblErrorEmail.setText(mensaje);
		} else if (mensaje.toLowerCase().contains("usuario")) {
			lblErrorUsuario.setText(mensaje);
		} else {
			lblErrorEmail.setText(mensaje);
		}
	}

	@FXML
	private void handleCancelar(ActionEvent event) {
		ocultarFormulario();
	}

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
	 * Gestiona el botón de ver espectaculos con aviso si hay operación en curso
	 * 
	 * @param event
	 */
	@FXML
	private void handleGestionEspectaculos(ActionEvent event) {
		if (modoActual != null) {
			Alert aviso = new Alert(AlertType.WARNING);
			aviso.setTitle("Operación en curso");
			aviso.setHeaderText("Tiene una operación sin guardar");
			aviso.setContentText("Debe guardar o cancelar la operación actual antes de continuar.");
			aviso.showAndWait();
			return;
		}
		stageManager.switchScene(FxmlView.GESTIONAR_ESPECTACULOS);
	}

	/**
	 * Gestiona ver los detalles de espectaculos con aviso si hay operación en
	 * curso.
	 * 
	 * @param event
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
	 * Cierra la sesión con aviso si hay operación en curso.
	 */
	@FXML
	private void handleCerrarSesion(ActionEvent event) {
		if (modoActual != null) {
			Alert aviso = new Alert(AlertType.WARNING);
			aviso.setTitle("Operación en curso");
			aviso.setHeaderText("Tiene una operación sin guardar");
			aviso.setContentText("Debe guardar o cancelar la operación actual antes de cerrar sesión.");
			aviso.showAndWait();
			return;
		}
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
