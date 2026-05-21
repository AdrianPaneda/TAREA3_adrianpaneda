package com.adrianpaneda.tarea3AD2024base.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.adrianpaneda.tarea3AD2024base.config.HelpContextHolder;
import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.services.HelpContentGenerator;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

/**
 * Controlador de la ventana de ayuda contextual del sistema.
 * <p>
 * Se encarga de inicializar el {@link WebView} con el contenido HTML generado
 * por {@link HelpContentGenerator} en función del perfil del usuario activo y
 * la vista desde la que se abrió la ayuda (almacenada en
 * {@link HelpContextHolder}).
 * </p>
 * <p>
 * Esta ventana se abre en un {@link javafx.stage.Stage} secundario no modal
 * gestionado por {@link com.adrianpaneda.tarea3AD2024base.config.HelpStageManager}.
 * Cada pantalla de la aplicación tiene un botón <strong>❓ AYUDA</strong> en su
 * barra superior que abre esta ventana con la sección relevante ya activa.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see HelpContentGenerator
 * @see HelpContextHolder
 * @see com.adrianpaneda.tarea3AD2024base.config.HelpStageManager
 */
@Controller
public class HelpController implements Initializable {

    /** WebView que renderiza el contenido HTML de la ayuda. */
    @FXML
    private WebView webView;

    @Autowired
    private HelpContentGenerator helpContentGenerator;

    /**
     * Inicializa el controlador generando y cargando el HTML de ayuda en el
     * {@link WebView}.
     * <p>
     * Obtiene el perfil del usuario de {@link SessionManager} y la vista activa
     * de {@link HelpContextHolder}, delega la generación del HTML en
     * {@link HelpContentGenerator} y carga el resultado en el motor del WebView.
     * </p>
     *
     * @param location  la URL del FXML cargado
     * @param resources el bundle de recursos (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Perfil perfil = SessionManager.getCurrentPerfil();
        FxmlView currentView = HelpContextHolder.getCurrentView();

        String html = helpContentGenerator.generateHtml(perfil, currentView);
        webView.getEngine().loadContent(html, "text/html");
    }
}
