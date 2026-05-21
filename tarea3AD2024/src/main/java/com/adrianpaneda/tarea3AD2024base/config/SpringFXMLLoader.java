package com.adrianpaneda.tarea3AD2024base.config;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Cargador de archivos FXML integrado con el contexto de Spring.
 * <p>
 * Delega en {@link FXMLLoader} la carga de la jerarquía de nodos FXML y
 * configura Spring como factoría de controladores ({@code controllerFactory}),
 * de modo que los controladores de JavaFX son gestionados como beans de Spring
 * y reciben inyección de dependencias.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see StageManager
 * @see AppJavaConfig
 */
@Component
public class SpringFXMLLoader {

    private final ResourceBundle resourceBundle;
    private final ApplicationContext context;

    /**
     * Constructor inyectado por Spring con el contexto de aplicación y el
     * bundle de recursos.
     *
     * @param context        el contexto de aplicación de Spring
     * @param resourceBundle el bundle de recursos internacionales
     */
    @Autowired
    public SpringFXMLLoader(ApplicationContext context, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.context = context;
    }

    /**
     * Carga la jerarquía de nodos de un archivo FXML y devuelve el nodo raíz.
     * <p>
     * Spring actúa como factoría de controladores, lo que permite que los
     * controladores FXML sean beans gestionados con inyección de dependencias.
     * </p>
     *
     * @param fxmlPath la ruta del archivo FXML relativa al classpath
     * @return el nodo raíz de la jerarquía FXML cargada
     * @throws IOException si el archivo FXML no se encuentra o no puede
     *                     cargarse
     */
    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        loader.setResources(resourceBundle);
        loader.setLocation(getClass().getResource(fxmlPath));
        return loader.load();
    }
}
