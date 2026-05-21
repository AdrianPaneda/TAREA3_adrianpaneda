package com.adrianpaneda.tarea3AD2024base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Servicio que gestiona la apertura de la ventana secundaria del sistema de ayuda.
 * <p>
 * Mantiene una referencia al {@link Stage} de ayuda actualmente visible para
 * evitar ventanas duplicadas. Cuando se llama a {@link #showHelp(FxmlView)},
 * establece el contexto en {@link HelpContextHolder}, cierra cualquier ventana
 * previa y abre una nueva cargando {@code Ayuda.fxml} mediante
 * {@link SpringFXMLLoader}.
 * </p>
 * <p>
 * La ventana de ayuda es <em>no modal</em>: el usuario puede seguir usando la
 * aplicación mientras la tiene abierta.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see HelpContextHolder
 * @see com.adrianpaneda.tarea3AD2024base.controller.HelpController
 */
@Service
public class HelpStageManager {

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    /** Stage de ayuda activo, o {@code null} si no hay ninguno abierto. */
    private Stage helpStage;

    /**
     * Abre (o reabre) la ventana de ayuda contextual para la vista indicada.
     * <p>
     * El contexto se guarda en {@link HelpContextHolder} antes de cargar el FXML,
     * de modo que el controlador {@code HelpController} puede seleccionar la
     * sección adecuada al inicializarse.
     * </p>
     *
     * @param callerView la vista desde la que se abre la ayuda; determina la
     *                   sección activa mostrada por defecto
     */
    public void showHelp(FxmlView callerView) {
        HelpContextHolder.setCurrentView(callerView);

        // Cerrar la ventana previa si está abierta para recargar el contenido
        if (helpStage != null && helpStage.isShowing()) {
            helpStage.close();
        }

        try {
            Parent root = springFXMLLoader.load(FxmlView.AYUDA.getFxmlFile());
            helpStage = new Stage();
            helpStage.setTitle("Sistema de Ayuda - Circo");
            helpStage.initModality(Modality.NONE);
            helpStage.setScene(new Scene(root, 1150, 760));
            helpStage.setMinWidth(900);
            helpStage.setMinHeight(600);
            helpStage.setResizable(true);
            helpStage.setOnCloseRequest(e -> helpStage = null);
            helpStage.show();
        } catch (Exception e) {
            System.err.println("Error al abrir la ventana de ayuda: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
