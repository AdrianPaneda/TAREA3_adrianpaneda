package com.adrianpaneda.tarea3AD2024base.modelo.dossier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.adrianpaneda.tarea3AD2024base.modelo.Especialidad;

@Document
public class Dossier {

	@Id
	private String id;

	private Long idArtista;

	private String nombre;

	private String nacionalidad;

	private String email;

	private String apodo;

	private Set<Especialidad> especialidades;

	private List<Trayectoria> trayectoria = new ArrayList<Trayectoria>();

	private List<Evaluacion> evaluaciones = new ArrayList<Evaluacion>();

	private List<Observacion> observaciones = new ArrayList<Observacion>();

	public String getApodo() {
		return apodo;
	}

	public void setApodo(String apodo) {
		this.apodo = apodo;
	}

	public Dossier(Long idArtista, String nombre, String nacionalidad, String email, String apodo,
			Set<Especialidad> especialidades, List<Trayectoria> trayectoria, List<Evaluacion> evaluaciones,
			List<Observacion> observaciones) {
		super();
		this.idArtista = idArtista;
		this.nombre = nombre;
		this.nacionalidad = nacionalidad;
		this.email = email;
		this.apodo = apodo;
		this.especialidades = especialidades;
		this.trayectoria = trayectoria;
		this.evaluaciones = evaluaciones;
		this.observaciones = observaciones;
	}

	public Dossier() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Trayectoria> getTrayectoria() {
		return trayectoria;
	}

	public void setTrayectoria(List<Trayectoria> trayectoria) {
		this.trayectoria = trayectoria;
	}

	public Long getIdArtista() {
		return idArtista;
	}

	public void setIdArtista(Long idArtista) {
		this.idArtista = idArtista;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Especialidad> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(Set<Especialidad> especialidades) {
		this.especialidades = especialidades;
	}

	public List<Trayectoria> getEspectaculos() {
		return trayectoria;
	}

	public void setEspectaculos(List<Trayectoria> espectaculos) {
		this.trayectoria = espectaculos;
	}

	public List<Evaluacion> getEvaluaciones() {
		return evaluaciones;
	}

	public void setEvaluaciones(List<Evaluacion> evaluaciones) {
		this.evaluaciones = evaluaciones;
	}

	public List<Observacion> getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(List<Observacion> observaciones) {
		this.observaciones = observaciones;
	}

}
