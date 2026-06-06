package com.adrianpaneda.tarea3AD2024base.services.dossier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.Dossier;
import com.adrianpaneda.tarea3AD2024base.repositorios.dossier.DossierRepository;

@Service
public class DossierService {

	@Autowired
	DossierRepository dossierRepo;

	public Dossier registrarDossier(Artista artista) {

		Dossier dossier = new Dossier();
		dossier.setApodo(artista.getApodo());
		dossier.setEmail(artista.getEmail());
		dossier.setIdArtista(artista.getId());
		dossier.setNacionalidad(artista.getNacionalidad());
		dossier.setNombre(artista.getNombre());
		dossier.setEspecialidades(artista.getEspecialidades());

		return dossierRepo.save(dossier);

	}

}
