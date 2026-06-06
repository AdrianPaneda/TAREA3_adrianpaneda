package com.adrianpaneda.tarea3AD2024base.modelo.dossier;

import java.util.List;

public class Trayectoria {

	private Long idEspectaculo;

	private String nombreEspectaculo;

	private List<NumeroTrayectoria> numeros;

	public Trayectoria(Long idEspectaculo, String nombreEspectaculo, List<NumeroTrayectoria> numeros) {
		super();
		this.idEspectaculo = idEspectaculo;
		this.nombreEspectaculo = nombreEspectaculo;
		this.numeros = numeros;
	}

	public Trayectoria() {
		super();
	}

	public Long getIdEspectaculo() {
		return idEspectaculo;
	}

	public void setIdEspectaculo(Long idEspectaculo) {
		this.idEspectaculo = idEspectaculo;
	}

	public String getNombreEspectaculo() {
		return nombreEspectaculo;
	}

	public void setNombreEspectaculo(String nombreEspectaculo) {
		this.nombreEspectaculo = nombreEspectaculo;
	}

	public List<NumeroTrayectoria> getNumeros() {
		return numeros;
	}

	public void setNumeros(List<NumeroTrayectoria> numeros) {
		this.numeros = numeros;
	}

}
