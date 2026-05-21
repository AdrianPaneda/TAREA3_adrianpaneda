package com.adrianpaneda.tarea3AD2024base.config;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Objects;

import org.slf4j.Logger;

import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Gestor de escenas de la ventana principal de la aplicación JavaFX.
 * <p>
 * Proporciona el método {@link #switchScene(FxmlView)} para cambiar la escena
 * activa del Stage primario. Cada cambio carga el archivo FXML correspondiente
 * mediante {@link SpringFXMLLoader}, actualiza el título de la ventana y ajusta
 * el tamaño de la ventana al contenido.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see SpringFXMLLoader
 * @see FxmlView
 */
public class StageManager {

    private static final Logger LOG = getLogger(StageManager.class);
    private final Stage primaryStage;
    private final SpringFXMLLoader springFXMLLoader;

    /**
     * Crea un nuevo gestor de escenas para el Stage primario indicado.
     *
     * @param springFXMLLoader el cargador de archivos FXML integrado con Spring
     * @param stage            el Stage primario de la aplicación
     */
    public StageManager(SpringFXMLLoader springFXMLLoader, Stage stage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = stage;
    }

    /**
     * Cambia la escena activa del Stage primario por la vista indicada.
     * <p>
     * Carga el archivo FXML de la vista, actualiza el título de la ventana y
     * ajusta el tamaño de la ventana al nuevo contenido.
     * </p>
     *
     * @param view la vista destino a mostrar
     */
    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        show(viewRootNodeHierarchy, view.getTitle());
    }
    
    /**
     * Muestra el nodo raíz en el Stage primario con el título indicado.
     *
     * @param rootnode el nodo raíz de la jerarquía de la escena
     * @param title    el título a mostrar en la barra de la ventana
     */
    private void show(final Parent rootnode, String title) {
        Scene scene = prepareScene(rootnode);
        //scene.getStylesheets().add("/styles/Styles.css");
        
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        
        try {
            primaryStage.show();
        } catch (Exception exception) {
            logAndExit ("Unable to show scene for title" + title,  exception);
        }
    }
    
    /**
     * Prepara la escena reutilizando la existente si ya hay una asignada al
     * Stage, para evitar recrearla innecesariamente.
     *
     * @param rootnode el nuevo nodo raíz a asignar a la escena
     * @return la escena preparada con el nuevo nodo raíz
     */
    private Scene prepareScene(Parent rootnode){
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootnode);
        }
        scene.setRoot(rootnode);
        return scene;
    }

    /**
     * Carga la jerarquía de objetos de un archivo FXML y devuelve el nodo raíz.
     * <p>
     * Si ocurre cualquier error durante la carga, registra el error y cierra la
     * aplicación para evitar un estado inconsistente.
     * </p>
     *
     * @param fxmlFilePath la ruta del archivo FXML a cargar
     * @return el nodo raíz de la jerarquía FXML cargada
     */
    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            rootNode = springFXMLLoader.load(fxmlFilePath);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception exception) {
            logAndExit("Unable to load FXML view" + fxmlFilePath, exception);
        }
        return rootNode;
    }
    
    /**
     * Registra un error en el log y cierra la aplicación JavaFX.
     *
     * @param errorMsg  el mensaje descriptivo del error
     * @param exception la excepción que causó el error
     */
    private void logAndExit(String errorMsg, Exception exception) {
        LOG.error(errorMsg, exception);
        Platform.exit();
    }

}
