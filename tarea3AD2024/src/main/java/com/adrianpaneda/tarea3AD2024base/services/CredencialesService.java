package com.adrianpaneda.tarea3AD2024base.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Credenciales;
import com.adrianpaneda.tarea3AD2024base.repositorios.CredencialesRepository;

/**
 * Servicio para la gestión de credenciales de acceso al sistema.
 * <p>
 * Proporciona la lógica de negocio para autenticación de usuarios (CU2)
 * y validación de unicidad de nombres de usuario (CU3).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Credenciales
 * @see CredencialesRepository
 */
@Service
public class CredencialesService {

    @Autowired
    private CredencialesRepository credencialesRepository;

    /**
     * Autentica un usuario verificando sus credenciales.
     * <p>
     * Necesario para CU2 (Login). Busca el usuario y compara la contraseña.
     * </p>
     *
     * @param nombreUsuario el nombre de usuario (en minúsculas)
     * @param password la contraseña a verificar
     * @return {@code true} si las credenciales son válidas, {@code false} en caso contrario
     */
    public boolean autenticar(String nombreUsuario, String password) {
        Optional<Credenciales> credenciales = credencialesRepository.findByNombreUsuario(nombreUsuario);
        
        if (credenciales.isEmpty()) {
            return false;
        }
        
        return password.equals(credenciales.get().getPassword());
    }

    /**
     * Verifica si un nombre de usuario ya existe en el sistema.
     * <p>
     * Necesario para validar unicidad en CU3 (Registrar persona).
     * </p>
     *
     * @param nombreUsuario el nombre de usuario a verificar
     * @return {@code true} si el nombre de usuario ya existe
     */
    public boolean existeNombreUsuario(String nombreUsuario) {
        return credencialesRepository.existsByNombreUsuario(nombreUsuario);
    }

    /**
     * Guarda o actualiza credenciales en el sistema.
     *
     * @param credenciales las credenciales a guardar
     * @return las credenciales guardadas con su ID generado
     */
    public Credenciales guardar(Credenciales credenciales) {
        return credencialesRepository.save(credenciales);
    }

    /**
     * Busca credenciales por su identificador.
     *
     * @param id el identificador de las credenciales
     * @return las credenciales encontradas
     */
    public Credenciales buscarPorId(Long id) {
        return credencialesRepository.findById(id).orElse(null);
    }
}