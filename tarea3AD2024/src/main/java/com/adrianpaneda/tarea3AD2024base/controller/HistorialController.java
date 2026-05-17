package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.db4o.LogOperacion;
import com.adrianpaneda.tarea3AD2024base.modelo.db4o.TipoOperacion;
import com.adrianpaneda.tarea3AD2024base.services.db4o.LogOperacionService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador para la pantalla de consulta del historial de operaciones (CU10).
 * <p>
 * Permite al administrador consultar las operaciones registradas en la base de
 * datos DB4O, aplicando filtros por usuario, tipo de operación y rango de
 * fechas. Los resultados se muestran en una tabla con las columnas: ID, Fecha y
 * Hora, Usuario, Tipo de operación y Resumen.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see LogOperacionService
 * @see LogOperacion
 */
@Controller
public class HistorialController implements Initializable {

	@Autowired
	@Lazy
	private StageManager stageManager;

	@Autowired
	private LogOperacionService logOperacionService;

	// ═══ FILTROS ═══
	@FXML
	private TextField txtUsuario;
	@FXML
	private CheckBox chkNuevo;
	@FXML
	private CheckBox chkActualizacion;
	@FXML
	private CheckBox chkBorrado;
	@FXML
	private DatePicker dateDesde;
	@FXML
	private DatePicker dateHasta;
	@FXML
	private Label lblError;

	// ═══ BOTONES ═══
	@FXML
	private Button btnBuscar;
	@FXML
	private Button btnLimpiar;
	@FXML
	private Button btnVolver;

	// ═══ TABLA ═══
	@FXML
	private TableView<LogOperacion> tablaHistorial;
	@FXML
	private TableColumn<LogOperacion, Long> colId;
	@FXML
	private TableColumn<LogOperacion, String> colFecha;
	@FXML
	private TableColumn<LogOperacion, String> colUsuario;
	@FXML
	private TableColumn<LogOperacion, String> colTipo;
	@FXML
	private TableColumn<LogOperacion, String> colResumen;
	@FXML
	private Label lblTotal;

	/** Formateador para mostrar la fecha y hora en la tabla. */
	private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	/**
	 * Inicializa la pantalla configurando las columnas de la tabla.
	 *
	 * @param location  la ubicación del FXML
	 * @param resources los recursos del bundle
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configurarColumnas();
		lblError.setText("");
		lblTotal.setText("");
	}

	/**
	 * Configura las columnas de la tabla con los valores correspondientes de
	 * LogOperacion.
	 * <p>
	 * Las columnas de fecha y tipo utilizan cell value factories personalizadas
	 * para formatear la fecha (dd/MM/yyyy HH:mm:ss) y mostrar el nombre del enum
	 * respectivamente.
	 * </p>
	 */
	private void configurarColumnas() {
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
		colResumen.setCellValueFactory(new PropertyValueFactory<>("resumen"));

		// Columna fecha formateada
		colFecha.setCellValueFactory(cellData -> {
			Date fecha = cellData.getValue().getFechaHora();
			String textoFecha = (fecha != null) ? formatoFecha.format(fecha) : "";
			return new javafx.beans.property.SimpleStringProperty(textoFecha);
		});

		// Columna tipo de operación
		colTipo.setCellValueFactory(cellData -> {
			TipoOperacion tipo = cellData.getValue().getTipoOperacion();
			String textoTipo = (tipo != null) ? tipo.name() : "";
			return new javafx.beans.property.SimpleStringProperty(textoTipo);
		});
	}

	/**
	 * Maneja el evento de click en el botón "BUSCAR".
	 * <p>
	 * Valida que el campo usuario no esté vacío (obligatorio según la
	 * especificación), recoge los filtros seleccionados (tipos de operación y rango
	 * de fechas) y consulta el historial mediante el servicio DB4O.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleBuscar(ActionEvent event) {
		lblError.setText("");

		// Validar usuario obligatorio
		String usuario = txtUsuario.getText().trim();
		if (usuario.isEmpty()) {
			lblError.setText("El campo usuario es obligatorio para realizar la búsqueda.");
			return;
		}

		// Recoger tipos seleccionados
		List<TipoOperacion> tipos = new ArrayList<>();
		if (chkNuevo.isSelected()) {
			tipos.add(TipoOperacion.NUEVO);
		}
		if (chkActualizacion.isSelected()) {
			tipos.add(TipoOperacion.ACTUALIZACION);
		}
		if (chkBorrado.isSelected()) {
			tipos.add(TipoOperacion.BORRADO);
		}

		// Convertir LocalDate a Date para las fechas
		Date fechaInicio = null;
		Date fechaFin = null;

		if (dateDesde.getValue() != null) {
			fechaInicio = convertirLocalDateADate(dateDesde.getValue(), true);
		}
		if (dateHasta.getValue() != null) {
			fechaFin = convertirLocalDateADate(dateHasta.getValue(), false);
		}

		// Consultar
		List<LogOperacion> resultados = logOperacionService.consultarConFiltros(usuario, tipos, fechaInicio, fechaFin);

		// Mostrar en tabla
		tablaHistorial.setItems(FXCollections.observableArrayList(resultados));
		lblTotal.setText("Total: " + resultados.size() + " resultado(s)");
	}

	/**
	 * Convierte un LocalDate (del DatePicker de JavaFX) a un Date (que usa DB4O).
	 * <p>
	 * Si es fecha de inicio, se establece a las 00:00:00 del día seleccionado. Si
	 * es fecha de fin, se establece a las 23:59:59 del día seleccionado para
	 * incluir todas las operaciones de ese día.
	 * </p>
	 *
	 * @param localDate la fecha del DatePicker
	 * @param esInicio  {@code true} si es fecha de inicio, {@code false} si es
	 *                  fecha de fin
	 * @return la fecha convertida a Date
	 */
	private Date convertirLocalDateADate(LocalDate localDate, boolean esInicio) {
		if (esInicio) {
			return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else {
			return Date.from(localDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
		}
	}

	/**
	 * Maneja el evento de click en el botón "LIMPIAR".
	 * <p>
	 * Restablece todos los filtros a su estado inicial y vacía la tabla de
	 * resultados.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleLimpiar(ActionEvent event) {
		txtUsuario.clear();
		chkNuevo.setSelected(false);
		chkActualizacion.setSelected(false);
		chkBorrado.setSelected(false);
		dateDesde.setValue(null);
		dateHasta.setValue(null);
		lblError.setText("");
		lblTotal.setText("");
		tablaHistorial.getItems().clear();
	}

	/**
	 * Maneja el evento de click en el botón "VOLVER".
	 * <p>
	 * Navega de vuelta a la pantalla de gestión de personas.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleVolver(ActionEvent event) {
		stageManager.switchScene(FxmlView.GESTION_PERSONAS);
	}
}