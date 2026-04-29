package com.adrianpaneda.tarea3AD2024base.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Persona;
import com.adrianpaneda.tarea3AD2024base.repositorios.PersonaRepository;

/**
 * Servicio para la gestión de personas del circo.
 * <p>
 * Proporciona la lógica de negocio para el registro y modificación de personas
 * (CU3), incluyendo todas las validaciones requeridas por la rúbrica.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Persona
 * @see PersonaRepository
 */
@Service
public class PersonaService {

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private CredencialesService credencialesService;

	/**
	 * Valida que el email no esté duplicado en el sistema.
	 * <p>
	 * Validación requerida por PT-19 de la rúbrica.
	 * </p>
	 *
	 * @param email el email a validar
	 * @return {@code true} si el email es válido (no existe)
	 * @throws IllegalArgumentException si el email ya está registrado
	 */
	public boolean validarEmailUnico(String email) {
		if (personaRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("El email ya está registrado en el sistema");
		}
		return true;
	}

	/**
	 * Valida que las credenciales cumplan todas las reglas de negocio.
	 *
	 * @param nombreUsuario el nombre de usuario a validar
	 * @param password      la contraseña a validar
	 * @return el nombre de usuario validado y convertido a minúsculas
	 * @throws IllegalArgumentException si alguna validación falla
	 */
	public String validarCredenciales(String nombreUsuario, String password) {

		if (nombreUsuario.contains(" ") || password.contains(" ")) {
			throw new IllegalArgumentException("El usuario y la contraseña no pueden contener espacios");
		}

		if (nombreUsuario.length() <= 2 || password.length() <= 2) {
			throw new IllegalArgumentException("El usuario y la contraseña deben tener más de 2 caracteres");
		}

		if (!nombreUsuario.matches("[a-zA-Z]+")) {
			throw new IllegalArgumentException("El usuario solo puede contener letras sin tildes ni diéresis");
		}

		if (credencialesService.existeNombreUsuario(nombreUsuario.toLowerCase())) {
			throw new IllegalArgumentException("El nombre de usuario ya existe");
		}

		return nombreUsuario.toLowerCase();
	}

	/**
	 * Guarda una persona en el sistema.
	 * <p>
	 * Las credenciales asociadas se guardan automáticamente por cascade.
	 * </p>
	 *
	 * @param persona la persona a guardar
	 * @return la persona guardada con su ID generado
	 */
	public Persona guardar(Persona persona) {
		return personaRepository.save(persona);
	}

	/**
	 * Busca una persona por su identificador.
	 *
	 * @param id el identificador de la persona
	 * @return la persona encontrada, o {@code null} si no existe
	 */
	public Persona buscarPorId(Long id) {
		return personaRepository.findById(id).orElse(null);
	}

	/**
	 * Obtiene todas las personas registradas en el sistema.
	 *
	 * @return lista de todas las personas
	 */
	public java.util.List<Persona> obtenerTodas() {
		return personaRepository.findAll();
	}

}