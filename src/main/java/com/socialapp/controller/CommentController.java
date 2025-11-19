package com.socialapp.controller;

import com.socialapp.dto.auth.UserDto;
import com.socialapp.dto.comment.CommentCreateDto;
import com.socialapp.dto.comment.CommentResponseDto;
import com.socialapp.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    //!Sırasıyla comment ekleme, posta ait commentleri görüntüleme ve comment silme endpointlerini oluşturdum ve business-logic'i commentService dosyasında oluşturdum.
    private final CommentService commentService;

    //!Add Comment
    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommentResponseDto> addComment(@RequestHeader("Authorization")String authHeader, @PathVariable Long id, @Valid @RequestBody CommentCreateDto commentCreateDto){

        CommentResponseDto createdComment = commentService.addComment(authHeader,id,commentCreateDto); //? id postId ye karşılık geliyor. Comment, postun içerisinde oluşuyor.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
    //!Get all comments in the post
    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<CommentResponseDto>> listAllCommentsInPost(@PathVariable Long id,  @RequestHeader("Authorization") String authHeader){
        List<CommentResponseDto> comments = commentService.listAllCommentsInPost(id,authHeader);
        return ResponseEntity.ok(comments);
    }
    //!Delete comment
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<String> deleteComment(@RequestHeader("Authorization")String authHeader,@PathVariable Long id){
        commentService.deleteComment(authHeader,id);
        return ResponseEntity.status(HttpStatus.OK).body("Comment successfully deleted!");
    }

    //!Get deleted comments (only for admins after soft delete)
    @GetMapping("/comments/deleted")
    public List<CommentResponseDto> getDeletedComments(@RequestHeader("Authorization") String authHeader){
        return commentService.getDeletedComments(authHeader);
    }
    //!Get deleted comment by id (only for admins after soft delete)
    @GetMapping("/comments/deleted/{id}")
    public CommentResponseDto getDeletedCommentById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id){
        return commentService.getDeletedCommentById(authHeader, id);
    }
    //!Restore deleted comment by id (only for admins after soft delete)
    @PutMapping("/comments/{id}/restore")
    public CommentResponseDto restoreComment(@RequestHeader("Authorization") String authHeader,
                               @PathVariable Long id){
        return commentService.restoreComment(authHeader, id);
    }
}
