package com.adrianpaneda.tarea3AD2024base.modelo.dossier;

public class NumeroTrayectoria {

	private Long idNumero;

	private String nombreNumero;

	public NumeroTrayectoria(Long idNumero, String nombreNumero) {
		super();
		this.idNumero = idNumero;
		this.nombreNumero = nombreNumero;
	}

	public NumeroTrayectoria() {

	}

	public Long getIdNumero() {
		return idNumero;
	}

	public void setIdNumero(Long idNumero) {
		this.idNumero = idNumero;
	}

	public String getNombreNumero() {
		return nombreNumero;
	}

	public void setNombreNumero(String nombreNumero) {
		this.nombreNumero = nombreNumero;
	}

}
