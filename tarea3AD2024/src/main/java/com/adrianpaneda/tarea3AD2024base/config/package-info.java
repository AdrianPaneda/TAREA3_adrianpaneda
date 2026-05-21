/**
 * Configuración de la aplicación: integración Spring-JavaFX y gestión de sesión.
 * <p>
 * Incluye {@link com.adrianpaneda.tarea3AD2024base.config.AppJavaConfig} (beans
 * de infraestructura), {@link com.adrianpaneda.tarea3AD2024base.config.StageManager}
 * (cambio de escenas FXML), {@link com.adrianpaneda.tarea3AD2024base.config.SpringFXMLLoader}
 * (carga de FXML con inyección de dependencias) y
 * {@link com.adrianpaneda.tarea3AD2024base.config.SessionManager} (estado de sesión
 * del usuario actual).
 * </p>
 * <p>
 * La conexión a la base de datos ObjectDB se configura mediante
 * {@link com.adrianpaneda.tarea3AD2024base.config.ObjectDBConnection}.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
package com.adrianpaneda.tarea3AD2024base.config;
