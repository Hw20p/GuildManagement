package com.example.royal.controller;

import com.example.royal.model.Post;
import com.example.royal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 목록 조회
    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "board/list";
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPost(id));
        return "board/view";
    }

    // 게시글 작성 폼
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("post", new Post());
        return "board/new";
    }

    // 게시글 저장
    @PostMapping
    public String create(@ModelAttribute Post post) {
        postService.save(post);
        return "redirect:/board";
    }

    // 게시글 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPost(id));
        return "board/edit";
    }

    // 게시글 수정 처리
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Post updatedPost) {
        postService.update(id, updatedPost);
        return "redirect:/board/" + id;
    }

    // 게시글 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/board";
    }
}
