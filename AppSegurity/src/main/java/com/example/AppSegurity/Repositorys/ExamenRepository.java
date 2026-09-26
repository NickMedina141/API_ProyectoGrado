package com.example.AppSegurity.Repositorys;

import com.example.AppSegurity.Models.Examen;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExamenRepository extends MongoRepository<Examen, String>{
    //Obtener todos los examenes que pertenecen a ese profesor
    List<Examen> findByProfesorId(String profesorId);
    
    //Traer el pin del examen
    Optional<Examen> findByControlAccesoPinSesion(String piSesion);
}
