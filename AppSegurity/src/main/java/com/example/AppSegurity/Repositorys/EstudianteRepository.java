package com.example.AppSegurity.Repositorys;

import com.example.AppSegurity.Models.Estudiante;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EstudianteRepository extends MongoRepository<Estudiante, String>{
    
    //Busca y trae el email del estudiante
    Optional<Estudiante> findByEmail(String email);
    
}
