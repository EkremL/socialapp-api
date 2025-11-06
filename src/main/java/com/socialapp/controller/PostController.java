package com.socialapp.controller;

import com.socialapp.dto.post.PostCreateDto;
import com.socialapp.dto.post.PostResponseDto;
import com.socialapp.dto.post.PostUpdateDto;
import com.socialapp.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    //!Sırasıyla post ekleme, tekli post getirme , bütün postları getirme, post güncelleme,
    //! post silme(yalnızca user ve admin tarafından silinebilme özelliği) ve post görüntülenme arttırma endpointlerini oluşturdum ve business-logic'i postService dosyasında oluşturdum.

    private final PostService postService;

    //!Create post
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody PostCreateDto postCreateDto){

        PostResponseDto createdPost = postService.createPost(authHeader,postCreateDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    //!Get single post
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){

        PostResponseDto post = postService.getPostById(id,authHeader);

        return ResponseEntity.status(HttpStatus.OK).body(post);
    }
    //!Get all posts
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts(@RequestHeader("Authorization") String authHeader){

        List<PostResponseDto> posts = postService.getAllPosts(authHeader);

        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
    //!Update post
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody PostUpdateDto postUpdateDto, @PathVariable Long id){

        PostResponseDto updatedPost = postService.updatePost(authHeader,postUpdateDto,id);

        return ResponseEntity.status(HttpStatus.OK).body(updatedPost);
    }
    //!Delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){

       postService.deletePost(authHeader,id);

       return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post successfully deleted!");
    }
    //!Increment view count
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){

        postService.incrementViewCount(id, authHeader);

        return ResponseEntity.ok().build();
    }
}
