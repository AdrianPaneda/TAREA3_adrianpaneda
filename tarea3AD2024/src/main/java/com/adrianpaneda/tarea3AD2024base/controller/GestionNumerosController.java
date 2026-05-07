package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;
import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.services.ArtistaService;
import com.adrianpaneda.tarea3AD2024base.services.EspectaculoService;
import com.adrianpaneda.tarea3AD2024base.services.NumeroService;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * Controlador para la pantalla de gestión de números de un espectáculo (CU5B y
 * CU5C).
 * <p>
 * Permite crear y modificar los números artísticos de un espectáculo,
 * incluyendo la asignación de artistas a cada número.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class GestionNumerosController implements Initializable {

	@FXML
	private Label lblNombreEspectaculo;

	@FXML
	private TableView<Numero> tablaNumeros;

	@FXML
	private TableColumn<Numero, Long> colId;

	@FXML
	private TableColumn<Numero, String> colNombre;

	@FXML
	private TableColumn<Numero, Double> colDuracion;

	@FXML
	private TableColumn<Numero, Integer> colOrden;

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
	private Spinner<Double> spinnerDuracion;

	@FXML
	private Label lblErrorDuracion;

	@FXML
	private TextField txtOrden;

	@FXML
	private Label lblErrorOrden;

	@FXML
	private TableView<Artista> tablaArtistas;

	@FXML
	private TableColumn<Artista, Boolean> colSeleccionar;

	@FXML
	private TableColumn<Artista, String> colArtistaNombre;

	@FXML
	private TableColumn<Artista, String> colArtistaEspecialidades;

	@FXML
	private Label lblErrorArtistas;

	@FXML
	private Button btnAnadir;

	@FXML
	private Button btnVolver;

	@FXML
	private Button btnGuardar;

	@FXML
	private Button btnCancelar;

	@Autowired
	private EspectaculoService espectaculoService;

	@Autowired
	private NumeroService numeroService;

	@Autowired
	private ArtistaService artistaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	private Espectaculo espectaculoActual;
	private ObservableList<Numero> listaNumeros = FXCollections.observableArrayList();
	private ObservableList<Artista> listaArtistas = FXCollections.observableArrayList();
	private Map<Long, SimpleBooleanProperty> seleccionArtistas = new HashMap<>();

	private enum Modo {
		CREAR, EDITAR
	}

	private Modo modoActual;
	private Numero numeroEnEdicion;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Long id = SessionManager.getSelectedEspectaculo();
		espectaculoActual = espectaculoService.obtenerConDetalle(id);

		if (espectaculoActual == null) {
			stageManager.switchScene(FxmlView.GESTIONAR_ESPECTACULOS);
			return;
		}

		lblNombreEspectaculo.setText("Espectáculo: " + espectaculoActual.getNombre());

		configurarTablaNumeros();
		configurarTablaArtistas();
		configurarSpinnerDuracion();
		cargarNumeros();
		ocultarFormulario();
	}

	private void configurarTablaNumeros() {
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
		colOrden.setCellValueFactory(new PropertyValueFactory<>("orden"));
		añadirColumnaAccionesNumeros();
	}

	private void añadirColumnaAccionesNumeros() {
		TableColumn<Numero, Void> colAcciones = new TableColumn<>("Acciones");
		colAcciones.setPrefWidth(120.0);
		colAcciones.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-alignment: CENTER;");

		colAcciones.setCellFactory(param -> new TableCell<>() {
			private final Button btnEditar = new Button("Editar");

			{
				btnEditar.setStyle(
						"-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 5 10 5 10; -fx-font-weight: bold; -fx-cursor: hand;");
				btnEditar.setOnAction(event -> {
					Numero numero = getTableView().getItems().get(getIndex());
					handleEditarNumero(numero);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : btnEditar);
			}
		});

		tablaNumeros.getColumns().add(colAcciones);
	}

	private void cargarNumeros() {
		listaNumeros.clear();
		listaNumeros.addAll(numeroService.obtenerPorEspectaculo(espectaculoActual.getId()));
		tablaNumeros.setItems(listaNumeros);
	}

	private void configurarTablaArtistas() {
		colSeleccionar.setCellFactory(param -> new TableCell<>() {
			private final CheckBox checkBox = new CheckBox();

			@Override
			protected void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					Artista artista = getTableView().getItems().get(getIndex());
					SimpleBooleanProperty prop = seleccionArtistas.get(artista.getId());
					if (prop != null) {
						checkBox.setSelected(prop.get());
						checkBox.setOnAction(event -> prop.set(checkBox.isSelected()));
					}
					setGraphic(checkBox);
				}
			}
		});

		colArtistaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

		colArtistaEspecialidades.setCellValueFactory(cellData -> {
			Set<Especialidad> esp = cellData.getValue().getEspecialidades();
			if (esp == null || esp.isEmpty()) {
				return new SimpleStringProperty("Ninguna");
			}
			String texto = esp.stream()
					.map(e -> e.name().substring(0, 1).toUpperCase() + e.name().substring(1).toLowerCase())
					.collect(Collectors.joining(", "));
			return new SimpleStringProperty(texto);
		});
	}

	private void cargarArtistasDisponibles(Set<Artista> artistasSeleccionados) {
		listaArtistas.clear();
		seleccionArtistas.clear();

		List<Artista> todosArtistas = artistaService.obtenerTodos();
		listaArtistas.addAll(todosArtistas);

		for (Artista artista : todosArtistas) {
			boolean seleccionado = artistasSeleccionados.stream().anyMatch(a -> a.getId().equals(artista.getId()));
			seleccionArtistas.put(artista.getId(), new SimpleBooleanProperty(seleccionado));
		}

		tablaArtistas.setItems(listaArtistas);
	}

	private void configurarSpinnerDuracion() {
		SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 60.0, 5.0,
				0.5);
		spinnerDuracion.setValueFactory(valueFactory);
		spinnerDuracion.setEditable(false);
	}

	@FXML
	private void handleAnadir(ActionEvent event) {
		modoActual = Modo.CREAR;
		numeroEnEdicion = null;
		limpiarFormulario();
		mostrarFormulario();
		lblTituloFormulario.setText("AÑADIR NÚMERO");

		int siguienteOrden = listaNumeros.size() + 1;
		txtOrden.setText(String.valueOf(siguienteOrden));

		cargarArtistasDisponibles(new HashSet<>());
	}

	private void handleEditarNumero(Numero numero) {
		Numero numeroCompleto = numeroService.obtenerConArtistas(numero.getId());

		modoActual = Modo.EDITAR;
		numeroEnEdicion = numeroCompleto;
		limpiarFormulario();
		mostrarFormulario();
		lblTituloFormulario.setText("EDITAR NÚMERO");

		txtNombre.setText(numeroCompleto.getNombre());
		spinnerDuracion.getValueFactory().setValue(numeroCompleto.getDuracion());
		txtOrden.setText(String.valueOf(numeroCompleto.getOrden()));

		Set<Artista> artistasActuales = numeroCompleto.getArtistas() != null ? numeroCompleto.getArtistas()
				: new HashSet<>();
		cargarArtistasDisponibles(artistasActuales);
	}

	@FXML
	private void handleGuardar(ActionEvent event) {
		limpiarErrores();
		if (!validarFormulario()) {
			return;
		}
		if (modoActual == Modo.CREAR) {
			crearNumero();
		} else if (modoActual == Modo.EDITAR) {
			actualizarNumero();
		}
	}

	private void crearNumero() {
		try {
			Numero numero = new Numero();
			numero.setNombre(txtNombre.getText().trim());
			numero.setDuracion(spinnerDuracion.getValue());
			numero.setOrden(Integer.parseInt(txtOrden.getText().trim()));
			numero.setEspectaculo(espectaculoActual);
			numero.setArtistas(obtenerArtistasSeleccionados());
			numeroService.guardar(numero);
			cargarNumeros();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			lblErrorDuracion.setText(e.getMessage());
		}
	}

	private void actualizarNumero() {
		try {
			numeroEnEdicion.setNombre(txtNombre.getText().trim());
			numeroEnEdicion.setDuracion(spinnerDuracion.getValue());
			numeroEnEdicion.setOrden(Integer.parseInt(txtOrden.getText().trim()));
			numeroEnEdicion.setArtistas(obtenerArtistasSeleccionados());
			numeroService.guardar(numeroEnEdicion);
			cargarNumeros();
			ocultarFormulario();
		} catch (IllegalArgumentException e) {
			lblErrorDuracion.setText(e.getMessage());
		}
	}

	private boolean validarFormulario() {
		boolean valido = true;
		if (txtNombre.getText().trim().isEmpty()) {
			lblErrorNombre.setText("El nombre es obligatorio");
			valido = false;
		}
		String ordenTexto = txtOrden.getText().trim();
		if (ordenTexto.isEmpty()) {
			lblErrorOrden.setText("El orden es obligatorio");
			valido = false;
		} else {
			try {
				int orden = Integer.parseInt(ordenTexto);
				if (orden <= 0) {
					lblErrorOrden.setText("El orden debe ser un número positivo");
					valido = false;
				}
			} catch (NumberFormatException e) {
				lblErrorOrden.setText("El orden debe ser un número entero");
				valido = false;
			}
		}
		Set<Artista> seleccionados = obtenerArtistasSeleccionados();
		if (seleccionados.isEmpty()) {
			lblErrorArtistas.setText("Debe seleccionar al menos un artista");
			valido = false;
		}
		return valido;
	}

	private Set<Artista> obtenerArtistasSeleccionados() {
		Set<Artista> seleccionados = new HashSet<>();
		for (Artista artista : listaArtistas) {
			SimpleBooleanProperty prop = seleccionArtistas.get(artista.getId());
			if (prop != null && prop.get()) {
				seleccionados.add(artista);
			}
		}
		return seleccionados;
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
		numeroEnEdicion = null;
		bloquearBotones(false);
	}

	/**
	 * Bloquea o desbloquea los botones principales durante la edición.
	 *
	 * @param bloquear true para bloquear, false para desbloquear
	 */
	private void bloquearBotones(boolean bloquear) {
		btnAnadir.setDisable(bloquear);
		tablaNumeros.setDisable(bloquear);
	}

	private void limpiarFormulario() {
		txtNombre.clear();
		spinnerDuracion.getValueFactory().setValue(5.0);
		txtOrden.clear();
		seleccionArtistas.clear();
		limpiarErrores();
	}

	private void limpiarErrores() {
		lblErrorNombre.setText("");
		lblErrorDuracion.setText("");
		lblErrorOrden.setText("");
		lblErrorArtistas.setText("");
	}

	@FXML
	private void handleCancelar(ActionEvent event) {
		ocultarFormulario();
	}

	/**
	 * Vuelve a la pantalla de espectáculos con aviso si hay operación en curso.
	 */
	@FXML
	private void handleVolver(ActionEvent event) {
		if (modoActual != null) {
			Alert confirmacion = new Alert(AlertType.CONFIRMATION);
			confirmacion.setTitle("Operación en curso");
			confirmacion.setHeaderText("Tiene una operación sin guardar");
			confirmacion.setContentText("Si retrocede perderá los cambios. ¿Desea continuar?");
			Optional<ButtonType> resultado = confirmacion.showAndWait();
			if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
				return;
			}
		}
		stageManager.switchScene(FxmlView.GESTIONAR_ESPECTACULOS);
	}
}
