package com.socialapp.controller;

import com.socialapp.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
    //!Sırasıyla like ve unlike endpointlerini oluşturdum ve business-logic'i likeService dosyasında oluşturdum.
    private final LikeService likeService;

    //!LIKE
    @PostMapping("/posts/{id}/likes")
    public ResponseEntity<String> likePost(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        likeService.likePost(authHeader,id);
        return ResponseEntity.ok("Post successfully liked!");
    }
    //!UNLIKE
    @DeleteMapping("/posts/{id}/likes")
    public ResponseEntity<String> unLikePost(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        likeService.unLikePost(authHeader,id);
        return ResponseEntity.ok("Post successfully unliked!");
    }
}
