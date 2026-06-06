package com.adrianpaneda.tarea3AD2024base.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.HelpStageManager;
import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion;
import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.modelo.Persona;
import com.adrianpaneda.tarea3AD2024base.services.PersonaService;
import com.adrianpaneda.tarea3AD2024base.services.dossier.DossierService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controlador para la pantalla de registro y edición de personas (CU3A, CU3B,
 * CU3C).
 * <p>
 * Gestiona tanto el registro de nuevas personas (artistas y coordinadores) como
 * la edición de las existentes. El modo de operación se determina a partir del
 * ID almacenado en {@link SessionManager#getSelectedPersona()}: si hay un ID,
 * se carga la persona correspondiente en modo edición; si es {@code null}, se
 * opera en modo registro.
 * </p>
 * <p>
 * El tipo de persona preseleccionado en modo registro se recupera de
 * {@link SessionManager#getTipoRegistroPersona()}. Los paneles específicos de
 * artista y coordinación se muestran u ocultan dinámicamente según el tipo
 * seleccionado en el ComboBox.
 * </p>
 * <p>
 * En modo edición las credenciales no son modificables, por lo que sus campos
 * se deshabilitan. En modo registro el panel de credenciales está habilitado y
 * es obligatorio.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class RegistrarPersonaController implements Initializable {

	// ── Cabecera ──────────────────────────────────────────────────────────────

	@FXML
	private Label lblTitulo;

	// ── Selector de tipo ──────────────────────────────────────────────────────

	@FXML
	private ComboBox<String> cmbTipoPersona;

	// ── Datos personales ──────────────────────────────────────────────────────

	@FXML
	private TextField txtNombre;

	@FXML
	private Label lblErrorNombre;

	@FXML
	private TextField txtEmail;

	@FXML
	private Label lblErrorEmail;

	@FXML
	private ComboBox<String> cmbNacionalidad;

	@FXML
	private Label lblErrorNacionalidad;

	// ── Panel artista ─────────────────────────────────────────────────────────

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

	// ── Panel coordinación ────────────────────────────────────────────────────

	@FXML
	private VBox panelCoordinacion;

	@FXML
	private CheckBox chkSenior;

	@FXML
	private DatePicker dateFechaSenior;

	@FXML
	private Label lblErrorFechaSenior;

	// ── Credenciales ──────────────────────────────────────────────────────────

	@FXML
	private TextField txtUsuario;

	@FXML
	private Label lblErrorUsuario;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Label lblErrorPassword;

	// ── Mensaje global ────────────────────────────────────────────────────────

	@FXML
	private Label lblMensaje;

	@FXML
	private Button btnGuardar;

	// ── Dependencias ──────────────────────────────────────────────────────────

	@Autowired
	private PersonaService personaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private HelpStageManager helpStageManager;

	@Autowired
	private DossierService dossierServ;

	/** Persona que se está editando, o {@code null} si es un registro nuevo. */
	private Persona personaEnEdicion;

	/** Listas para el autocompletado del ComboBox de nacionalidades. */
	private ObservableList<String> nacionalidades;
	private FilteredList<String> filtradasNacionalidades;

	// ── Inicialización ────────────────────────────────────────────────────────

	/**
	 * Inicializa la pantalla determinando si se está en modo registro o edición a
	 * partir de {@link SessionManager#getSelectedPersona()}.
	 * <p>
	 * Si hay un ID de persona almacenado en sesión, carga la persona con
	 * {@link PersonaService#buscarPorId(Long)} y precarga el formulario con sus
	 * datos en modo edición, deshabilitando los campos de credenciales. Si el ID es
	 * {@code null}, opera en modo registro y preselecciona el tipo indicado por
	 * {@link SessionManager#getTipoRegistroPersona()}.
	 * </p>
	 *
	 * @param location  la URL de localización del recurso FXML
	 * @param resources el bundle de recursos para la internacionalización
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configurarTipoPersona();
		configurarNacionalidades();

		Long idPersona = SessionManager.getSelectedPersona();

		if (idPersona != null) {
			// Modo edición
			personaEnEdicion = personaService.buscarPorId(idPersona);
			lblTitulo.setText("EDITAR PERSONA");
			cmbTipoPersona.setDisable(true);
			txtUsuario.setDisable(true);
			txtPassword.setDisable(true);
			precargarDatos(personaEnEdicion);
		} else {
			// Modo registro — preseleccionar tipo si viene informado desde SessionManager
			String tipo = SessionManager.getTipoRegistroPersona();
			if (tipo != null) {
				cmbTipoPersona.setValue(tipo);
				mostrarPanelSegunTipo(tipo);
			}
		}
	}

	/**
	 * Configura el ComboBox de tipo de persona con las opciones disponibles.
	 */
	private void configurarTipoPersona() {
		cmbTipoPersona.setItems(FXCollections.observableArrayList("Artista", "Coordinación"));
	}

	/**
	 * Configura el ComboBox de nacionalidades con autocompletado a partir del
	 * fichero XML de países.
	 * <p>
	 * El combo es editable y filtra las opciones en tiempo real según lo que el
	 * usuario escribe, siguiendo el mismo patrón que
	 * {@link GestionPersonasController}.
	 * </p>
	 */
	private void configurarNacionalidades() {
		File file = new File("src/main/resources/DATA/paises.xml");
		Map<String, String> nacionalidadesMap = PersonaService.listarPaises(file);
		nacionalidades = FXCollections.observableArrayList(nacionalidadesMap.values());
		Collections.sort(nacionalidades);

		filtradasNacionalidades = new FilteredList<>(nacionalidades, p -> true);
		cmbNacionalidad.setItems(filtradasNacionalidades);
		cmbNacionalidad.setEditable(true);
		cmbNacionalidad.setPromptText("Escribe para buscar...");

		cmbNacionalidad.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
			final String filtro = newVal == null ? "" : newVal.toLowerCase().trim();
			if (cmbNacionalidad.getValue() != null && cmbNacionalidad.getValue().equalsIgnoreCase(newVal)) {
				return;
			}
			filtradasNacionalidades.setPredicate(nac -> filtro.isEmpty() || nac.toLowerCase().contains(filtro));
			if (!filtro.isEmpty() && !filtradasNacionalidades.isEmpty()) {
				cmbNacionalidad.show();
			}
		});
	}

	/**
	 * Precarga el formulario con los datos de la persona que se va a editar.
	 * <p>
	 * Rellena los campos comunes y delega en {@link #rellenarDatosArtista(Artista)}
	 * o {@link #rellenarDatosCoordinacion(Coordinacion)} según el tipo de la
	 * persona.
	 * </p>
	 *
	 * @param persona la persona cuyos datos se cargan en el formulario
	 */
	private void precargarDatos(Persona persona) {
		txtNombre.setText(persona.getNombre());
		txtEmail.setText(persona.getEmail());
		cmbNacionalidad.setValue(persona.getNacionalidad());

		if (persona instanceof Artista) {
			cmbTipoPersona.setValue("Artista");
			mostrarPanelSegunTipo("Artista");
			rellenarDatosArtista((Artista) persona);
		} else if (persona instanceof Coordinacion) {
			cmbTipoPersona.setValue("Coordinación");
			mostrarPanelSegunTipo("Coordinación");
			rellenarDatosCoordinacion((Coordinacion) persona);
		}
	}

	/**
	 * Rellena los campos específicos de artista en el formulario con los datos de
	 * la entidad.
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
	 * Rellena los campos específicos de coordinación en el formulario con los datos
	 * de la entidad.
	 *
	 * @param coordinacion la coordinación cuyos datos se cargarán
	 */
	private void rellenarDatosCoordinacion(Coordinacion coordinacion) {
		chkSenior.setSelected(coordinacion.isSenior());
		dateFechaSenior.setDisable(!coordinacion.isSenior());
		dateFechaSenior.setValue(coordinacion.getFechaSenior());
	}

	// ── Eventos de UI ──────────────────────────────────────────────────────────

	/**
	 * Reacciona al cambio de selección en el ComboBox de tipo de persona mostrando
	 * u ocultando los paneles específicos de artista o coordinación.
	 *
	 * @param event el evento de acción del ComboBox
	 */
	@FXML
	private void handleTipoPersonaSeleccionado(ActionEvent event) {
		String tipo = cmbTipoPersona.getValue();
		if (tipo != null) {
			mostrarPanelSegunTipo(tipo);
		}
	}

	/**
	 * Muestra el panel específico correspondiente al tipo indicado y oculta el
	 * otro.
	 *
	 * @param tipo el tipo de persona seleccionado ("Artista" o "Coordinación")
	 */
	private void mostrarPanelSegunTipo(String tipo) {
		boolean esArtista = "Artista".equals(tipo);
		panelArtista.setVisible(esArtista);
		panelArtista.setManaged(esArtista);
		panelCoordinacion.setVisible(!esArtista);
		panelCoordinacion.setManaged(!esArtista);
	}

	/**
	 * Maneja el cambio de estado del checkbox "Senior".
	 * <p>
	 * Habilita o deshabilita el DatePicker de fecha senior y limpia la fecha si se
	 * desmarca.
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

	/**
	 * Maneja el click en el botón "Guardar".
	 * <p>
	 * Limpia los errores y ejecuta el registro o la actualización según el modo
	 * activo determinado por {@code personaEnEdicion}.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleGuardar(ActionEvent event) {
		limpiarErrores();
		if (personaEnEdicion != null) {
			if ("Artista".equals(cmbTipoPersona.getValue())) {
				actualizarArtista();
			} else {
				actualizarCoordinacion();
			}
		} else {
			if ("Artista".equals(cmbTipoPersona.getValue())) {
				registrarArtista();
			} else {
				registrarCoordinacion();
			}
		}
	}

	// ── Registro ──────────────────────────────────────────────────────────────

	/**
	 * Valida el formulario, construye un nuevo {@link Artista} y lo persiste.
	 */
	private void registrarArtista() {
		if (!validarDatosPersonales() || !validarEspecialidades() || !validarCredenciales()) {
			return;
		}
		try {
			Artista artista = new Artista();
			artista.setNombre(txtNombre.getText().trim());
			artista.setEmail(txtEmail.getText().trim());
			artista.setNacionalidad(cmbNacionalidad.getValue().trim());
			artista.setApodo(txtApodo.getText().trim().isEmpty() ? null : txtApodo.getText().trim());
			artista.setEspecialidades(obtenerEspecialidadesSeleccionadas());
			Credenciales credenciales = crearCredenciales(Perfil.artista);
			artista.setCredenciales(credenciales);
			personaService.validarEmailUnico(artista.getEmail());
			personaService.guardar(artista);
			stageManager.switchScene(FxmlView.GESTION_PERSONAS);
		} catch (IllegalArgumentException e) {
			mostrarErrorGeneral(e.getMessage());
		}
	}

	/**
	 * Valida el formulario, construye una nueva {@link Coordinacion} y la persiste.
	 */
	private void registrarCoordinacion() {
		if (!validarDatosPersonales() || !validarFechaSenior() || !validarCredenciales()) {
			return;
		}
		try {
			Coordinacion coordinacion = new Coordinacion();
			coordinacion.setNombre(txtNombre.getText().trim());
			coordinacion.setEmail(txtEmail.getText().trim());
			coordinacion.setNacionalidad(cmbNacionalidad.getValue().trim());
			coordinacion.setSenior(chkSenior.isSelected());
			coordinacion.setFechaSenior(chkSenior.isSelected() ? dateFechaSenior.getValue() : null);
			Credenciales credenciales = crearCredenciales(Perfil.coordinacion);
			coordinacion.setCredenciales(credenciales);
			personaService.validarEmailUnico(coordinacion.getEmail());
			personaService.guardar(coordinacion);
			stageManager.switchScene(FxmlView.GESTION_PERSONAS);
		} catch (IllegalArgumentException e) {
			mostrarErrorGeneral(e.getMessage());
		}
	}

	// ── Edición ───────────────────────────────────────────────────────────────

	/**
	 * Actualiza los datos del artista en edición y los persiste.
	 */
	private void actualizarArtista() {
		if (!validarDatosPersonales() || !validarEspecialidades()) {
			return;
		}
		try {
			Artista artista = (Artista) personaEnEdicion;
			artista.setNombre(txtNombre.getText().trim());
			artista.setEmail(txtEmail.getText().trim());
			artista.setNacionalidad(cmbNacionalidad.getValue().trim());
			artista.setApodo(txtApodo.getText().trim().isEmpty() ? null : txtApodo.getText().trim());
			artista.setEspecialidades(obtenerEspecialidadesSeleccionadas());
			personaService.actualizar(artista);
			stageManager.switchScene(FxmlView.GESTION_PERSONAS);
		} catch (IllegalArgumentException e) {
			lblErrorEmail.setText(e.getMessage());
		}
	}

	/**
	 * Actualiza los datos de la coordinación en edición y los persiste.
	 */
	private void actualizarCoordinacion() {
		if (!validarDatosPersonales() || !validarFechaSenior()) {
			return;
		}
		try {
			Coordinacion coordinacion = (Coordinacion) personaEnEdicion;
			coordinacion.setNombre(txtNombre.getText().trim());
			coordinacion.setEmail(txtEmail.getText().trim());
			coordinacion.setNacionalidad(cmbNacionalidad.getValue().trim());
			coordinacion.setSenior(chkSenior.isSelected());
			coordinacion.setFechaSenior(chkSenior.isSelected() ? dateFechaSenior.getValue() : null);
			personaService.actualizar(coordinacion);
			stageManager.switchScene(FxmlView.GESTION_PERSONAS);
		} catch (IllegalArgumentException e) {
			lblErrorEmail.setText(e.getMessage());
		}
	}

	// ── Validaciones ──────────────────────────────────────────────────────────

	/**
	 * Valida los campos comunes de nombre, email y nacionalidad.
	 *
	 * @return {@code true} si todos los datos personales son válidos
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
		if (cmbNacionalidad.getValue() == null || cmbNacionalidad.getValue().toString().trim().isEmpty()) {
			lblErrorNacionalidad.setText("La nacionalidad es obligatoria");
			valido = false;
		}
		if (!verificarNacionalidad(cmbNacionalidad.getValue())) {
			lblErrorNacionalidad.setText("Nacionalidad no válida");
			valido = false;
		}
		return valido;
	}

	/**
	 * Verifica que la nacionalidad introducida exista en el fichero XML de países.
	 *
	 * @param nacionalidad la cadena de nacionalidad a verificar
	 * @return {@code true} si la nacionalidad existe en el listado de países
	 */
	private boolean verificarNacionalidad(String nacionalidad) {
		File file = new File("src/main/resources/DATA/paises.xml");
		List<String> lista = new ArrayList<>(PersonaService.listarPaises(file).values());
		return lista.contains(nacionalidad);
	}

	/**
	 * Valida que se haya seleccionado al menos una especialidad para el artista.
	 *
	 * @return {@code true} si hay al menos una especialidad seleccionada
	 */
	private boolean validarEspecialidades() {
		if (obtenerEspecialidadesSeleccionadas().isEmpty()) {
			lblErrorEspecialidades.setText("Debe seleccionar al menos una especialidad");
			return false;
		}
		return true;
	}

	/**
	 * Valida que si la coordinación es senior tenga informada la fecha senior y que
	 * dicha fecha no sea posterior a hoy.
	 *
	 * @return {@code true} si la fecha senior es válida o no es senior
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
	 * Valida el usuario y contraseña introducidos según las reglas de negocio.
	 *
	 * @return {@code true} si las credenciales son válidas
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

	// ── Helpers ───────────────────────────────────────────────────────────────

	/**
	 * Obtiene el conjunto de especialidades marcadas con los checkboxes del
	 * formulario.
	 *
	 * @return conjunto de especialidades seleccionadas
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
	 * Construye un objeto {@link Credenciales} con los datos del formulario y el
	 * perfil indicado.
	 *
	 * @param perfil el perfil a asignar a las credenciales
	 * @return las credenciales construidas con usuario en minúsculas
	 */
	private Credenciales crearCredenciales(Perfil perfil) {
		Credenciales credenciales = new Credenciales();
		credenciales.setNombreUsuario(txtUsuario.getText().trim().toLowerCase());
		credenciales.setPassword(txtPassword.getText());
		credenciales.setPerfil(perfil);
		return credenciales;
	}

	/**
	 * Limpia todos los mensajes de error del formulario.
	 */
	private void limpiarErrores() {
		lblErrorNombre.setText("");
		lblErrorEmail.setText("");
		lblErrorNacionalidad.setText("");
		lblErrorEspecialidades.setText("");
		lblErrorFechaSenior.setText("");
		lblErrorUsuario.setText("");
		lblErrorPassword.setText("");
		lblMensaje.setText("");
	}

	/**
	 * Muestra un mensaje de error en el campo más relacionado con el contenido del
	 * mensaje.
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

	// ── Navegación ─────────────────────────────────────────────────────────────

	/**
	 * Vuelve a la pantalla de gestión de personas descartando los cambios en curso.
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleVolver(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTION_PERSONAS);
	}

	/**
	 * Abre la ventana de ayuda contextual mostrando la sección de gestión de
	 * personas.
	 */
	@FXML
	private void handleAyuda() {
		helpStageManager.showHelp(FxmlView.GESTION_PERSONAS);
	}
}
