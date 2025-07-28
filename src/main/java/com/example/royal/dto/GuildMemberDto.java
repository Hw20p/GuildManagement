package com.example.royal.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuildMemberDto {
    private Long id;
    private String name;
    private List<String> subCharacterNames = new ArrayList<>();
    private List<String> knightCharacterNames = new ArrayList<>();
}
