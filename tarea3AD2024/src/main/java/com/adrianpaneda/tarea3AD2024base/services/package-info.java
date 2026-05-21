/**
 * Servicios de negocio para las entidades JPA persistidas en MySQL.
 * <p>
 * Cada servicio encapsula la lógica de negocio, las validaciones y la
 * coordinación entre el repositorio correspondiente y el log de operaciones
 * DB4O. Todos los métodos de escritura están anotados con
 * {@link org.springframework.transaction.annotation.Transactional}.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see com.adrianpaneda.tarea3AD2024base.services.db4o
 * @see com.adrianpaneda.tarea3AD2024base.services.objectdb
 */
package com.adrianpaneda.tarea3AD2024base.services;
