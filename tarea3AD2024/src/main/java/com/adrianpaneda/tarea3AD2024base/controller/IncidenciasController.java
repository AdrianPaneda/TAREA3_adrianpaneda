package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.HelpStageManager;
import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.objectdb.Incidencia;
import com.adrianpaneda.tarea3AD2024base.objectdb.ResolucionIncidencia;
import com.adrianpaneda.tarea3AD2024base.objectdb.TipoIncidencia;
import com.adrianpaneda.tarea3AD2024base.services.CredencialesService;
import com.adrianpaneda.tarea3AD2024base.services.EspectaculoService;
import com.adrianpaneda.tarea3AD2024base.services.NumeroService;
import com.adrianpaneda.tarea3AD2024base.services.objectdb.IncidenciaService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Controlador para las pantallas de gestión de incidencias del circo.
 * <p>
 * Gestiona los casos de uso:
 * <ul>
 * <li>CU8 – Registrar nueva incidencia (todos los usuarios autenticados)</li>
 * <li>CU9 – Resolver incidencia (solo Coordinador y Administrador)</li>
 * <li>CU11 – Consultar/filtrar incidencias (todos los usuarios
 * autenticados)</li>
 * </ul>
 * </p>
 * <p>
 * Este controlador es compartido por dos vistas FXML: {@code Incidencias.fxml}
 * (consulta + resolución mediante cards desplegables) y
 * {@code RegistrarIncidencia.fxml} (formulario de alta).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 2.0
 * @since 2026-01-01
 */
@Controller
public class IncidenciasController implements Initializable {

	// ─── Vista de consulta (Incidencias.fxml) ───
	@FXML
	private VBox contenedorCards;
	@FXML
	private ComboBox<String> cmbFiltroTipo;
	@FXML
	private ComboBox<String> cmbFiltroEstado;
	@FXML
	private ComboBox<Espectaculo> cmbFiltroEspectaculo;
	@FXML
	private ComboBox<Numero> cmbFiltroNumero;
	@FXML
	private DatePicker dpFiltroDesde;
	@FXML
	private DatePicker dpFiltroHasta;
	@FXML
	private Label lblContadorResultados;

	@FXML
	private VBox panelResolucion;
	@FXML
	private Label lblTituloResolucion;
	@FXML
	private TextArea txtAccionesResolucion;
	@FXML
	private Label lblMensajeResolucion;
	@FXML
	private Button btnNuevaIncidencia;

	// ─── Vista de registro (RegistrarIncidencia.fxml) ───
	@FXML
	private ComboBox<TipoIncidencia> cmbTipo;
	@FXML
	private TextArea txtDescripcion;
	@FXML
	private Label lblContadorDesc;
	@FXML
	private ComboBox<Espectaculo> cmbEspectaculo;
	@FXML
	private ComboBox<Numero> cmbNumero;
	@FXML
	private Label lblMensaje;

	/** Incidencia seleccionada para resolver. */
	private Incidencia incidenciaSeleccionada;

	private Map<Long, String> nombreEspectaculos = new HashMap<>();
	private Map<Long, String> nombresNumeros = new HashMap<>();
	private Map<Long, String> nombresUsuarios = new HashMap<>();
	private Map<Long, ResolucionIncidencia> resolucionesPorIncidencia = new HashMap<>();

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Autowired
	private IncidenciaService incidenciaService;
	@Autowired
	private EspectaculoService espectaculoService;
	@Autowired
	private NumeroService numeroService;
	@Autowired
	private CredencialesService credencialesService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private HelpStageManager helpStageManager;

