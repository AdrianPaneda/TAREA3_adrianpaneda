package com.adrianpaneda.tarea3AD2024base.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.repositorios.NumeroRepository;

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
	 * Guarda un número artístico después de validar su duración.
	 * <p>
	 * Antes de persistir el número, se verifica que su duración cumpla con el
	 * formato requerido (x.0 o x.5). Si la validación falla, se lanza una excepción
	 * y el número no se guarda.
	 * </p>
	 *
	 * @param numero el número a guardar
	 * @return el número guardado con su ID generado
	 * @throws IllegalArgumentException si la duración no cumple el formato
	 */
	public Numero guardar(Numero numero) {
		validarFormatoDuracion(numero.getDuracion());
		return numeroRepository.save(numero);
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
}