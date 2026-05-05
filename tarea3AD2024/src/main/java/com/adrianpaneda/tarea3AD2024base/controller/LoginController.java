package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.repositorios.CredencialesRepository;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controlador para la pantalla de autenticación (login).
 * <p>
 * Gestiona el proceso de login de usuarios verificando sus credenciales y
 * redirigiendo a la pantalla correspondiente según su perfil. El administrador
 * tiene credenciales hardcodeadas en application.properties.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Controller
public class LoginController implements Initializable {

	@FXML
	private TextField txtUsuario;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Button btnLogin;

	@FXML
	private Label lblError;

	@FXML
	private Button btnInvitado;

	@Autowired
	private CredencialesRepository credencialesRepository;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Value("${admin.usuario}")
	private String adminUsuario;

	@Value("${admin.password}")
	private String adminPassword;

	/**
	 * Maneja el evento de click en el botón Login.
	 * <p>
	 * Primero verifica si es el administrador (credenciales hardcodeadas). Si no,
	 * busca las credenciales en la BD, verifica la contraseña y redirige según el
	 * perfil del usuario.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleLogin(ActionEvent event) {
		String nombreUsuario = getUsuario();
		String password = getPassword();

		// Validar campos vacíos
		if (nombreUsuario.isEmpty() || password.isEmpty()) {
			lblError.setText("Por favor, ingrese usuario y contraseña");
			return;
		}

		// Verificar si es el admin
		if (nombreUsuario.equals(adminUsuario) && password.equals(adminPassword)) {
			// Crear Credenciales falsas para el admin y poder mantener la sesion en
			// SessionManager
			Credenciales adminCredenciales = new Credenciales();
			adminCredenciales.setNombreUsuario(adminUsuario);
			adminCredenciales.setPerfil(Perfil.admin);

			SessionManager.setCurrentUser(adminCredenciales);
			stageManager.switchScene(FxmlView.PERSONAS);
			return;
		}

		// Si no es admin, buscar en la BD
		Optional<Credenciales> credencialesOpt = credencialesRepository.findByNombreUsuario(nombreUsuario);

		if (credencialesOpt.isEmpty()) {
			lblError.setText("Usuario o contraseña incorrectos");
			return;
		}

		Credenciales credenciales = credencialesOpt.get();

		// Verificar contraseña
		if (!password.equals(credenciales.getPassword())) {
			lblError.setText("Usuario o contraseña incorrectos");
			return;
		}

		// Limpiar password antes de guardar en sesión
		credenciales.setPassword(null);

		// Login exitoso guardar sesión
		SessionManager.setCurrentUser(credenciales);

		// Redirigir según perfil
		Perfil perfil = credenciales.getPerfil();

		if (perfil == Perfil.coordinacion) {
			stageManager.switchScene(FxmlView.GESTIONAR_ESPECTACULOS);
		} else if (perfil == Perfil.artista) {
			stageManager.switchScene(FxmlView.FICHA_ARTISTA);
		}
	}

	/**
	 * Maneja el evento de click en el botón "Ver espectáculos como invitado".
	 * <p>
	 * Permite acceder a la pantalla de espectáculos sin necesidad de autenticación.
	 * </p>
	 *
	 * @param event el evento de acción del botón
	 */
	@FXML
	private void handleInvitado(ActionEvent event) {
		stageManager.switchScene(FxmlView.ESPECTACULOS);
	}

	/**
	 * Obtiene el nombre de usuario ingresado.
	 *
	 * @return el nombre de usuario en minúsculas sin espacios
	 */
	private String getUsuario() {
		return txtUsuario.getText().trim().toLowerCase();
	}

	/**
	 * Obtiene la contraseña ingresada.
	 *
	 * @return la contraseña
	 */
	private String getPassword() {
		return txtPassword.getText();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Inicialización si es necesaria en el futuro
	}
}