	/**
	 * Punto de entrada JavaFX: determina qué vista FXML se ha cargado por la URL y
	 * delega en el inicializador correspondiente.
	 *
	 * @param location  la URL del FXML cargado
	 * @param resources el bundle de recursos (no utilizado)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (location != null) {
			String fxmlFile = location.toString();
			if (fxmlFile.contains("Incidencias.fxml")) {
				inicializarVistaConsulta();
			} else if (fxmlFile.contains("RegistrarIncidencia.fxml")) {
				inicializarVistaRegistro();
			}
		}
	}

	// ════════════════════════════════════════════════
	// ═══ VISTA DE CONSULTA Y RESOLUCIÓN ═══
	// ════════════════════════════════════════════════

	/**
	 * Inicializa la vista de consulta y resolución de incidencias.
	 * <p>
	 * Precarga los caches de nombres, configura los filtros y carga todas las
	 * incidencias sin filtro aplicado. También configura el acceso al panel de
	 * resolución según el perfil del usuario.
	 * </p>
	 */
	private void inicializarVistaConsulta() {
		precargarCaches();
		configurarFiltros();
		cargarIncidencias(null, null, null, null, null, null);
		configurarAccesoResolucion();
	}

	/**
	 * Precarga los nombres de espectáculos, números, usuarios y resoluciones para
	 * evitar consultas repetidas al pintar las cards.
	 */
	private void precargarCaches() {
		nombreEspectaculos.clear();
		nombresNumeros.clear();
		nombresUsuarios.clear();
		resolucionesPorIncidencia.clear();

		for (Espectaculo e : espectaculoService.obtenerTodos()) {
			nombreEspectaculos.put(e.getId(), e.getNombre());
		}
		for (Numero n : numeroService.obtenerTodos()) {
			nombresNumeros.put(n.getId(), n.getNombre());
		}
		// Admin tiene id 0 (ficticio): lo precargamos
		nombresUsuarios.put(0L, "admin");
		for (Credenciales c : credencialesService.obtenerTodas()) {
			nombresUsuarios.put(c.getId(), c.getNombreUsuario());
		}
		for (ResolucionIncidencia r : incidenciaService.obtenerTodasResoluciones()) {
			if (r.getIncidencia() != null) {
				resolucionesPorIncidencia.put(r.getIncidencia().getId(), r);
			}
		}
	}

	/**
	 * Configura los ComboBox de filtros de tipo, estado, espectáculo y número con
	 * sus valores iniciales y listeners.
	 */
	private void configurarFiltros() {
		cmbFiltroTipo.setItems(FXCollections.observableArrayList("Todos", "TECNICA", "ARTISTICA", "ORGANIZATIVA"));
		cmbFiltroTipo.getSelectionModel().selectFirst();

		cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todas", "Pendiente", "Resuelta"));
		cmbFiltroEstado.getSelectionModel().selectFirst();

		ObservableList<Espectaculo> espectaculos = FXCollections.observableArrayList();
		espectaculos.add(null);
		espectaculos.addAll(espectaculoService.obtenerTodos());
		cmbFiltroEspectaculo.setItems(espectaculos);
		cmbFiltroEspectaculo.setConverter(crearConverterEspectaculo("Todos los espectáculos"));
		cmbFiltroEspectaculo.getSelectionModel().selectFirst();

		cmbFiltroNumero.setItems(FXCollections.observableArrayList((Numero) null));
		cmbFiltroNumero.setConverter(crearConverterNumero("Todos los números"));
		cmbFiltroNumero.getSelectionModel().selectFirst();

		cmbFiltroEspectaculo.valueProperty().addListener((obs, oldVal, newVal) -> {
			cargarNumerosEnCombo(cmbFiltroNumero, newVal, "Todos los números");
		});
	}

	/**
	 * Configura la visibilidad del panel de resolución y del botón "Nueva
	 * incidencia" según el estado de sesión.
	 */
	private void configurarAccesoResolucion() {
		if (panelResolucion != null) {
			panelResolucion.setVisible(false);
			panelResolucion.setManaged(false);
		}
		if (btnNuevaIncidencia != null) {
			btnNuevaIncidencia.setVisible(SessionManager.isLoggedIn());
		}
	}

