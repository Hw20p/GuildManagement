package com.example.royal.controller;

import com.example.royal.dto.GuildMemberDto;
import com.example.royal.service.GuildMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/guild")
@RequiredArgsConstructor
public class GuildMemberController {

    private final GuildMemberService service;

    // 전체 길드 멤버 조회
    @GetMapping
    public String list(Model model) {
        model.addAttribute("members", service.getAll());
        return "guild/member-list";
    }

    // 길드 멤버 생성 폼 페이지 요청
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("member", new GuildMemberDto());
        return "guild/member-create";
    }

    // 길드 멤버 생성
    @PostMapping
    public String create(@ModelAttribute GuildMemberDto dto) {
        service.save(dto);
        return "redirect:/guild";
    }

    // 길드 멤버 수정 폼 페이지
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", service.getById(id));
        return "guild/member-edit";
    }

    // 길드 멤버 수정
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute GuildMemberDto dto) {
        service.update(id, dto);
        return "redirect:/guild";
    }

    // 길드 멤버 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/guild";
    }
}
