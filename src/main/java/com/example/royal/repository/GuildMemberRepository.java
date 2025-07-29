package com.example.royal.repository;

import com.example.royal.model.GuildMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuildMemberRepository extends JpaRepository<GuildMember, Long> {
    // 이름 혹은 부캐(서브 캐릭터 이름들 중 하나)에서 검색
    @Query("SELECT DISTINCT gm FROM GuildMember gm " +
            "LEFT JOIN gm.subCharacters sc " +
            "LEFT JOIN gm.knightCharacters kc " +
            "WHERE LOWER(gm.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(sc.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kc.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<GuildMember> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}