	/**
	 * Consulta incidencias con los filtros indicados y renderiza los resultados
	 * como cards.
	 *
	 * @param tipo          tipo de incidencia, o {@code null} para todas
	 * @param resuelta      estado de resolución, o {@code null} para todas
	 * @param idEspectaculo id del espectáculo, o {@code null} para no filtrar
	 * @param idNumero      id del número, o {@code null} para no filtrar
	 * @param fechaInicio   límite inferior de fecha, o {@code null}
	 * @param fechaFin      límite superior de fecha, o {@code null}
	 */
	private void cargarIncidencias(TipoIncidencia tipo, Boolean resuelta, Long idEspectaculo, Long idNumero,
			Date fechaInicio, Date fechaFin) {
		List<Incidencia> lista = incidenciaService.consultarConFiltros(tipo, resuelta, idEspectaculo, idNumero,
				fechaInicio, fechaFin);
		renderizarCards(lista);
		if (lblContadorResultados != null) {
			lblContadorResultados.setText(lista.size() + " incidencia" + (lista.size() != 1 ? "s" : ""));
		}
	}

	/**
	 * Vacía el contenedor de cards y genera una card por cada incidencia.
	 */
	private void renderizarCards(List<Incidencia> incidencias) {
		contenedorCards.getChildren().clear();

		if (incidencias.isEmpty()) {
			Label lblVacio = new Label("No se encontraron incidencias con los filtros aplicados.");
			lblVacio.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8; -fx-padding: 40;");
			VBox box = new VBox(lblVacio);
			box.setAlignment(Pos.CENTER);
			contenedorCards.getChildren().add(box);
			return;
		}

		for (Incidencia inc : incidencias) {
			contenedorCards.getChildren().add(construirCard(inc));
		}
	}

