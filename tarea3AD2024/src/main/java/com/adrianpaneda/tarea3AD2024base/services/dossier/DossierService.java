package com.adrianpaneda.tarea3AD2024base.services.dossier;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Artista;
import com.adrianpaneda.tarea3AD2024base.modelo.Espectaculo;
import com.adrianpaneda.tarea3AD2024base.modelo.Numero;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.Dossier;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.NumeroTrayectoria;
import com.adrianpaneda.tarea3AD2024base.modelo.dossier.Trayectoria;
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

	public Dossier actualizarDossier(Artista artista) {

		Optional<Dossier> dossierOpt = Optional.ofNullable(dossierRepo.findByIdArtista(artista.getId()));
		if (dossierOpt.isPresent()) {

			Dossier dossier = dossierOpt.get();
			dossier.setApodo(artista.getApodo());
			dossier.setEmail(artista.getEmail());
			dossier.setNacionalidad(artista.getNacionalidad());
			dossier.setNombre(artista.getNombre());
			dossier.setEspecialidades(artista.getEspecialidades());

			return dossierRepo.save(dossier);

		} else {

			System.out.println("Error, Dossier no encontrado");

			return null;
		}

	}

	public void actualizarTrayectorias(Artista artista, Numero numero) {

		Espectaculo esp = numero.getEspectaculo();
		// Creamos nueva trayectoria
		Trayectoria trayectoria = new Trayectoria(esp.getId(), esp.getNombre());
		// Creamos el nuevo numero de la trayectoria
		NumeroTrayectoria num = new NumeroTrayectoria();
		num.setIdNumero(numero.getId());
		num.setNombreNumero(numero.getNombre());
		trayectoria.getNumeros().add(num);
		// Dossier dossier = dossierRepo.findByIdArtista(artista.getId());
		Optional<Dossier> dossierOpt = Optional.ofNullable(dossierRepo.findByIdArtista(artista.getId()));

		if (dossierOpt.isPresent()) {

			Dossier dossier = dossierOpt.get();

			for (Trayectoria t : dossier.getTrayectoria()) {

				if (t.getIdEspectaculo().equals(esp.getId())) {

					t.getNumeros().add(num);
					dossierRepo.save(dossier);
					return;
				}

			}

			dossier.getTrayectoria().add(trayectoria);
			dossierRepo.save(dossier);
		}

	}

}
