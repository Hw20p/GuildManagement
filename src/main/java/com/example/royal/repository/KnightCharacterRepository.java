package com.example.royal.repository;

import com.example.royal.model.KnightCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnightCharacterRepository extends JpaRepository<KnightCharacter, Long> {}