	/**
	 * Construye una card desplegable para una incidencia, con la información básica
	 * en el header y los detalles ocultos por defecto.
	 */
	private VBox construirCard(Incidencia inc) {
		VBox card = new VBox(0);
		card.setStyle("-fx-background-color: #fef3c7; -fx-background-radius: 12; "
				+ "-fx-border-color: #f59e0b; -fx-border-width: 1.5; -fx-border-radius: 12;");
		card.setPadding(new Insets(15, 18, 15, 18));

		// ─── HEADER (siempre visible) ───
		HBox header = new HBox(15);
		header.setAlignment(Pos.CENTER_LEFT);
		header.setStyle("-fx-cursor: hand;");

		Label lblFecha = new Label(SDF.format(inc.getFechaHora()));
		lblFecha.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #78350f;");
		lblFecha.setMinWidth(140);

		String nombreReporta = nombresUsuarios.getOrDefault(inc.getIdPersonaReporta(),
				"ID " + inc.getIdPersonaReporta());
		Label lblReporta = new Label("👤 " + nombreReporta);
		lblReporta.setStyle("-fx-font-size: 13px; -fx-text-fill: #78350f;");
		lblReporta.setMinWidth(160);

		String nombreEsp = inc.getIdEspectaculo() == null ? null
				: nombreEspectaculos.getOrDefault(inc.getIdEspectaculo(), "ID " + inc.getIdEspectaculo());
		Label lblEsp = new Label(nombreEsp == null ? "" : "🎪 " + nombreEsp);
		lblEsp.setStyle("-fx-font-size: 13px; -fx-text-fill: #78350f;");
		lblEsp.setMinWidth(180);

		String nombreNum = inc.getIdNumero() == null ? null
				: nombresNumeros.getOrDefault(inc.getIdNumero(), "ID " + inc.getIdNumero());
		Label lblNum = new Label(nombreNum == null ? "" : "🎭 " + nombreNum);
		lblNum.setStyle("-fx-font-size: 13px; -fx-text-fill: #78350f;");
		lblNum.setMinWidth(160);

		Label lblEstado = new Label(inc.isResuelta() ? "✓ Resuelta" : "⚠ Pendiente");
		lblEstado.setStyle(inc.isResuelta()
				? "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a; -fx-padding: 4 10 4 10; "
						+ "-fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold;"
				: "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 4 10 4 10; "
						+ "-fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		Label lblFlecha = new Label("▼");
		lblFlecha.setStyle("-fx-font-size: 14px; -fx-text-fill: #78350f; -fx-font-weight: bold;");

		header.getChildren().addAll(lblFecha, lblReporta, lblEsp, lblNum, spacer, lblEstado, lblFlecha);

		// ─── CONTENIDO desplegable (oculto por defecto) ───
		VBox contenido = new VBox(10);
		contenido.setPadding(new Insets(15, 0, 0, 0));
		contenido.setVisible(false);
		contenido.setManaged(false);

		// Tipo de incidencia
		Label lblTipo = new Label(inc.getTipo().toString());
		lblTipo.setStyle("-fx-background-color: " + colorTipo(inc.getTipo()) + "; "
				+ "-fx-text-fill: white; -fx-padding: 4 12 4 12; -fx-background-radius: 6; "
				+ "-fx-font-size: 11px; -fx-font-weight: bold;");
		HBox boxTipo = new HBox(lblTipo);
		boxTipo.setAlignment(Pos.CENTER_LEFT);

		// Descripción
		Label lblDescTitle = new Label("Descripción");
		lblDescTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #78350f;");

		Label lblDesc = new Label(inc.getDescripcion());
		lblDesc.setWrapText(true);
		lblDesc.setStyle("-fx-font-size: 14px; -fx-text-fill: #1e293b; "
				+ "-fx-background-color: rgba(255,255,255,0.6); -fx-padding: 10; " + "-fx-background-radius: 6;");
		lblDesc.setMaxWidth(Double.MAX_VALUE);

		contenido.getChildren().addAll(boxTipo, lblDescTitle, lblDesc);

		// Si está resuelta, mostrar la resolución
		if (inc.isResuelta()) {
			ResolucionIncidencia res = resolucionesPorIncidencia.get(inc.getId());
			if (res != null) {
				Separator sep = new Separator();
				sep.setPadding(new Insets(8, 0, 0, 0));

				Label lblResTitle = new Label("─── RESOLUCIÓN ───");
				lblResTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");

				String nombreResuelve = nombresUsuarios.getOrDefault(res.getIdPersonaResuelve(),
						"ID " + res.getIdPersonaResuelve());

				Label lblAccionesTitle = new Label("Acciones realizadas:");
				lblAccionesTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #78350f;");

				Label lblAcciones = new Label(res.getAccionesRealizadas());
				lblAcciones.setWrapText(true);
				lblAcciones.setStyle("-fx-font-size: 14px; -fx-text-fill: #1e293b; "
						+ "-fx-background-color: #f0fdf4; -fx-padding: 10; -fx-background-radius: 6;");
				lblAcciones.setMaxWidth(Double.MAX_VALUE);

				contenido.getChildren().addAll(sep, lblResTitle, crearLineaInfo("✓ Resuelta por:", nombreResuelve),
						crearLineaInfo("📅 Fecha resolución:", SDF.format(res.getFechahoraResolucion())),
						lblAccionesTitle, lblAcciones);
			}
		} else if (puedeResolverIncidencias()) {
			// Botón resolver para admin/coordinacion
			Separator sep = new Separator();
			sep.setPadding(new Insets(8, 0, 0, 0));

			Button btnResolver = new Button("RESOLVER INCIDENCIA");
			btnResolver.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; "
					+ "-fx-background-radius: 8; -fx-padding: 10 24 10 24; "
					+ "-fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand;");
			btnResolver.setOnAction(e -> handleAbrirResolucion(inc));

			HBox btnBox = new HBox(btnResolver);
			btnBox.setAlignment(Pos.CENTER);
			btnBox.setPadding(new Insets(8, 0, 0, 0));

			contenido.getChildren().addAll(sep, btnBox);
		}

		// Click en header → toggle del contenido
		header.setOnMouseClicked(e -> {
			boolean nuevoVisible = !contenido.isVisible();
			contenido.setVisible(nuevoVisible);
			contenido.setManaged(nuevoVisible);
			lblFlecha.setText(nuevoVisible ? "▲" : "▼");
		});

		card.getChildren().addAll(header, contenido);
		return card;
	}

