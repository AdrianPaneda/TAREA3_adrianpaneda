package com.adrianpaneda.tarea3AD2024base.modelo.dossier;

import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;

public class Evaluador {

	private Long idPersona;

	private Perfil rol;

	public Evaluador(Long idPersona, Perfil rol) {
		super();
		this.idPersona = idPersona;
		this.rol = rol;
	}

	public Evaluador() {
		super();
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Perfil getRol() {
		return rol;
	}

	public void setRol(Perfil rol) {
		this.rol = rol;
	}

}
