package com.adrianpaneda.tarea3AD2024base.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.repositorios.EspectaculoRepository;

/**
 * Servicio para la gestión de espectáculos del circo.
 * <p>
 * Proporciona la lógica de negocio para la creación, modificación y consulta de
 * espectáculos, incluyendo todas las validaciones de reglas de negocio
 * requeridas: unicidad de nombre, duración máxima de 1 año, y mínimo de 3
 * números por espectáculo.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.0
 * @since 2025-01-01
 * @see Espectaculo
 * @see EspectaculoRepository
 */
@Service
public class EspectaculoService {

	@Autowired
	private EspectaculoRepository espectaculoRepository;

	/**
	 * Valida que el nombre del espectáculo sea único en el sistema.
	 * <p>
	 * El nombre del espectáculo es único y no puede duplicarse. Este método
	 * verifica que no exista ya un espectáculo con el mismo nombre antes de
	 * permitir el registro.
	 * </p>
	 *
	 * @param nombre el nombre del espectáculo a validar
	 * @return {@code true} si el nombre es válido (no existe)
	 * @throws IllegalArgumentException si el nombre ya está registrado
	 */
	public boolean validarNombreUnico(String nombre) {
		if (espectaculoRepository.existsByNombre(nombre)) {
			throw new IllegalArgumentException("Ya existe un espectáculo con ese nombre");
		}
		return true;
	}

	/**
	 * Valida que el nombre del espectáculo no exceda el límite de caracteres.
	 * <p>
	 * El nombre del espectáculo tiene un máximo de 25 caracteres según las reglas
	 * de negocio del circo.
	 * </p>
	 *
	 * @param nombre el nombre a validar
	 * @return {@code true} si el nombre cumple la longitud permitida
	 * @throws IllegalArgumentException si el nombre excede 25 caracteres
	 */
	public boolean validarLongitudNombre(String nombre) {
		if (nombre.length() > 25) {
			throw new IllegalArgumentException("El nombre del espectáculo no puede exceder 25 caracteres");
		}
		return true;
	}

	/**
	 * Valida que la duración del espectáculo no exceda 1 año.
	 * <p>
	 * Un espectáculo no puede durar más de 1 año desde su fecha de inicio hasta su
	 * fecha de fin. Este método calcula el periodo entre ambas fechas y verifica
	 * que no supere los 365 días.
	 * </p>
	 *
	 * @param fechaInicio la fecha de inicio del espectáculo
	 * @param fechaFin    la fecha de fin del espectáculo
	 * @return {@code true} si la duración es válida (≤ 1 año)
	 * @throws IllegalArgumentException si la duración excede 1 año
	 */
	public boolean validarDuracionMaxima(LocalDate fechaInicio, LocalDate fechaFin) {

		// Period periodo = Period.between(fechaInicio, fechaFin);

		long diasTotales = ChronoUnit.DAYS.between(fechaInicio, fechaFin);

		if (diasTotales > 365) {
			throw new IllegalArgumentException("La duración del espectáculo no puede exceder 1 año (365 días)");
		}

		return true;
	}

	/**
	 * Valida que el espectáculo tenga al menos 3 números artísticos.
	 * <p>
	 * Según las reglas de negocio, un espectáculo debe estar compuesto por un
	 * mínimo de 3 números. Esta validación se realiza antes de guardar el
	 * espectáculo en el sistema.
	 * </p>
	 *
	 * @param espectaculo el espectáculo a validar
	 * @return {@code true} si tiene al menos 3 números
	 * @throws IllegalArgumentException si tiene menos de 3 números
	 */
	public boolean validarMinimoNumeros(Espectaculo espectaculo) {
		if (espectaculo.getNumeros() == null || espectaculo.getNumeros().size() < 3) {
			throw new IllegalArgumentException("El espectáculo debe tener al menos 3 números artísticos");
		}
		return true;
	}

	/**
	 * Guarda un espectáculo en el sistema después de validar todas las reglas.
	 * <p>
	 * Antes de persistir el espectáculo, se verifican todas las validaciones:
	 * nombre único, longitud del nombre, duración máxima y mínimo de números. Si
	 * alguna validación falla, se lanza una excepción y el espectáculo no se
	 * guarda.
	 * </p>
	 *
	 * @param espectaculo el espectáculo a guardar
	 * @return el espectáculo guardado con su ID generado
	 * @throws IllegalArgumentException si alguna validación falla
	 */
	public Espectaculo guardar(Espectaculo espectaculo) {
		// Validar todas las reglas antes de guardar
		validarLongitudNombre(espectaculo.getNombre());
		validarNombreUnico(espectaculo.getNombre());
		validarDuracionMaxima(espectaculo.getFechaInicio(), espectaculo.getFechaFin());
		validarMinimoNumeros(espectaculo);

		return espectaculoRepository.save(espectaculo);
	}

	/**
	 * Busca un espectáculo por su identificador.
	 * <p>
	 * Utilizado para cargar los datos completos de un espectáculo cuando se
	 * necesita visualizar su detalle (números que lo componen, coordinador,
	 * artistas participantes) o cuando se va a modificar.
	 * </p>
	 *
	 * @param id el identificador del espectáculo
	 * @return el espectáculo encontrado, o {@code null} si no existe
	 */
	public Espectaculo buscarPorId(Long id) {
		return espectaculoRepository.findById(id).orElse(null);
	}

	/**
	 * Obtiene todos los espectáculos registrados en el sistema.
	 * <p>
	 * Devuelve el listado completo de espectáculos (pasados, vigentes y futuros)
	 * para mostrar en la vista principal. Incluye información básica: id, nombre,
	 * fecha de inicio y fecha de fin.
	 * </p>
	 *
	 * @return lista de todos los espectáculos
	 */
	public List<Espectaculo> obtenerTodos() {
		return espectaculoRepository.findAll();
	}

	/**
	 * Obtiene un espectáculo por su ID cargando todos sus datos relacionados.
	 * <p>
	 * A diferencia de {@link #buscarPorId(Long)}, este método carga en una sola
	 * consulta la coordinación, los números y los artistas de cada número, evitando
	 * problemas de LazyInitializationException al acceder a colecciones fuera de la
	 * transacción JPA.
	 * </p>
	 * <p>
	 * Se utiliza para mostrar el detalle completo de un espectáculo.
	 * </p>
	 *
	 * @param id el identificador del espectáculo
	 * @return el espectáculo con todos sus datos relacionados, o {@code null} si no
	 *         existe
	 */
	public Espectaculo obtenerConDetalle(Long id) {
		return espectaculoRepository.findByIdConDetalle(id).orElse(null);
	}
}