	/**
	 * Crea una línea de información con etiqueta y valor en formato HBox.
	 */
	private HBox crearLineaInfo(String etiqueta, String valor) {
		Label lblEtq = new Label(etiqueta);
		lblEtq.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #78350f;");
		lblEtq.setMinWidth(170);

		Label lblValor = new Label(valor);
		lblValor.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");
		lblValor.setWrapText(true);

		HBox box = new HBox(8, lblEtq, lblValor);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}

	/** Devuelve el color de fondo del badge según el tipo de incidencia. */
	private String colorTipo(TipoIncidencia tipo) {
		return switch (tipo) {
		case TECNICA -> "#3b82f6";
		case ARTISTICA -> "#8b5cf6";
		case ORGANIZATIVA -> "#f59e0b";
		};
	}

	/**
	 * Aplica los filtros seleccionados en la vista y recarga las incidencias.
	 */
	@FXML
	private void handleFiltrar() {
		TipoIncidencia tipo = parseTipoFiltro();
		Boolean resuelta = parseEstadoFiltro();
		Espectaculo esp = cmbFiltroEspectaculo == null ? null : cmbFiltroEspectaculo.getValue();
		Numero num = cmbFiltroNumero == null ? null : cmbFiltroNumero.getValue();
		Long idEsp = esp == null ? null : esp.getId();
		Long idNum = num == null ? null : num.getId();
		Date desde = parseDatePicker(dpFiltroDesde, false);
		Date hasta = parseDatePicker(dpFiltroHasta, true);
		cargarIncidencias(tipo, resuelta, idEsp, idNum, desde, hasta);
	}

	/**
	 * Restablece todos los filtros a su estado inicial y recarga todas las
	 * incidencias sin filtro.
	 */
	@FXML
	private void handleLimpiarFiltros() {
		cmbFiltroTipo.getSelectionModel().selectFirst();
		cmbFiltroEstado.getSelectionModel().selectFirst();
		cmbFiltroEspectaculo.getSelectionModel().selectFirst();
		cmbFiltroNumero.setItems(FXCollections.observableArrayList((Numero) null));
		cmbFiltroNumero.getSelectionModel().selectFirst();
		dpFiltroDesde.setValue(null);
		dpFiltroHasta.setValue(null);
		cargarIncidencias(null, null, null, null, null, null);
	}

	/**
	 * Navega a la pantalla de registro de nueva incidencia (CU8).
	 */
	@FXML
	private void handleNuevaIncidencia() {
		stageManager.switchScene(FxmlView.REGISTRAR_INCIDENCIA);
	}

	/**
	 * Abre el panel lateral de resolución para la incidencia indicada.
	 *
	 * @param incidencia la incidencia que se va a resolver
	 */
	private void handleAbrirResolucion(Incidencia incidencia) {
		incidenciaSeleccionada = incidencia;
		if (panelResolucion != null) {
			lblTituloResolucion.setText("RESOLVER INCIDENCIA #" + incidencia.getId());
			txtAccionesResolucion.clear();
			lblMensajeResolucion.setText("");
			panelResolucion.setVisible(true);
			panelResolucion.setManaged(true);
		}
	}

