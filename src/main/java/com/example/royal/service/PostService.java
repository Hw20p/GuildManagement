package com.example.royal.service;

import com.example.royal.model.Post;
import com.example.royal.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow();
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public void update(Long id, Post updatedPost) {
        Post post = getPost(id);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setAuthor(updatedPost.getAuthor());
        postRepository.save(post);
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}
