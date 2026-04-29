package com.adrianpaneda.tarea3AD2024base.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.repositorios.ArtistaRepository;

/**
 * Servicio para la gestión de artistas del circo.
 * <p>
 * Proporciona la lógica de negocio para la gestión de artistas (CU3),
 * asignación a números (CU5C) y visualización de ficha (CU6).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Artista
 * @see ArtistaRepository
 */
@Service
public class ArtistaService {

	@Autowired
	private ArtistaRepository artistaRepository;

	/**
	 * Guarda un artista en el sistema.
	 * 
	 * @param artista el artista a guardar
	 * @return el artista guardado con su ID generado
	 */
	public Artista guardar(Artista artista) {
		return artistaRepository.save(artista);
	}

	/**
	 * Busca un artista por su identificador.
	 * 
	 * @param id el identificador del artista
	 * @return el artista encontrado, o {@code null} si no existe
	 */
	public Artista buscarPorId(Long id) {
		return artistaRepository.findById(id).orElse(null);
	}

	/**
	 * Obtiene todos los artistas registrados en el sistema.
	 *
	 * @return lista de todos los artistas
	 */
	public List<Artista> obtenerTodos() {
		return artistaRepository.findAll();
	}
}