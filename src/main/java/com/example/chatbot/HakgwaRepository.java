package com.example.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HakgwaRepository extends JpaRepository<Hakgwa, Integer> {
    List<Hakgwa> findByGyeyeol_GyeyeolId(Integer gyeyeolId);
}