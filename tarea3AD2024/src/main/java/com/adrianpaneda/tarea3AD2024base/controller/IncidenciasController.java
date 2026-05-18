package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.objectdb.Incidencia;
import com.adrianpaneda.tarea3AD2024base.objectdb.TipoIncidencia;
import com.adrianpaneda.tarea3AD2024base.services.objectdb.IncidenciaService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

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
 * (consulta + resolución) y {@code RegistrarIncidencia.fxml} (formulario de
 * alta). Spring gestiona este bean como singleton, por lo que los campos
 * {@code @FXML} pueden ser {@code null} dependiendo de qué vista está activa;
 * se comprueban antes de usar.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2026-01-01
 */
@Controller
public class IncidenciasController implements Initializable {

	@FXML
	private TableView<Incidencia> tablaIncidencias;
	@FXML
	private TableColumn<Incidencia, Long> colId;
	@FXML
	private TableColumn<Incidencia, Date> colFechaHora;
	@FXML
	private TableColumn<Incidencia, TipoIncidencia> colTipo;
	@FXML
	private TableColumn<Incidencia, String> colDescripcion;
	@FXML
	private TableColumn<Incidencia, Boolean> colResuelta;
	@FXML
	private TableColumn<Incidencia, Long> colIdPersona;
	@FXML
	private TableColumn<Incidencia, Void> colAcciones;

	@FXML
	private ComboBox<String> cmbFiltroTipo;
	@FXML
	private ComboBox<String> cmbFiltroEstado;
	@FXML
	private TextField txtFiltroEspectaculo;
	@FXML
	private TextField txtFiltroNumero;
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
	@FXML
	private ComboBox<TipoIncidencia> cmbTipo;
	@FXML
	private TextArea txtDescripcion;
	@FXML
	private Label lblContadorDesc;
	@FXML
	private TextField txtIdEspectaculo;
	@FXML
	private TextField txtIdNumero;
	@FXML
	private Label lblMensaje;

	/** Incidencia seleccionada en la tabla para ser resuelta. */
	private Incidencia incidenciaSeleccionada;

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Autowired
	private IncidenciaService incidenciaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

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

	private void inicializarVistaConsulta() {
		configurarFiltros();
		configurarColumnas();
		cargarIncidencias(null, null, null, null, null, null);
		configurarAccesoResolucion();
	}

	private void configurarFiltros() {
		cmbFiltroTipo.setItems(FXCollections.observableArrayList("Todos", "TECNICA", "ARTISTICA", "ORGANIZATIVA"));
		cmbFiltroTipo.getSelectionModel().selectFirst();

		cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todas", "Pendiente", "Resuelta"));
		cmbFiltroEstado.getSelectionModel().selectFirst();
	}

	private void configurarColumnas() {
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
		colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
		colResuelta.setCellValueFactory(new PropertyValueFactory<>("resuelta"));
		colIdPersona.setCellValueFactory(new PropertyValueFactory<>("idPersonaReporta"));

		// Columna fecha formateada
		colFechaHora.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
		colFechaHora.setCellFactory(col -> new TableCell<>() {
			@Override
			protected void updateItem(Date item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? null : SDF.format(item));
			}
		});