	/**
	 * Confirma la resolución de la incidencia seleccionada con las acciones
	 * introducidas. Muestra un error si el campo de acciones está vacío.
	 */
	@FXML
	private void handleConfirmarResolucion() {
		if (incidenciaSeleccionada == null)
			return;

		String acciones = txtAccionesResolucion.getText().trim();
		if (acciones.isEmpty()) {
			lblMensajeResolucion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
			lblMensajeResolucion.setText("Debes describir las acciones realizadas.");
			return;
		}

		Long idPersonaResuelve = obtenerIdPersonaSesion();

		try {
			incidenciaService.resolver(incidenciaSeleccionada.getId(), acciones, idPersonaResuelve);
			incidenciaSeleccionada = null;
			precargarCaches();
			handleFiltrar();
			panelResolucion.setVisible(false);
			panelResolucion.setManaged(false);
			txtAccionesResolucion.clear();
		} catch (IllegalArgumentException ex) {
			lblMensajeResolucion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
			lblMensajeResolucion.setText(ex.getMessage());
		} catch (Exception ex) {
			lblMensajeResolucion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
			lblMensajeResolucion.setText("Error al resolver: " + ex.getMessage());
		}
	}

	/**
	 * Cancela la resolución en curso, cierra el panel lateral y limpia el estado.
	 */
	@FXML
	private void handleCancelarResolucion() {
		incidenciaSeleccionada = null;
		if (panelResolucion != null) {
			panelResolucion.setVisible(false);
			panelResolucion.setManaged(false);
			txtAccionesResolucion.clear();
			lblMensajeResolucion.setText("");
		}
	}

	/**
	 * Navega a la pantalla principal del usuario según su perfil.
	 */
	@FXML
	private void handleVolver() {
		navegarPantallaPrincipal();
	}

	/**
	 * Abre la ventana de ayuda contextual mostrando la sección de incidencias.
	 */
	@FXML
	private void handleAyuda() {
		helpStageManager.showHelp(FxmlView.INCIDENCIAS);
	}

	// ════════════════════════════════════════════════
	// ═══ VISTA DE REGISTRO (CU8) — SIN CAMBIOS ═══
	// ════════════════════════════════════════════════

