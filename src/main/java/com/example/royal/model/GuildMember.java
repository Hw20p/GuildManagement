package com.example.royal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuildMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "guildMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCharacter> subCharacters = new ArrayList<>();

    @OneToMany(mappedBy = "guildMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnightCharacter> knightCharacters = new ArrayList<>();
}

