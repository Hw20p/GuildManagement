package com.example.royal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnightCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "guild_member_id")
    private GuildMember guildMember;

    public KnightCharacter(String name, GuildMember guildMember) {
        this.name = name;
        this.guildMember = guildMember;
    }
}
