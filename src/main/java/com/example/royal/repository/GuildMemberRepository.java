package com.example.royal.repository;

import com.example.royal.model.GuildMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuildMemberRepository extends JpaRepository<GuildMember, Long> {}