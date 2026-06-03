package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.HelpStageManager;
import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion;
import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.modelo.Persona;
import com.adrianpaneda.tarea3AD2024base.services.PersonaService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Controlador para la pantalla de gestión de personas (CU3).
 * <p>
 * Muestra las personas del sistema (artistas y coordinadores) en formato de
 * tarjetas (cards) con posibilidad de filtrar por perfil y por nombre de
 * usuario. Desde esta pantalla se puede navegar al formulario de registro o
 * edición de personas ({@link FxmlView#REGISTRAR_PERSONA}), así como eliminar
 * personas directamente desde cada card.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class GestionPersonasController implements Initializable {

	// ── Filtros ────────────────────────────────────────────────────────────────

	@FXML
	private ComboBox<String> cmbFiltroPerfil;

	@FXML
	private TextField txtFiltroUsuario;

	@FXML
	private Label lblContadorResultados;

	// ── Contenedor de cards ────────────────────────────────────────────────────

	@FXML
	private VBox contenedorCards;

	// ── Botones de navegación ──────────────────────────────────────────────────

	@FXML
	private Button btnRegistrarArtista;

	@FXML
	private Button btnRegistrarCoordinacion;

	@FXML
	private Button btnGestionEspectaculos;

	@FXML
	private Button btnVerEspectaculos;

	@FXML
	private Button btnHistorial;

	@FXML
	private Button btnIncidencias;

	@FXML
	private Button btnCerrarSesion;

	// ── Dependencias ──────────────────────────────────────────────────────────

	@Autowired
	private PersonaService personaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private HelpStageManager helpStageManager;

	/** Lista completa de personas cargadas desde la base de datos. */
	private List<Persona> todasLasPersonas;

	// ── Inicialización ────────────────────────────────────────────────────────

	/**
	 * Inicializa la pantalla validando el acceso, configurando los filtros y
	 * cargando las personas en formato de cards.
	 *
	 * @param location  la URL de localización del recurso FXML
	 * @param resources el bundle de recursos para la internacionalización
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		validarAcceso();
		configurarFiltros();
		cargarPersonas();
	}

	/**
	 * Verifica que el usuario autenticado tenga perfil de administrador.
	 * <p>
	 * Si no tiene el perfil requerido, redirige al login.
	 * </p>
	 */
	private void validarAcceso() {
		Credenciales user = SessionManager.getCurrentUser();
		if (user == null || user.getPerfil() != Perfil.admin) {
			stageManager.switchScene(FxmlView.LOGIN);
		}
	}

	/**
	 * Configura el ComboBox de filtro por perfil con las opciones disponibles.
	 */
	private void configurarFiltros() {
		cmbFiltroPerfil.setItems(FXCollections.observableArrayList("Todos", "Artista", "Coordinación"));
		cmbFiltroPerfil.setValue("Todos");
	}

	/**
	 * Carga todas las personas desde el servicio y las renderiza como cards.
	 */
	private void cargarPersonas() {
		todasLasPersonas = personaService.obtenerTodas();
		renderizarCards(todasLasPersonas);
	}

	/**
	 * Renderiza la lista de personas recibida como cards dentro del contenedor.
	 * <p>
	 * Limpia el contenedor antes de volver a pintarlo y actualiza el contador de
	 * resultados.
	 * </p>
	 *
	 * @param personas la lista de personas a mostrar
	 */
	private void renderizarCards(List<Persona> personas) {
		contenedorCards.getChildren().clear();
		lblContadorResultados.setText(personas.size() + " personas");
		for (Persona persona : personas) {
			contenedorCards.getChildren().add(crearCard(persona));
		}
	}

	/**
	 * Construye la card visual para una persona concreta.
	 * <p>
	 * La card muestra el perfil con un color diferenciado (azul para artista, verde
	 * para coordinación), el nombre de usuario, el nombre real, el email y la
	 * contraseña, junto con botones de editar y eliminar.
	 * </p>
	 *
	 * @param persona la persona para la que se construye la card
	 * @return el {@link HBox} que representa la card de la persona
	 */
	private HBox crearCard(Persona persona) {
		HBox card = new HBox(20);
		card.setAlignment(Pos.CENTER_LEFT);
		card.setPadding(new Insets(16, 20, 16, 20));
		card.setStyle(
				"-fx-background-color: white;" +
				"-fx-border-color: #e2e8f0;" +
				"-fx-border-width: 1.5;" +
				"-fx-border-radius: 12;" +
				"-fx-background-radius: 12;" +
				"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

		// Etiqueta de perfil
		boolean esArtista = persona instanceof Artista;
		Label lblPerfil = new Label(esArtista ? "ARTISTA" : "COORDINACIÓN");
		lblPerfil.setFont(Font.font("System Bold", 11));
		lblPerfil.setStyle(
				"-fx-text-fill: white;" +
				"-fx-background-color: " + (esArtista ? "#2563eb" : "#16a34a") + ";" +
				"-fx-background-radius: 6;" +
				"-fx-padding: 4 10 4 10;");
		lblPerfil.setMinWidth(110);

		// Datos
		VBox datos = new VBox(4);
		HBox.setHgrow(datos, Priority.ALWAYS);

		String usuario = persona.getCredenciales() != null
				? persona.getCredenciales().getNombreUsuario() : "-";
		String password = persona.getCredenciales() != null
				? persona.getCredenciales().getPassword() : "-";

		Label lblUsuario = new Label("👤 " + usuario);
		lblUsuario.setFont(Font.font("System Bold", 14));
		lblUsuario.setStyle("-fx-text-fill: #0f172a;");

		Label lblNombre = new Label(persona.getNombre());
		lblNombre.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

		Label lblEmail = new Label("✉ " + persona.getEmail());
		lblEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

		Label lblPassword = new Label("🔑 " + password);
		lblPassword.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

		datos.getChildren().addAll(lblUsuario, lblNombre, lblEmail, lblPassword);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// Botones
		Button btnEditar = new Button("Editar");
		btnEditar.setStyle(
				"-fx-background-color: #2563eb; -fx-text-fill: white;" +
				"-fx-background-radius: 6; -fx-padding: 7 16 7 16;" +
				"-fx-font-weight: bold; -fx-cursor: hand;");
		btnEditar.setOnAction(e -> handleEditar(persona));

		Button btnEliminar = new Button("Eliminar");
		btnEliminar.setStyle(
				"-fx-background-color: #dc2626; -fx-text-fill: white;" +
				"-fx-background-radius: 6; -fx-padding: 7 16 7 16;" +
				"-fx-font-weight: bold; -fx-cursor: hand;");
		btnEliminar.setOnAction(e -> handleEliminar(persona));

		HBox botones = new HBox(10, btnEditar, btnEliminar);
		botones.setAlignment(Pos.CENTER_RIGHT);

		card.getChildren().addAll(lblPerfil, datos, spacer, botones);
		return card;
	}

	// ── Filtros ────────────────────────────────────────────────────────────────

	/**
	 * Aplica los filtros de perfil y nombre de usuario sobre la lista completa de
	 * personas y vuelve a renderizar las cards con los resultados filtrados.
	 */
	@FXML
	private void handleFiltrar() {
		String perfilFiltro = cmbFiltroPerfil.getValue();
		String usuarioFiltro = txtFiltroUsuario.getText().trim().toLowerCase();

		List<Persona> filtradas = todasLasPersonas.stream()
				.filter(p -> {
					if ("Artista".equals(perfilFiltro) && !(p instanceof Artista)) return false;
					if ("Coordinación".equals(perfilFiltro) && !(p instanceof Coordinacion)) return false;
					return true;
				})
				.filter(p -> {
					if (usuarioFiltro.isEmpty()) return true;
					String usuario = p.getCredenciales() != null
							? p.getCredenciales().getNombreUsuario().toLowerCase() : "";
					return usuario.contains(usuarioFiltro);
				})
				.collect(Collectors.toList());

		renderizarCards(filtradas);
	}

	/**
	 * Limpia los filtros activos y vuelve a mostrar todas las personas.
	 */
	@FXML
	private void handleLimpiarFiltros() {
		cmbFiltroPerfil.setValue("Todos");
		txtFiltroUsuario.clear();
		renderizarCards(todasLasPersonas);
	}

	// ── Acciones sobre personas ────────────────────────────────────────────────

	/**
	 * Navega a la pantalla de registro de persona en modo edición para la persona
	 * indicada.
	 * <p>
	 * Almacena el ID de la persona en {@link SessionManager} siguiendo el mismo
	 * patrón que {@code setSelectedEspectaculo}, para que
	 * {@link RegistrarPersonaController} pueda recuperarla y precargar el
	 * formulario.
	 * </p>
	 *
	 * @param persona la persona a editar
	 */
	private void handleEditar(Persona persona) {
		SessionManager.setSelectedPersona(persona.getId());
		stageManager.switchScene(FxmlView.REGISTRAR_PERSONA);
	}

	/**
	 * Muestra un diálogo de confirmación y elimina la persona indicada si el
	 * usuario confirma la acción.
	 * <p>
	 * La eliminación también borra sus credenciales asociadas por cascade. Tras la
	 * eliminación recarga la lista de cards para reflejar el cambio.
	 * </p>
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
			} catch (Exception e) {
				Alert error = new Alert(AlertType.ERROR);
				error.setTitle("Error");
				error.setHeaderText("No se pudo eliminar");
				error.setContentText(e.getMessage());
				error.showAndWait();
			}
		}
	}

	// ── Navegación ─────────────────────────────────────────────────────────────

	/**
	 * Navega a la pantalla de registro de nueva persona en modo artista.
	 * <p>
	 * Limpia cualquier persona seleccionada previamente en {@link SessionManager}
	 * y almacena el tipo "Artista" para que el formulario de destino lo
	 * preseleccione.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleRegistrarArtista(ActionEvent event) {
		SessionManager.setSelectedPersona(null);
		SessionManager.setTipoRegistroPersona("Artista");
		stageManager.switchScene(FxmlView.REGISTRAR_PERSONA);
	}

	/**
	 * Navega a la pantalla de registro de nueva persona en modo coordinación.
	 * <p>
	 * Limpia cualquier persona seleccionada previamente en {@link SessionManager}
	 * y almacena el tipo "Coordinación" para que el formulario de destino lo
	 * preseleccione.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleRegistrarCoordinacion(ActionEvent event) {
		SessionManager.setSelectedPersona(null);
		SessionManager.setTipoRegistroPersona("Coordinación");
		stageManager.switchScene(FxmlView.REGISTRAR_PERSONA);
	}

	/**
	 * Navega a la pantalla de gestión de espectáculos.
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleGestionEspectaculos(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTIONAR_ESPECTACULOS);
	}

	/**
	 * Navega a la pantalla de visualización de espectáculos.
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleVerEspectaculos(ActionEvent event) {
		stageManager.switchScene(FxmlView.ESPECTACULOS);
	}

	/**
	 * Navega a la pantalla del historial de operaciones.
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleHistorial(ActionEvent event) {
		stageManager.switchScene(FxmlView.HISTORIAL);
	}

	/**
	 * Navega a la pantalla de incidencias.
	 */
	@FXML
	private void handleIncidencias() {
		stageManager.switchScene(FxmlView.INCIDENCIAS);
	}

	/**
	 * Cierra la sesión del usuario actual mostrando un diálogo de confirmación.
	 * <p>
	 * Si el usuario confirma, limpia la sesión y redirige al login.
	 * </p>
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

	/**
	 * Abre la ventana de ayuda contextual mostrando la sección de gestión de
	 * personas.
	 */
	@FXML
	private void handleAyuda() {
		helpStageManager.showHelp(FxmlView.GESTION_PERSONAS);
	}
}