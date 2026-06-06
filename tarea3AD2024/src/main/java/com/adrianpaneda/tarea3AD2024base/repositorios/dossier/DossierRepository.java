package com.adrianpaneda.tarea3AD2024base.repositorios.dossier;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.adrianpaneda.tarea3AD2024base.modelo.dossier.Dossier;
import com.mongodb.lang.Nullable;

public interface DossierRepository extends MongoRepository<Dossier, String> {

	@Nullable
	Dossier findByIdArtista(Long idArtista);

}
