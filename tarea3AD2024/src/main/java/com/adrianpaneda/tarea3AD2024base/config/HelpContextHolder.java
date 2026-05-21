package com.adrianpaneda.tarea3AD2024base.config;

import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

/**
 * Contenedor estático que almacena la vista desde la que se abre el sistema de
 * ayuda contextual.
 * <p>
 * Actúa como un puente de comunicación simple entre el controlador que lanza la
 * ventana de ayuda y el {@link com.adrianpaneda.tarea3AD2024base.controller.HelpController}
 * que la renderiza. Cada controlador establece la vista actual antes de llamar a
 * {@link HelpStageManager#showHelp(FxmlView)}, de modo que la ventana de ayuda
 * puede pre-seleccionar la sección relevante para la pantalla en uso.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see HelpStageManager
 */
public final class HelpContextHolder {

    /** Vista actualmente activa desde la que se abrió la ayuda. */
    private static FxmlView currentView;

    /** Previene la instanciación de esta clase utilitaria. */
    private HelpContextHolder() {
    }

    /**
     * Establece la vista desde la que se abre la ayuda.
     *
     * @param view la vista activa en el momento de abrir la ayuda
     */
    public static void setCurrentView(FxmlView view) {
        currentView = view;
    }

    /**
     * Obtiene la vista que activó la apertura de la ayuda.
     *
     * @return la vista guardada, o {@code null} si no se ha establecido ninguna
     */
    public static FxmlView getCurrentView() {
        return currentView;
    }
}
