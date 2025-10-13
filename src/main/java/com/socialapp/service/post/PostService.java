package com.socialapp.service.post;

import com.socialapp.dto.post.PostCreateDto;
import com.socialapp.dto.post.PostResponseDto;
import com.socialapp.dto.post.PostUpdateDto;

import java.util.List;


public interface PostService {
    //!Post oluşturma (user token ve post oluşturma veri modeli ile)
    PostResponseDto createPost(String authHeader, PostCreateDto postCreateDto);
    //! PostId ye göre tekli post fetch işlemi
    PostResponseDto getPostById(Long id);
    //! Tüm postları listeleme
    List<PostResponseDto> getAllPosts();
    //! Post güncelleme (yalnızca post sahibi veya admin tarafından)
    PostResponseDto updatePost(String authHeader, PostUpdateDto postUpdateDto, Long id);
    //! Postu silme (yalnızca post sahibi veya admin tarafından)
    void deletePost(String authHeader,Long id);
    //! view Count arttırma
    void incrementViewCount(Long id , String authHeader); //?authHeaderi sonradan ekledim tekrar tekrar kendi postuna bakınca view arttırmayı engellemek için
}
