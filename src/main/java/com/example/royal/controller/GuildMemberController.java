package com.example.royal.controller;

import com.example.royal.dto.GuildMemberDto;
import com.example.royal.service.DiscordMessageService;
import com.example.royal.service.GuildMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/guild")
@RequiredArgsConstructor
public class GuildMemberController {

    private final GuildMemberService service;

    // 전체 길드 멤버 조회
    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page) {  // 0-based page index
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지 10명
        Page<GuildMemberDto> memberPage = service.getAll(pageable);

        model.addAttribute("memberPage", memberPage);
        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());

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


    // ===== 디스코드 메시지 분류 기능 =====

    // 디스코드 메시지 입력 폼 페이지
    @GetMapping("/discord-messages")
    public String discordMessagesForm() {
        return "guild/discord-messages";
    }

    // 디스코드 메시지를 사용자별로 분류하여 표시
    @PostMapping("/discord-messages")
    public String showDiscordMessages(@RequestParam("discordText") String discordText, Model model) {
        try {
            Map<String, List<String>> userMessages = DiscordMessageService.parseDiscordMessagesByUser(discordText);
            model.addAttribute("userMessages", userMessages);
            model.addAttribute("totalUsers", userMessages.size());
            model.addAttribute("discordText", discordText);
            return "guild/discord-messages-result";
        } catch (Exception e) {
            model.addAttribute("error", "메시지 파싱 중 오류가 발생했습니다: " + e.getMessage());
            return "guild/discord-messages";
        }
    }
}
