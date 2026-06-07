package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.Evaluacion;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.Evaluador;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.NivelEvaluacion;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.Observacion;
import com.adrianpaneda.tarea3AD2024base.services.PersonaService;
import com.adrianpaneda.tarea3AD2024base.services.dossier.DossierService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Controlador para la pantalla de dossiers artísticos (CU12).
 * <p>
 * Permite a usuarios con perfil Coordinación o Administrador visualizar
 * artistas y añadir valoraciones u observaciones a sus dossiers en MongoDB.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 */
@Controller
public class DossierArtistasController implements Initializable {

	// ── Búsqueda ──────────────────────────────────────────────────────────────

	@FXML
	private TextField txtBuscarArtista;

	@FXML
	private Label lblContadorArtistas;

	// ── Contenedor de cards ────────────────────────────────────────────────────

	@FXML
	private VBox contenedorCards;

	// ── Botones ───────────────────────────────────────────────────────────────

	@FXML
	private Button btnVolver;

	@FXML
	private Button btnCerrarSesion;

	// ── Overlay valoración ────────────────────────────────────────────────────

	@FXML
	private StackPane overlayValoracion;

	@FXML
	private Label lblTituloValoracion;

	@FXML
	private TextArea txtComentarioValoracion;

	@FXML
	private ComboBox<NivelEvaluacion> cmbNivelValoracion;

	// ── Overlay observación ───────────────────────────────────────────────────

	@FXML
	private StackPane overlayObservacion;

	@FXML
	private Label lblTituloObservacion;

	@FXML
	private TextArea txtTextoObservacion;

	@FXML
	private Label lblErrorValoracion;
	@FXML
	private Label lblErrorObservacion;

	// ── Dependencias ──────────────────────────────────────────────────────────

	@Autowired
	private PersonaService personaService;

	@Autowired
	private DossierService dossierServ;

	@Lazy
	@Autowired
	private StageManager stageManager;

	/** Artista seleccionado para valoración/observación */
	private Artista artistaSeleccionado;

	/** Lista completa de artistas */
	private List<Artista> todosLosArtistas;

