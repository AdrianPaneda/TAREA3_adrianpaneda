package com.adrianpaneda.tarea3AD2024base.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.adrianpaneda.tarea3AD2024base.modelo.Persona;
import com.adrianpaneda.tarea3AD2024base.repositorios.PersonaRepository;

/**
 * Servicio para la gestión de personas del circo.
 * <p>
 * Proporciona la lógica de negocio para el registro, modificación y eliminación
 * de personas (CU3), incluyendo todas las validaciones requeridas por la
 * rúbrica.
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
	 * Valida que el email no esté duplicado en el sistema, excluyendo el registro
	 * actual.
	 * <p>
	 * Se utiliza al modificar una persona para permitir que mantenga su email
	 * actual sin que se considere duplicado.
	 * </p>
	 *
	 * @param email     el email a validar
	 * @param idPersona el id de la persona que se está modificando
	 * @return {@code true} si el email es válido (no existe en otra persona)
	 * @throws IllegalArgumentException si el email ya pertenece a otra persona
	 */
	public boolean validarEmailUnicoExcluyendo(String email, Long idPersona) {
		Persona personaConEmail = personaRepository.findByEmail(email);
		if (personaConEmail != null && !personaConEmail.getId().equals(idPersona)) {
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
	 * Actualiza los datos de una persona existente.
	 * <p>
	 * No modifica las credenciales asociadas (estas son inmutables tras el
	 * registro). Se valida que el email sea único excluyendo el registro actual
	 * para permitir que mantenga su email.
	 * </p>
	 *
	 * @param persona la persona a actualizar
	 * @return la persona actualizada
	 * @throws IllegalArgumentException si el email pertenece a otra persona
	 */
	@Transactional
	public Persona actualizar(Persona persona) {
		validarEmailUnicoExcluyendo(persona.getEmail(), persona.getId());
		return personaRepository.save(persona);
	}

	/**
	 * Elimina una persona del sistema por su ID.
	 * <p>
	 * Las credenciales asociadas se eliminan automáticamente por cascade
	 * configurado en la entidad Persona.
	 * </p>
	 *
	 * @param id el identificador de la persona a eliminar
	 * @throws IllegalArgumentException si la persona no existe
	 */
	@Transactional
	public void eliminar(Long id) {
		if (!personaRepository.existsById(id)) {
			throw new IllegalArgumentException("La persona con ID " + id + " no existe");
		}
		personaRepository.deleteById(id);
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
	public List<Persona> obtenerTodas() {
		return personaRepository.findAll();
	}
	
	/**
	 * @param nodos
	 * @return Map<Integer,String> Metodo para recorrer nodos del xml paises
	 * 
	 */
	private static Map<String, String> leerNodos(NodeList nodos) {

		Map<String, String> paises = new TreeMap<>();
		Node nodo;
		for (int i = 0; i < nodos.getLength(); i++) {
			nodo = nodos.item(i);
			// Compruebo que el nodo es pais y añado al Map
			if (nodo.getNodeName().equals("pais")) {
				Element pais = (Element) nodo;
				String id = pais.getElementsByTagName("id").item(0).getTextContent();
				String nombre = pais.getElementsByTagName("nombre").item(0).getTextContent();
				paises.put(id, nombre);
			}
		}
		return paises;
	}

	/**
	 * Metodo para listar paises
	 * 
	 * @param fichero
	 * @return Map<String, String>
	 */
	public static Map<String, String> listarPaises(File fichero) {
		System.out.println("**Listado de paises**");
		Map<String, String> listaPaises = new TreeMap<>();
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(fichero);
			Node raiz = doc.getFirstChild();
			System.out.println(raiz.getNodeName());
			NodeList paises = raiz.getChildNodes();

			listaPaises = leerNodos(paises);

		} catch (IOException e) {
			System.err.println("Se produjo una IOException:" + e.getLocalizedMessage());
			e.printStackTrace();

		} catch (SAXException e) {
			System.out.println("Se produjo una SAXException: " + e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.out.println("Se produjo una ParserConfigurationException: " + e.getMessage());
			e.printStackTrace();
		}
		return listaPaises;

	}
	
}