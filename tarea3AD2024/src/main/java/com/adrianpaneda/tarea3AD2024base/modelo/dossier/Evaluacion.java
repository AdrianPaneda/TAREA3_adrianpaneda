package com.adrianpaneda.tarea3AD2024base.modelo.dossier;

import java.time.LocalDateTime;

public class Evaluacion {

	private LocalDateTime fecha = LocalDateTime.now();

	private Evaluador realizadaPor;

	private String comentario;

	private NivelEvaluacion nivel;

	public Evaluacion(LocalDateTime fecha, Evaluador realizadaPor, String comentario, NivelEvaluacion nivel) {
		super();
		this.fecha = fecha;
		this.realizadaPor = realizadaPor;
		this.comentario = comentario;
		this.nivel = nivel;
	}

	public Evaluacion() {
		super();
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public Evaluador getRealizadaPor() {
		return realizadaPor;
	}

	public void setRealizadaPor(Evaluador realizadaPor) {
		this.realizadaPor = realizadaPor;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public NivelEvaluacion getNivel() {
		return nivel;
	}

	public void setNivel(NivelEvaluacion nivel) {
		this.nivel = nivel;
	}

}
