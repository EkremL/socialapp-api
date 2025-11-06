package com.socialapp.service.comment;

import com.socialapp.dto.comment.CommentCreateDto;
import com.socialapp.dto.comment.CommentResponseDto;

import java.util.List;

public interface CommentService {
    //!PostId ve User token ile ilişkili bir şekilde yeni yorum eklemeyi sağladım.
    CommentResponseDto addComment(String authHeader, Long postId, CommentCreateDto commentCreateDto);

    //!Belirli postun altındaki bütün yorumları fetch ederek listeledim.
    List<CommentResponseDto> listAllCommentsInPost(Long postId,String authHeader);

    //!Yorum silme işlemi (authHeader sayesinde korumalı endpoint haline geldi yani yorum sahibi, post sahibi veya admin tarafından yapılabilir.)
    void deleteComment(String authHeader, Long commentId);
}
