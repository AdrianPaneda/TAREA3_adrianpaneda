package com.adrianpaneda.tarea3AD2024base.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.modelo.db4o.TipoOperacion;
import com.adrianpaneda.tarea3AD2024base.repositorios.NumeroRepository;
import com.adrianpaneda.tarea3AD2024base.services.db4o.LogOperacionService;

import jakarta.transaction.Transactional;

/**
 * Servicio para la gestión de números artísticos de espectáculos.
 * <p>
 * Proporciona la lógica de negocio para la creación y modificación de números,
 * incluyendo la validación de la duración en formato x.y donde y solo puede ser
 * 0 o 5 (por ejemplo: 3.0, 5.5, 12.0).
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Numero
 * @see NumeroRepository
 */
@Service
public class NumeroService {

	@Autowired
	private NumeroRepository numeroRepository;

	@Autowired
	private LogOperacionService logOperacionService;

	/**
	 * Valida que la duración del número cumpla el formato requerido.
	 * <p>
	 * La duración debe expresarse en formato x.y donde y solo puede ser 0 o 5.
	 * Ejemplos válidos: 3.0, 5.5, 12.0, 8.5 Ejemplos inválidos: 3.2, 5.7, 12.3
	 * </p>
	 * <p>
	 * El método extrae la parte decimal y verifica que sea exactamente 0 o 5.
	 * Utiliza operaciones matemáticas para evitar problemas de precisión con
	 * números decimales en coma flotante.
	 * </p>
	 *
	 * @param duracion la duración en minutos a validar
	 * @return {@code true} si la duración es válida
	 * @throws IllegalArgumentException si la parte decimal no es 0 o 5
	 */
	public boolean validarFormatoDuracion(double duracion) {
		// Extraer la parte decimal multiplicando por 10 y obteniendo el resto
		int parteDecimal = (int) ((duracion * 10) % 10);

		if (parteDecimal != 0 && parteDecimal != 5) {
			throw new IllegalArgumentException("La duración debe tener formato x.0 o x.5 (ejemplo: 3.0, 5.5)");
		}

		return true;
	}

	/**
	 * Guarda un número artístico después de validar su duración y registra la
	 * operación en el log.
	 * <p>
	 * Antes de persistir el número, se verifica que su duración cumpla con el
	 * formato requerido (x.0 o x.5). La operación se registra como NUEVO si el
	 * número no tenía ID previo o como ACTUALIZACION si ya existía.
	 * </p>
	 *
	 * @param numero el número a guardar
	 * @return el número guardado con su ID generado
	 * @throws IllegalArgumentException si la duración no cumple el formato
	 */
	@Transactional
	public Numero guardar(Numero numero) {
		validarFormatoDuracion(numero.getDuracion());

		boolean esNuevo = (numero.getId() == null);

		Numero guardado = numeroRepository.save(numero);

		if (esNuevo) {
			logOperacionService.registrar(SessionManager.getCurrentUsername(), TipoOperacion.NUEVO,
					"Se ha insertado un nuevo Número de id " + guardado.getId());
		} else {
			logOperacionService.registrar(SessionManager.getCurrentUsername(), TipoOperacion.ACTUALIZACION,
					"Se ha actualizado la información del id " + guardado.getId() + " de Número");
		}

		return guardado;
	}

	/**
	 * Busca un número por su identificador.
	 * <p>
	 * Utilizado para cargar los datos de un número cuando se necesita modificarlo o
	 * visualizar sus detalles completos (artistas participantes, espectáculo al que
	 * pertenece).
	 * </p>
	 *
	 * @param id el identificador del número
	 * @return el número encontrado, o {@code null} si no existe
	 */
	public Numero buscarPorId(Long id) {
		return numeroRepository.findById(id).orElse(null);
	}

	/**
	 * Obtiene todos los números registrados en el sistema.
	 * <p>
	 * Devuelve el listado completo de números artísticos de todos los espectáculos.
	 * Puede ser utilizado para consultas generales o reportes.
	 * </p>
	 *
	 * @return lista de todos los números
	 */
	public List<Numero> obtenerTodos() {
		return numeroRepository.findAll();
	}

	/**
	 * Obtiene los números de un espectáculo ordenados por su campo orden.
	 *
	 * @param espectaculoId el id del espectáculo
	 * @return lista de números ordenados
	 */
	public List<Numero> obtenerPorEspectaculo(Long espectaculoId) {
		return numeroRepository.findByEspectaculoIdOrderByOrdenAsc(espectaculoId);
	}

	/**
	 * Obtiene un número con sus artistas cargados.
	 * <p>
	 * Utiliza JOIN FETCH para cargar los artistas del número en una sola consulta,
	 * evitando LazyInitializationException.
	 * </p>
	 *
	 * @param id el id del número
	 * @return el número con sus artistas, o null si no existe
	 */
	public Numero obtenerConArtistas(Long id) {
		return numeroRepository.findByIdConArtistas(id).orElse(null);
	}
}