	/**
	 * Inicializa la vista de registro de incidencias (CU8).
	 * <p>
	 * Carga los combos de tipo, espectáculo y número, configura el listener de
	 * espectáculo para filtrar los números disponibles y activa el contador de
	 * caracteres de la descripción.
	 * </p>
	 */
	private void inicializarVistaRegistro() {
		cmbTipo.setItems(FXCollections.observableArrayList(TipoIncidencia.values()));

		ObservableList<Espectaculo> espectaculos = FXCollections.observableArrayList();
		espectaculos.add(null);
		espectaculos.addAll(espectaculoService.obtenerTodos());
		cmbEspectaculo.setItems(espectaculos);
		cmbEspectaculo.setConverter(crearConverterEspectaculo("Ninguno"));
		cmbEspectaculo.getSelectionModel().selectFirst();

		cmbNumero.setItems(FXCollections.observableArrayList((Numero) null));
		cmbNumero.setConverter(crearConverterNumero("Ninguno"));
		cmbNumero.getSelectionModel().selectFirst();
		cmbNumero.setDisable(true);

		cmbEspectaculo.valueProperty().addListener((obs, oldVal, newVal) -> {
			cargarNumerosEnCombo(cmbNumero, newVal, "Ninguno");
			cmbNumero.setDisable(newVal == null);
		});

		txtDescripcion.textProperty().addListener((obs, oldVal, newVal) -> {
			int len = newVal == null ? 0 : newVal.length();
			if (len > 1000) {
				txtDescripcion.setText(oldVal);
				return;
			}
			lblContadorDesc.setText(len + " / 1000");
			lblContadorDesc.setStyle(len > 900 ? "-fx-font-size: 12px; -fx-text-fill: #dc2626;"
					: "-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
		});
	}

	/**
	 * Valida el formulario y registra una nueva incidencia en el sistema (CU8).
	 * Muestra el resultado del registro o el error correspondiente.
	 */
	@FXML
	private void handleRegistrar() {
		if (cmbTipo.getValue() == null) {
			mostrarMensaje("Debes seleccionar el tipo de incidencia.", true);
			return;
		}
		String desc = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
		if (desc.isEmpty()) {
			mostrarMensaje("La descripción no puede estar vacía.", true);
			return;
		}
		if (desc.length() > 1000) {
			mostrarMensaje("La descripción supera los 1000 caracteres.", true);
			return;
		}

		Espectaculo esp = cmbEspectaculo == null ? null : cmbEspectaculo.getValue();
		Numero num = cmbNumero == null ? null : cmbNumero.getValue();

		Long idEsp = esp == null ? null : esp.getId();
		Long idNum = num == null ? null : num.getId();

		Incidencia incidencia = new Incidencia();
		incidencia.setTipo(cmbTipo.getValue());
		incidencia.setDescripcion(desc);
		incidencia.setIdPersonaReporta(obtenerIdPersonaSesion());
		incidencia.setIdEspectaculo(idEsp);
		incidencia.setIdNumero(idNum);

		try {
			incidenciaService.registrar(incidencia);
			mostrarMensaje("✓ Incidencia registrada correctamente (ID: " + incidencia.getId() + ")", false);
			limpiarFormulario();
		} catch (Exception e) {
			mostrarMensaje("Error al registrar: " + e.getMessage(), true);
		}
	}

	/**
	 * Restablece todos los campos del formulario de registro a su estado inicial.
	 */
	private void limpiarFormulario() {
		cmbTipo.getSelectionModel().clearSelection();
		txtDescripcion.clear();
		if (cmbEspectaculo != null)
			cmbEspectaculo.getSelectionModel().selectFirst();
		if (cmbNumero != null) {
			cmbNumero.setItems(FXCollections.observableArrayList((Numero) null));
			cmbNumero.getSelectionModel().selectFirst();
			cmbNumero.setDisable(true);
		}
	}

	/**
	 * Navega de vuelta a la pantalla de consulta de incidencias.
	 */
	@FXML
	private void handleVolverDesdeRegistro() {
		stageManager.switchScene(FxmlView.INCIDENCIAS);
	}

	// ════════════════════════════════════════════════
	// ═══ UTILIDADES ═══
	// ════════════════════════════════════════════════

	/**
	 * Carga los números del espectáculo dado en el combo indicado, añadiendo una
	 * opción nula como primera entrada.
	 *
	 * @param combo        el ComboBox de números a actualizar
	 * @param espectaculo  el espectáculo cuyos números se listan, o {@code null}
	 *                     para dejar solo la opción vacía
	 * @param etiquetaNull texto que se mostrará cuando el valor sea {@code null}
	 */
	private void cargarNumerosEnCombo(ComboBox<Numero> combo, Espectaculo espectaculo, String etiquetaNull) {
		ObservableList<Numero> items = FXCollections.observableArrayList();
		items.add(null);
		if (espectaculo != null) {
			items.addAll(numeroService.obtenerPorEspectaculo(espectaculo.getId()));
		}
		combo.setItems(items);
		combo.setConverter(crearConverterNumero(etiquetaNull));
		combo.getSelectionModel().selectFirst();
	}

	/**
	 * Crea un {@link StringConverter} para {@link Espectaculo} que muestra el
	 * nombre del espectáculo o la etiqueta indicada cuando el valor es
	 * {@code null}.
	 *
	 * @param etiquetaNull texto a mostrar para la opción vacía
	 * @return el converter listo para asignar a un ComboBox
	 */
	private StringConverter<Espectaculo> crearConverterEspectaculo(String etiquetaNull) {
		return new StringConverter<>() {
			@Override
			public String toString(Espectaculo e) {
				return (e == null) ? etiquetaNull : e.getNombre();
			}

			@Override
			public Espectaculo fromString(String s) {
				return null;
			}
		};
	}

	/**
	 * Crea un {@link StringConverter} para {@link Numero} que muestra el nombre del
	 * número o la etiqueta indicada cuando el valor es {@code null}.
	 *
	 * @param etiquetaNull texto a mostrar para la opción vacía
	 * @return el converter listo para asignar a un ComboBox
	 */
	private StringConverter<Numero> crearConverterNumero(String etiquetaNull) {
		return new StringConverter<>() {
			@Override
			public String toString(Numero n) {
				return (n == null) ? etiquetaNull : n.getNombre();
			}

			@Override
			public Numero fromString(String s) {
				return null;
			}
		};
	}

	/**
	 * Devuelve el identificador de la persona de la sesión activa, o {@code 0L} si
	 * no hay sesión iniciada o el id no está disponible.
	 *
	 * @return id de la persona en sesión
	 */
	private Long obtenerIdPersonaSesion() {
		if (!SessionManager.isLoggedIn())
			return 0L;
		Long id = SessionManager.getCurrentUser().getId();
		return id != null ? id : 0L;
	}

	/**
	 * Indica si el usuario en sesión tiene permiso para resolver incidencias.
	 *
	 * @return {@code true} si el perfil es administrador o coordinación
	 */
	private boolean puedeResolverIncidencias() {
		Perfil perfil = SessionManager.getCurrentPerfil();
		return perfil == Perfil.admin || perfil == Perfil.coordinacion;
	}

	/**
	 * Convierte el valor seleccionado en {@code cmbFiltroTipo} a
	 * {@link TipoIncidencia}.
	 *
	 * @return el tipo de incidencia seleccionado, o {@code null} si es "Todos" o no
	 *         hay selección
	 */
	private TipoIncidencia parseTipoFiltro() {
		if (cmbFiltroTipo == null)
			return null;
		String val = cmbFiltroTipo.getValue();
		if (val == null || val.equals("Todos"))
			return null;
		try {
			return TipoIncidencia.valueOf(val);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Convierte el valor seleccionado en {@code cmbFiltroEstado} a un booleano.
	 *
	 * @return {@code true} si está "Resuelta", {@code false} si "Pendiente", o
	 *         {@code null} si es "Todas"
	 */
	private Boolean parseEstadoFiltro() {
		if (cmbFiltroEstado == null)
			return null;
		String val = cmbFiltroEstado.getValue();
		if (val == null || val.equals("Todas"))
			return null;
		return val.equals("Resuelta");
	}

	/**
	 * Convierte el valor de un {@link DatePicker} a {@link Date}.
	 *
	 * @param dp       el DatePicker a leer
	 * @param endOfDay si es {@code true}, suma un día para incluir todo el día
	 *                 final
	 * @return la fecha convertida, o {@code null} si el picker está vacío
	 */
	private Date parseDatePicker(DatePicker dp, boolean endOfDay) {
		if (dp == null || dp.getValue() == null)
			return null;
		LocalDate ld = dp.getValue();
		if (endOfDay) {
			ld = ld.plusDays(1);
		}
		return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Muestra un mensaje en la etiqueta de estado del formulario de registro con el
	 * estilo visual adecuado al tipo de resultado.
	 *
	 * @param texto   el texto a mostrar
	 * @param esError {@code true} para mostrar en rojo (error), {@code false} para
	 *                verde (éxito)
	 */
	private void mostrarMensaje(String texto, boolean esError) {
		if (lblMensaje == null)
			return;
		lblMensaje.setText(texto);
		lblMensaje.setStyle(esError ? "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626;"
				: "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
	}

	/**
	 * Redirige al usuario a su pantalla principal según el perfil: artista → ficha
	 * artista, coordinación → gestión de espectáculos, admin → gestión de personas.
	 * Sin sesión activa navega al login.
	 */
	private void navegarPantallaPrincipal() {
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
			stageManager.switchScene(FxmlView.GESTION_PERSONAS);
		}
	}
}