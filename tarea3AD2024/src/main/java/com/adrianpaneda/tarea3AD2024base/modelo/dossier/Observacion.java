package com.adrianpaneda.tarea3AD2024base.modelo.dossier;

import java.time.LocalDateTime;

public class Observacion {

	private LocalDateTime fecha = LocalDateTime.now();

	private String texto;

	private String autor;

	public Observacion(LocalDateTime fecha, String texto, String autor) {
		super();
		this.fecha = fecha;
		this.texto = texto;
		this.autor = autor;
	}

	public Observacion() {
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

}
