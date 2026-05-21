package com.adrianpaneda.tarea3AD2024base;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.adrianpaneda.tarea3AD2024base.config.StageManager;
import com.adrianpaneda.tarea3AD2024base.config.db4o.DB4OConnection;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación del sistema de gestión del circo.
 * <p>
 * Integra el ciclo de vida de JavaFX con el contexto de Spring Boot. El
 * contexto de Spring se arranca en {@link #init()} y se cierra en
 * {@link #stop()}, garantizando que los beans estén disponibles durante toda
 * la ejecución. Al cerrar, también se cierra la conexión DB4O para liberar el
 * fichero de log.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see StageManager
 * @see FxmlView
 */
@SpringBootApplication
@EntityScan(basePackages = "com.adrianpaneda.tarea3AD2024base.modelo")
public class Tarea3Ad2024baseApplication extends Application {

	protected ConfigurableApplicationContext springContext;
	protected StageManager stageManager;

	/**
	 * Arranca el contexto de Spring Boot antes de que JavaFX muestre la ventana.
	 * <p>
	 * Este método es llamado por el hilo de JavaFX antes de {@link #start(Stage)},
	 * por lo que es el lugar adecuado para inicializar Spring sin bloquear el hilo
	 * de la interfaz gráfica.
	 * </p>
	 *
	 * @throws Exception si el arranque del contexto de Spring falla
	 */
	@Override
	public void init() throws Exception {
		springContext = springBootApplicationContext();
	}

	/**
	 * Punto de entrada de la aplicación.
	 *
	 * @param args los argumentos de línea de comandos
	 */
	public static void main(final String[] args) {
		Application.launch(args);
	}

	/**
	 * Configura el Stage primario y muestra la primera escena de la aplicación.
	 *
	 * @param primaryStage el Stage primario creado por JavaFX
	 * @throws Exception si no se puede mostrar la escena inicial
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		stageManager = springContext.getBean(StageManager.class, primaryStage);
		displayInitialScene();
	}

	/**
	 * Libera los recursos al cerrar la aplicación.
	 * <p>
	 * Cierra la conexión DB4O para liberar el fichero {@code log.db4o} y luego
	 * cierra el contexto de Spring para destruir todos los beans correctamente.
	 * </p>
	 *
	 * @throws Exception si ocurre algún error durante el cierre
	 */
	@Override
	public void stop() throws Exception {
		DB4OConnection.getInstancia().cerrar();
		springContext.close();
	}

	/**
	 * Muestra la primera escena al arrancar la aplicación.
	 * <p>
	 * Por defecto muestra la pantalla de login. Se puede sobreescribir en
	 * subclases para cambiar la vista inicial (por ejemplo, en tests funcionales).
	 * </p>
	 */
	protected void displayInitialScene() {
		stageManager.switchScene(FxmlView.LOGIN);
	}

	/**
	 * Construye y arranca el contexto de Spring Boot con los parámetros de
	 * arranque de la aplicación JavaFX.
	 *
	 * @return el contexto de Spring configurado y arrancado
	 */
	private ConfigurableApplicationContext springBootApplicationContext() {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Tarea3Ad2024baseApplication.class);
		String[] args = getParameters().getRaw().stream().toArray(String[]::new);
		return builder.run(args);
	}

}