	// ── Inicialización ────────────────────────────────────────────────────────

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		validarAcceso();
		cargarArtistas();
		cmbNivelValoracion.setItems(FXCollections.observableArrayList(NivelEvaluacion.values()));
		configurarBotonInferior();
	}

	private void validarAcceso() {
		Perfil perfil = SessionManager.getCurrentPerfil();
		if (perfil != Perfil.coordinacion && perfil != Perfil.admin) {
			stageManager.switchScene(FxmlView.LOGIN);
		}
	}

	private void configurarBotonInferior() {
		if (SessionManager.getCurrentPerfil() == Perfil.admin) {
			btnCerrarSesion.setText("VOLVER A PERSONAS");
		}
	}

	// ── Carga de artistas ─────────────────────────────────────────────────────

	private void cargarArtistas() {
		todosLosArtistas = personaService.obtenerTodas().stream().filter(p -> p instanceof Artista)
				.map(p -> (Artista) p).collect(Collectors.toList());
		renderizarCards(todosLosArtistas);
	}

	private void renderizarCards(List<Artista> artistas) {
		contenedorCards.getChildren().clear();
		lblContadorArtistas.setText(artistas.size() + " artistas");
		for (Artista artista : artistas) {
			contenedorCards.getChildren().add(crearCard(artista));
		}
	}

	// ── Construcción de cards ─────────────────────────────────────────────────

	private HBox crearCard(Artista artista) {
		HBox card = new HBox(20);
		card.setAlignment(Pos.CENTER_LEFT);
		card.setPadding(new Insets(16, 20, 16, 20));
		card.setStyle("-fx-background-color: white;" + "-fx-border-color: #e2e8f0;" + "-fx-border-width: 1.5;"
				+ "-fx-border-radius: 12;" + "-fx-background-radius: 12;"
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

		// Etiqueta perfil
		Label lblPerfil = new Label("ARTISTA");
		lblPerfil.setFont(Font.font("System Bold", 11));
		lblPerfil.setStyle("-fx-text-fill: white;" + "-fx-background-color: #2563eb;" + "-fx-background-radius: 6;"
				+ "-fx-padding: 4 10 4 10;");
		lblPerfil.setMinWidth(110);

		// Datos del artista
		VBox datos = new VBox(4);
		HBox.setHgrow(datos, Priority.ALWAYS);

		Label lblNombre = new Label(artista.getNombre());
		lblNombre.setFont(Font.font("System Bold", 14));
		lblNombre.setStyle("-fx-text-fill: #0f172a;");

		Label lblEmail = new Label("✉ " + artista.getEmail());
		lblEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

		Label lblNacionalidad = new Label("🌍 " + artista.getNacionalidad());
		lblNacionalidad.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

		String especialidadesStr = artista.getEspecialidades() != null
				? artista.getEspecialidades().stream().map(Especialidad::name).collect(Collectors.joining(", "))
				: "-";
		Label lblEspecialidades = new Label("🎪 " + especialidadesStr);
		lblEspecialidades.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

		if (artista.getApodo() != null && !artista.getApodo().isEmpty()) {
			Label lblApodo = new Label("🎭 " + artista.getApodo());
			lblApodo.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
			datos.getChildren().addAll(lblNombre, lblEmail, lblNacionalidad, lblEspecialidades, lblApodo);
		} else {
			datos.getChildren().addAll(lblNombre, lblEmail, lblNacionalidad, lblEspecialidades);
		}

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// Botones
		Button btnValoracion = new Button("Valoración+");
		btnValoracion.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white;"
				+ "-fx-background-radius: 6; -fx-padding: 7 16 7 16;" + "-fx-font-weight: bold; -fx-cursor: hand;");
		btnValoracion.setOnAction(e -> handleValoracion(artista));

		Button btnObservacion = new Button("Observación+");
		btnObservacion.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white;"
				+ "-fx-background-radius: 6; -fx-padding: 7 16 7 16;" + "-fx-font-weight: bold; -fx-cursor: hand;");
		btnObservacion.setOnAction(e -> handleObservacion(artista));

		HBox botones = new HBox(10);
		botones.setAlignment(Pos.CENTER_RIGHT);
		botones.getChildren().addAll(btnValoracion, btnObservacion);

		card.getChildren().addAll(lblPerfil, datos, spacer, botones);
		return card;
	}

	// ── Búsqueda ──────────────────────────────────────────────────────────────

	@FXML
	private void handleBuscar() {
		String filtro = txtBuscarArtista.getText().trim().toLowerCase();
		if (filtro.isEmpty()) {
			renderizarCards(todosLosArtistas);
			return;
		}
		List<Artista> filtrados = todosLosArtistas.stream().filter(a -> a.getNombre().toLowerCase().contains(filtro))
				.collect(Collectors.toList());
		renderizarCards(filtrados);
	}

	@FXML
	private void handleLimpiarBusqueda() {
		txtBuscarArtista.clear();
		renderizarCards(todosLosArtistas);
	}

	// ── Valoración ────────────────────────────────────────────────────────────

	private void handleValoracion(Artista artista) {
		artistaSeleccionado = artista;
		lblTituloValoracion.setText("NUEVA VALORACIÓN — " + artista.getNombre());
		txtComentarioValoracion.clear();
		cmbNivelValoracion.setValue(null);
		overlayValoracion.setVisible(true);
		overlayValoracion.setManaged(true);
	}

	@FXML
	private void handleGuardarValoracion() {
		String comentario = txtComentarioValoracion.getText().trim();
		NivelEvaluacion nivel = cmbNivelValoracion.getValue();

		if (comentario.isEmpty() && nivel == null) {
			lblErrorValoracion.setText("Debes rellenar el comentario y seleccionar un nivel.");
			return;
		}
		if (comentario.isEmpty()) {
			lblErrorValoracion.setText("El comentario es obligatorio.");
			return;
		}
		if (nivel == null) {
			lblErrorValoracion.setText("Debes seleccionar un nivel.");
			return;
		}
		lblErrorValoracion.setText("");

		Credenciales current = SessionManager.getCurrentUser();
		Evaluador evaluador = new Evaluador(SessionManager.getCurrentUser().getId(), current.getPerfil());
		Evaluacion evaluacion = new Evaluacion(LocalDateTime.now(), evaluador, comentario, nivel);
		dossierServ.agregarEvaluacion(artistaSeleccionado.getId(), evaluacion);
		handleCancelarValoracion();
	}

	@FXML
	private void handleGuardarObservacion() {
		String texto = txtTextoObservacion.getText().trim();

		if (texto.isEmpty()) {
			lblErrorObservacion.setText("El texto de la observación es obligatorio.");
			return;
		}
		lblErrorObservacion.setText("");

		Observacion observacion = new Observacion(LocalDateTime.now(), texto, SessionManager.getCurrentUsername());
		dossierServ.agregarObservacion(artistaSeleccionado.getId(), observacion);
		handleCancelarObservacion();
	}

	@FXML
	private void handleCancelarValoracion() {
		overlayValoracion.setVisible(false);
		overlayValoracion.setManaged(false);
		artistaSeleccionado = null;
	}

	// ── Observación ───────────────────────────────────────────────────────────

	private void handleObservacion(Artista artista) {
		artistaSeleccionado = artista;
		lblTituloObservacion.setText("NUEVA OBSERVACIÓN — " + artista.getNombre());
		txtTextoObservacion.clear();
		overlayObservacion.setVisible(true);
		overlayObservacion.setManaged(true);
	}

	@FXML
	private void handleCancelarObservacion() {
		overlayObservacion.setVisible(false);
		overlayObservacion.setManaged(false);
		artistaSeleccionado = null;
	}

	// ── Navegación ────────────────────────────────────────────────────────────

	@FXML
	private void handleVolver() {
		stageManager.switchScene(FxmlView.GESTIONAR_ESPECTACULOS);
	}

	@FXML
	private void handleCerrarSesion() {
		if (SessionManager.getCurrentPerfil() == Perfil.admin) {
			stageManager.switchScene(FxmlView.GESTION_PERSONAS);
		} else {
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
}