		// Columna estado con badge de color
		colResuelta.setCellFactory(col -> new TableCell<>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else if (item) {
					setText("✓ Resuelta");
					setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
				} else {
					setText("⚠ Pendiente");
					setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
				}
			}
		});

		// Columna acciones (resolver) — solo visible para admin y coordinacion
		configurarColumnaAcciones();
	}

	private void configurarColumnaAcciones() {
		boolean puedeResolver = puedeResolverIncidencias();

		colAcciones.setCellFactory(col -> new TableCell<>() {
			private final Button btn = new Button("Resolver");
			{
				btn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; "
						+ "-fx-background-radius: 6; -fx-padding: 5 10 5 10; "
						+ "-fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 12px;");
				btn.setOnAction(e -> {
					Incidencia inc = getTableView().getItems().get(getIndex());
					handleAbrirResolucion(inc);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					Incidencia inc = getTableView().getItems().get(getIndex());
					// Mostrar botón solo si puede resolver y la incidencia no está resuelta
					setGraphic(puedeResolver && !inc.isResuelta() ? btn : null);
				}
			}
		});
	}

	private void configurarAccesoResolucion() {
		// El panel de resolución parte oculto; se muestra al pulsar "Resolver" en una
		// fila
		if (panelResolucion != null) {
			panelResolucion.setVisible(false);
		}
		// El botón "Nueva incidencia" siempre visible para usuarios autenticados
		if (btnNuevaIncidencia != null) {
			btnNuevaIncidencia.setVisible(SessionManager.isLoggedIn());
		}
	}

	private void cargarIncidencias(TipoIncidencia tipo, Boolean resuelta, Long idEspectaculo, Long idNumero,
			Date fechaInicio, Date fechaFin) {
		List<Incidencia> lista = incidenciaService.consultarConFiltros(tipo, resuelta, idEspectaculo, idNumero,
				fechaInicio, fechaFin);
		ObservableList<Incidencia> obs = FXCollections.observableArrayList(lista);
		tablaIncidencias.setItems(obs);
		if (lblContadorResultados != null) {
			lblContadorResultados.setText(lista.size() + " incidencia" + (lista.size() != 1 ? "s" : ""));
		}
	}

	/**
	 * CU11 – Aplica los filtros seleccionados y recarga la tabla.
	 */
	@FXML
	private void handleFiltrar() {
		TipoIncidencia tipo = parseTipoFiltro();
		Boolean resuelta = parseEstadoFiltro();
		Long idEsp = parseLongField(txtFiltroEspectaculo);
		Long idNum = parseLongField(txtFiltroNumero);
		Date desde = parseDatePicker(dpFiltroDesde, false);
		Date hasta = parseDatePicker(dpFiltroHasta, true);
		cargarIncidencias(tipo, resuelta, idEsp, idNum, desde, hasta);
	}

	/** Limpia todos los filtros y recarga sin restricciones. */
	@FXML
	private void handleLimpiarFiltros() {
		cmbFiltroTipo.getSelectionModel().selectFirst();
		cmbFiltroEstado.getSelectionModel().selectFirst();
		txtFiltroEspectaculo.clear();
		txtFiltroNumero.clear();
		dpFiltroDesde.setValue(null);
		dpFiltroHasta.setValue(null);
		cargarIncidencias(null, null, null, null, null, null);
	}

	/** Abre el formulario de registro de nueva incidencia. */
	@FXML
	private void handleNuevaIncidencia() {
		stageManager.switchScene(FxmlView.REGISTRAR_INCIDENCIA);
	}

	/** Muestra el panel de resolución para la incidencia seleccionada. */
	private void handleAbrirResolucion(Incidencia incidencia) {
		incidenciaSeleccionada = incidencia;
		if (panelResolucion != null) {
			lblTituloResolucion.setText("RESOLVER INCIDENCIA #" + incidencia.getId());
			txtAccionesResolucion.clear();
			lblMensajeResolucion.setText("");
			panelResolucion.setVisible(true);
		}
	}

	/**
	 * Confirmar la resolución de la incidencia seleccionada.
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
			lblMensajeResolucion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
			lblMensajeResolucion.setText("¡Incidencia resuelta correctamente!");
			incidenciaSeleccionada = null;
			// Recargar tabla para reflejar el nuevo estado
			handleFiltrar();
			// Ocultar panel tras un breve delay visual (el mensaje queda visible)
			panelResolucion.setVisible(false);
			txtAccionesResolucion.clear();
		} catch (IllegalArgumentException ex) {
			lblMensajeResolucion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
			lblMensajeResolucion.setText(ex.getMessage());
		} catch (Exception ex) {
			lblMensajeResolucion.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
			lblMensajeResolucion.setText("Error al resolver: " + ex.getMessage());
		}
	}

	/** Cierra el panel de resolución sin hacer nada. */
	@FXML
	private void handleCancelarResolucion() {
		incidenciaSeleccionada = null;
		if (panelResolucion != null) {
			panelResolucion.setVisible(false);
			txtAccionesResolucion.clear();
			lblMensajeResolucion.setText("");
		}
	}

	/** Vuelve a la pantalla principal del perfil activo. */
	@FXML
	private void handleVolver() {
		navegarPantallaPrincipal();
	}

	private void inicializarVistaRegistro() {

		// Aqui metemos los tipo de incidencias en el combo
		cmbTipo.setItems(FXCollections.observableArrayList(TipoIncidencia.values()));

		// Contador de caracteres en descripción
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
	 * Registra la nueva incidencia con los datos introducidos.
	 */
	@FXML
	private void handleRegistrar() {
		// Validar
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

		Long idEsp = parseLongField(txtIdEspectaculo);
		Long idNum = parseLongField(txtIdNumero);

		// Si el campo tiene texto pero no es numérico avisamos
		if (txtIdEspectaculo != null && !txtIdEspectaculo.getText().trim().isEmpty() && idEsp == null) {
			mostrarMensaje("El ID de espectáculo debe ser un número válido.", true);
			return;
		}
		if (txtIdNumero != null && !txtIdNumero.getText().trim().isEmpty() && idNum == null) {
			mostrarMensaje("El ID de número debe ser un número válido.", true);
			return;
		}

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

	// Limpia el formulario de registro. //
	private void limpiarFormulario() {
		cmbTipo.getSelectionModel().clearSelection();
		txtDescripcion.clear();
		if (txtIdEspectaculo != null)
			txtIdEspectaculo.clear();
		if (txtIdNumero != null)
			txtIdNumero.clear();
	}

	/** Vuelve a la pantalla principal desde el formulario de registro. */
	@FXML
	private void handleVolverDesdeRegistro() {
		stageManager.switchScene(FxmlView.INCIDENCIAS);
	}

	/**
	 * Devuelve el id de las credenciales de la sesión actual para usarlo como
	 * idPersonaReporta / idPersonaResuelve. El admin tiene credenciales con id 0
	 * (credenciales ficticias por seguridad).
	 */
	private Long obtenerIdPersonaSesion() {
		if (!SessionManager.isLoggedIn())
			return 0L;
		Long id = SessionManager.getCurrentUser().getId();
		return id != null ? id : 0L;
	}

	/** Verifica si el usuario actual tiene permiso para resolver incidencias. */
	private boolean puedeResolverIncidencias() {
		Perfil perfil = SessionManager.getCurrentPerfil();
		return perfil == Perfil.admin || perfil == Perfil.coordinacion;
	}

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

	private Boolean parseEstadoFiltro() {
		if (cmbFiltroEstado == null)
			return null;
		String val = cmbFiltroEstado.getValue();
		if (val == null || val.equals("Todas"))
			return null;
		return val.equals("Resuelta");
	}

	private Long parseLongField(TextField field) {
		if (field == null)
			return null;
		String text = field.getText().trim();
		if (text.isEmpty())
			return null;
		try {
			return Long.parseLong(text);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Convierte un LocalDate de un DatePicker en java.util.Date.
	 * 
	 * @param endOfDay si true, ajusta la hora al final del día (23:59:59)
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

	private void mostrarMensaje(String texto, boolean esError) {
		if (lblMensaje == null)
			return;
		lblMensaje.setText(texto);
		lblMensaje.setStyle(esError ? "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626;"
				: "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
	}

	/** Navega a la pantalla principal del perfil activo. */
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