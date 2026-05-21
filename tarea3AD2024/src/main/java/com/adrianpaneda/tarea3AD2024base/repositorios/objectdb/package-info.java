/**
 * Repositorio manual para el acceso a la base de datos ObjectDB de incidencias.
 * <p>
 * A diferencia de los repositorios JPA de MySQL, este paquete implementa el
 * acceso a datos directamente mediante {@link javax.persistence.EntityManager},
 * ya que Spring Data JPA no es compatible con ObjectDB en esta configuración.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see com.adrianpaneda.tarea3AD2024base.repositorios
 */
package com.adrianpaneda.tarea3AD2024base.repositorios.objectdb;
