package com.adrianpaneda.tarea3AD2024base.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.repositorios.ArtistaRepository;

/**
 * Servicio para gestionar operaciones relacionadas con artistas del circo.
 * <p>
 * Proporciona métodos para crear, actualizar, eliminar y consultar artistas,
 * además de validaciones de negocio específicas.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 */
@Service
public class ArtistaService {

	@Autowired
	private ArtistaRepository artistaRepository;

	/**
	 * Obtiene todos los artistas registrados.
	 *
	 * @return lista de todos los artistas
	 */
	public List<Artista> obtenerTodos() {
		return artistaRepository.findAll();
	}

	/**
	 * Obtiene un artista por su ID.
	 *
	 * @param id el identificador del artista
	 * @return Optional con el artista si existe
	 */
	public Optional<Artista> obtenerPorId(Long id) {
		return artistaRepository.findById(id);
	}

	/**
	 * Obtiene el artista asociado a un nombre de usuario.
	 * <p>
	 * Se utiliza al cargar la ficha del artista logueado, navegando desde las
	 * credenciales guardadas en sesión hasta el artista.
	 * </p>
	 *
	 * @param nombreUsuario el nombre de usuario de las credenciales
	 * @return Optional con el artista si existe
	 */
	public Optional<Artista> obtenerPorNombreUsuario(String nombreUsuario) {
		return artistaRepository.findByCredencialesNombreUsuario(nombreUsuario);
	}

	/**
	 * Guarda o actualiza un artista.
	 * <p>
	 * Realiza validaciones de negocio antes de persistir: - Verifica que tenga al
	 * menos una especialidad
	 * </p>
	 *
	 * @param artista el artista a guardar
	 * @return el artista guardado con su ID asignado
	 * @throws IllegalArgumentException si las validaciones fallan
	 */
	@Transactional
	public Artista guardar(Artista artista) {
		validarArtista(artista);
		return artistaRepository.save(artista);
	}

	/**
	 * Elimina un artista por su ID.
	 * <p>
	 * Verifica que el artista exista antes de eliminarlo.
	 * </p>
	 *
	 * @param id el identificador del artista a eliminar
	 * @throws IllegalArgumentException si el artista no existe
	 */
	@Transactional
	public void eliminar(Long id) {
		if (!artistaRepository.existsById(id)) {
			throw new IllegalArgumentException("El artista con ID " + id + " no existe");
		}
		artistaRepository.deleteById(id);
	}

	/**
	 * Obtiene todos los números en los que ha participado un artista.
	 * <p>
	 * Retorna los números ordenados por fecha de inicio del espectáculo (más
	 * recientes primero). Cada número incluye la referencia a su espectáculo
	 * completo.
	 * </p>
	 *
	 * @param artistaId el id del artista
	 * @return lista de números en los que participa el artista
	 */
	public List<Numero> obtenerNumerosPorArtista(Long artistaId) {
		return artistaRepository.findNumerosByArtistaId(artistaId);
	}

	/**
	 * Valida que un artista cumpla con las reglas de negocio.
	 *
	 * @param artista el artista a validar
	 * @throws IllegalArgumentException si alguna validación falla
	 */
	private void validarArtista(Artista artista) {
		if (artista.getEspecialidades() == null || artista.getEspecialidades().isEmpty()) {
			throw new IllegalArgumentException("El artista debe tener al menos una especialidad");
		}
	}
}
