package com.example.AppSegurity.Repositorys;

import com.example.AppSegurity.Models.Profesor;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfesorRepository extends MongoRepository<Profesor, String>{
    //Busca y trae el email del profesor
    Optional<Profesor> findByEmailInstitucional(String emailInstitucional);
}
