package com.example.royal.service;

import com.example.royal.dto.GuildMemberDto;
import com.example.royal.model.GuildMember;
import com.example.royal.model.KnightCharacter;
import com.example.royal.model.SubCharacter;
import com.example.royal.repository.GuildMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuildMemberService {

    private final GuildMemberRepository memberRepo;

    public Page<GuildMemberDto> getAll(Pageable pageable) {
        return memberRepo.findAll(pageable)
                .map(this::toDto);
    }

    public List<GuildMemberDto> getAllMembers() {
        return memberRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public GuildMemberDto getById(Long id) {
        return toDto(memberRepo.findById(id).orElseThrow());
    }

    public void save(GuildMemberDto dto) {
        GuildMember member = new GuildMember();
        member.setName(dto.getName());

        List<SubCharacter> subs = dto.getSubCharacterNames().stream()
                .map(name -> new SubCharacter(null, name, member))
                .collect(Collectors.toList());

        List<KnightCharacter> knights = dto.getKnightCharacterNames().stream()
                .map(name -> new KnightCharacter(null, name, member))
                .collect(Collectors.toList());

        member.setSubCharacters(subs);
        member.setKnightCharacters(knights);

        memberRepo.save(member);
    }

    public void delete(Long id) {
        memberRepo.deleteById(id);
    }

    public void update(Long id, GuildMemberDto dto) {
        GuildMember member = memberRepo.findById(id).orElseThrow();
        member.setName(dto.getName());

        member.getSubCharacters().clear();
        member.getKnightCharacters().clear();

        dto.getSubCharacterNames().forEach(name ->
                member.getSubCharacters().add(new SubCharacter(null, name, member)));
        dto.getKnightCharacterNames().forEach(name ->
                member.getKnightCharacters().add(new KnightCharacter(null, name, member)));

        memberRepo.save(member);
    }

    private GuildMemberDto toDto(GuildMember member) {
        return GuildMemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .subCharacterNames(member.getSubCharacters().stream().map(SubCharacter::getName).toList())
                .knightCharacterNames(member.getKnightCharacters().stream().map(KnightCharacter::getName).toList())
                .build();
    }

    public Page<GuildMemberDto> searchByKeyword(String keyword, Pageable pageable) {
        return memberRepo.searchByKeyword(keyword, pageable)
                .map(this::toDto);
    }


}
