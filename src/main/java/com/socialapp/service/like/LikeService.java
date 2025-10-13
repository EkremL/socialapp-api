package com.socialapp.service.like;

public interface LikeService {
    //!Postu beğenme ve beğeniyi geri alma işlemlerini tanımladım. (User ve postla ilişkili bir şekilde)
    void likePost(String authHeader,Long postId);
    void unLikePost(String authHeader,Long postId);
}
