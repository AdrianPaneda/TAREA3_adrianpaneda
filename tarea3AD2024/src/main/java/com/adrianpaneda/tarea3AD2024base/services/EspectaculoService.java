package com.adrianpaneda.tarea3AD2024base.services;

import java.io.File;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.adrianpaneda.tarea3AD2024base.config.SessionManager;
import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;
import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.modelo.db4o.TipoOperacion;
import com.adrianpaneda.tarea3AD2024base.repositorios.EspectaculoRepository;
import com.adrianpaneda.tarea3AD2024base.services.db4o.LogOperacionService;

import jakarta.transaction.Transactional;

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

	@Autowired
	private LogOperacionService logOperacionService;

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
	 * Guarda un espectáculo en el sistema después de validar todas las reglas y
	 * registra la operación en el log.
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
	@Transactional
	public Espectaculo guardar(Espectaculo espectaculo) {
		validarLongitudNombre(espectaculo.getNombre());
		validarNombreUnico(espectaculo.getNombre());
		validarDuracionMaxima(espectaculo.getFechaInicio(), espectaculo.getFechaFin());
		validarMinimoNumeros(espectaculo);

		Espectaculo guardado = espectaculoRepository.save(espectaculo);

		logOperacionService.registrar(SessionManager.getCurrentUsername(), TipoOperacion.NUEVO,
				"Se ha insertado un nuevo Espectáculo de id " + guardado.getId());

		return guardado;
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

	/**
	 * Guarda un espectáculo sin validar el mínimo de números y registra la
	 * operación en el log.
	 * <p>
	 * Se utiliza al crear o modificar un espectáculo porque los números se añaden
	 * en una pantalla posterior. La operación se registra como NUEVO si el
	 * espectáculo no tenía ID previo (creación) o como ACTUALIZACION si ya existía
	 * (modificación).
	 * </p>
	 *
	 * @param espectaculo el espectáculo a guardar
	 * @return el espectáculo guardado
	 */
	@Transactional
	public Espectaculo guardarSinValidarNumeros(Espectaculo espectaculo) {
		boolean esNuevo = (espectaculo.getId() == null);

		Espectaculo guardado = espectaculoRepository.save(espectaculo);

		if (esNuevo) {
			logOperacionService.registrar(SessionManager.getCurrentUsername(), TipoOperacion.NUEVO,
					"Se ha insertado un nuevo Espectáculo de id " + guardado.getId());
		} else {
			logOperacionService.registrar(SessionManager.getCurrentUsername(), TipoOperacion.ACTUALIZACION,
					"Se ha actualizado la información del id " + guardado.getId() + " de Espectáculo");
		}

		return guardado;
	}

	public String generarXMLEspectaculo(Espectaculo esp) {

		try {
			// Crear XML con nodo raiz "informe"
			Document documento = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation()
					.createDocument(null, "informe", null);
			documento.setXmlVersion("1.0");

			// Añadir nodo fecha
			Element fecha = documento.createElement("fechahora");
			String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
			fecha.setTextContent(fechaActual);
			documento.getDocumentElement().appendChild(fecha);

			// ---- ESPECTACULO ----
			Element espectaculo = documento.createElement("espectaculo");
			documento.getDocumentElement().appendChild(espectaculo);
			Element id = documento.createElement("id");
			id.setTextContent(esp.getId().toString());
			espectaculo.appendChild(id);
			Element nombre = documento.createElement("nombre");
			nombre.setTextContent(esp.getNombre());
			espectaculo.appendChild(nombre);
			Element fechaini = documento.createElement("fechaini");
			fechaini.setTextContent(esp.getFechaInicio().toString());
			espectaculo.appendChild(fechaini);
			Element fechafin = documento.createElement("fechafin");
			fechafin.setTextContent(esp.getFechaFin().toString());
			espectaculo.appendChild(fechafin);

			// ---- COORDINACION ----
			Element coordinacion = documento.createElement("coordinacion");
			espectaculo.appendChild(coordinacion);
			Element nombreCoord = documento.createElement("nombre");
			nombreCoord.setTextContent(esp.getCoordinacion().getNombre());
			coordinacion.appendChild(nombreCoord);
			Element emailCoord = documento.createElement("email");
			emailCoord.setTextContent(esp.getCoordinacion().getEmail());
			coordinacion.appendChild(emailCoord);
			Element senior = documento.createElement("senior");
			String esSenior = esp.getCoordinacion().isSenior() ? "si" : "no";
			senior.setTextContent(esSenior);
			coordinacion.appendChild(senior);

			// ---- NUMEROS ----
			Element numeros = documento.createElement("numeros");
			espectaculo.appendChild(numeros);

			for (Numero num : esp.getNumeros()) {
				Element numero = documento.createElement("numero");
				numeros.appendChild(numero);

				Element orden = documento.createElement("orden");
				orden.setTextContent(String.valueOf(num.getOrden()));
				Element nombreNum = documento.createElement("nombre");
				nombreNum.setTextContent(num.getNombre());
				Element duracion = documento.createElement("duracion");
				duracion.setTextContent(String.valueOf(num.getDuracion()));
				Element artistas = documento.createElement("artistas");

				numero.appendChild(orden);
				numero.appendChild(nombreNum);
				numero.appendChild(duracion);
				numero.appendChild(artistas);

				for (Artista art : num.getArtistas()) {
					Element artista = documento.createElement("artista");
					artistas.appendChild(artista);

					Element nombreArt = documento.createElement("nombre");
					nombreArt.setTextContent(art.getNombre());
					Element nacionalidad = documento.createElement("nacionalidad");
					nacionalidad.setTextContent(art.getNacionalidad());
					Element email = documento.createElement("email");
					email.setTextContent(art.getEmail());
					Element especialidades = documento.createElement("especialidades");
					String especialidadesCadena = "";
					for (Especialidad espec : art.getEspecialidades()) {
						especialidadesCadena = especialidadesCadena + espec + ", ";
					}
					if (!especialidadesCadena.isEmpty()) {
						especialidadesCadena = especialidadesCadena.substring(0, especialidadesCadena.length() - 2);
					}
					especialidades.setTextContent(especialidadesCadena);

					artista.appendChild(nombreArt);
					artista.appendChild(nacionalidad);
					artista.appendChild(email);
					artista.appendChild(especialidades);

					if (art.getApodo() != null) {
						Element apodo = documento.createElement("apodo");
						apodo.setTextContent(art.getApodo());
						artista.appendChild(apodo);
					}
				}
			}

			// ---- GUARDAR EN /ficheros Y CONVERTIR A STRING ----
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			// Guardar en /ficheros
			File carpeta = new File("ficheros");
			if (!carpeta.exists())
				carpeta.mkdirs();
			transformer.transform(new DOMSource(documento),
					new StreamResult(new File("ficheros/informe_espectaculo" + esp.getId() + ".xml")));

			// Convertir a String para devolver
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(documento), new StreamResult(writer));
			return writer.toString();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (javax.xml.transform.TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}