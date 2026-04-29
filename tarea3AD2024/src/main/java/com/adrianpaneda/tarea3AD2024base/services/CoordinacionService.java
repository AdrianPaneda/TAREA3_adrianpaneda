package com.adrianpaneda.tarea3AD2024base.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Coordinacion;
import com.adrianpaneda.tarea3AD2024base.repositorios.CoordinacionRepository;

/**
 * Servicio para la gestión de personas de coordinación del circo.
 * <p>
 * Proporciona la lógica de negocio para el registro y modificación de personas
 * de coordinación, incluyendo la gestión de la categoría senior y la asignación
 * de espectáculos que dirigen.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Coordinacion
 * @see CoordinacionRepository
 */
@Service
public class CoordinacionService {

	@Autowired
	private CoordinacionRepository coordinacionRepository;

	/**
	 * Guarda una persona de coordinación en el sistema.
	 * <p>
	 * Si la coordinación tiene categoría senior, el campo {@code fechaSenior} debe
	 * estar rellenado. Si no es senior, {@code fechaSenior} debe ser null. Las
	 * validaciones de datos personales (email, credenciales) se realizan en
	 * {@link PersonaService} antes de llamar a este método.
	 * </p>
	 *
	 * @param coordinacion la persona de coordinación a guardar
	 * @return la coordinación guardada con su ID generado
	 */
	public Coordinacion guardar(Coordinacion coordinacion) {
		return coordinacionRepository.save(coordinacion);
	}

	/**
	 * Busca una persona de coordinación por su identificador.
	 * <p>
	 * Utilizado cuando se necesita cargar los datos de una coordinación para
	 * modificarlos o para asignarla como directora de un espectáculo.
	 * </p>
	 *
	 * @param id el identificador de la coordinación
	 * @return la coordinación encontrada, o {@code null} si no existe
	 */
	public Coordinacion buscarPorId(Long id) {
		return coordinacionRepository.findById(id).orElse(null);
	}

	/**
	 * Obtiene todas las personas de coordinación registradas en el sistema.
	 * <p>
	 * Utilizado para mostrar el listado completo de coordinaciones disponibles,
	 * especialmente cuando el administrador necesita seleccionar una coordinación
	 * para asignarla como directora de un espectáculo.
	 * </p>
	 *
	 * @return lista de todas las personas de coordinación
	 */
	public List<Coordinacion> obtenerTodas() {
		return coordinacionRepository.findAll();
	}
}