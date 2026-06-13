package com.example.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {


    List<Professor> findByHakgwa_HakgwaId(Integer hakgwaId);

    Optional<Professor> findByNameKr(String nameKr);

    List<Professor> findByHakgwa_Gyeyeol_GyeyeolNameKr(String gyeyeolNameKr);


}
