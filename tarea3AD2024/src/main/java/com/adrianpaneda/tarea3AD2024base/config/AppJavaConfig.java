package com.adrianpaneda.tarea3AD2024base.config;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Clase de configuración principal de Spring para la integración con JavaFX.
 * <p>
 * Define los beans necesarios para arrancar la interfaz gráfica: el bundle de
 * recursos internacionales y el gestor de escenas {@link StageManager}. El
 * {@code StageManager} se crea con {@code @Lazy} para que el Stage de JavaFX
 * ya esté disponible cuando Spring lo inyecte.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see StageManager
 * @see SpringFXMLLoader
 */
@Configuration
public class AppJavaConfig {
	
    @Autowired 
    SpringFXMLLoader springFXMLLoader;

//    /**
//     * Useful when dumping stack trace to a string for logging.
//     * @return ExceptionWriter contains logging utility methods
//     */
//    @Bean
//    @Scope("prototype")
//    public ExceptionWriter exceptionWriter() {
//        return new ExceptionWriter(new StringWriter());
//    }

    /**
     * Registra el bundle de recursos internacionales {@code Bundle.properties}
     * como bean de Spring para que pueda ser inyectado en otros componentes.
     *
     * @return el ResourceBundle cargado desde {@code Bundle.properties}
     */
    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("Bundle");
    }

    /**
     * Registra el {@link StageManager} como bean de Spring con inicialización
     * perezosa.
     * <p>
     * Se marca como {@code @Lazy} porque el {@code Stage} de JavaFX no existe
     * hasta que el método {@code start()} de la aplicación es invocado por el
     * hilo de JavaFX, posterior al arranque del contexto de Spring.
     * </p>
     *
     * @param stage el Stage primario de JavaFX
     * @return el gestor de escenas configurado
     * @throws IOException si no se puede cargar algún archivo FXML
     */
    @Bean
    @Lazy(value = true)
    public StageManager stageManager(Stage stage) throws IOException {
        return new StageManager(springFXMLLoader, stage);
    }

}
