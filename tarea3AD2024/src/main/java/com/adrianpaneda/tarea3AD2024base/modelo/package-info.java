/**
 * Entidades JPA del dominio del circo persistidas en MySQL.
 * <p>
 * La jerarquía de personas utiliza la estrategia {@code JOINED}:
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Persona} es la entidad raíz y
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Artista} y
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion} son sus subclases.
 * </p>
 * <p>
 * Las demás entidades del dominio son
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo},
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Numero} y
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Credenciales}. Los enumerados
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Perfil} y
 * {@link com.adrianpaneda.tarea3AD2024base.modelo.Especialidad} definen los tipos
 * utilizados en el dominio.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
package com.adrianpaneda.tarea3AD2024base.modelo;
