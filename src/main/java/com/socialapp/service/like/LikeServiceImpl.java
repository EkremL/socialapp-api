package com.socialapp.service.like;

import com.socialapp.exception.BadRequestException;
import com.socialapp.exception.NotFoundException;
import com.socialapp.model.LikePost;
import com.socialapp.model.Post;
import com.socialapp.model.User;
import com.socialapp.repository.LikePostRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final CurrentUserProvider currentUserProvider;
    private final LikePostRepository likePostRepository;
    private final PostRepository postRepository;

    //? Post üzerindeki like sayısını senkron eden sub method.
    private void implementLikeCount(Post post){

        //!Toplam like sayısını LikePost tablosundan alıyorum.
        long likeCount = likePostRepository.countByPostId(post.getId());

        //! Post tablosunun like alanını güncelliyorum ve güncellenen postu tekrardan kayıt ediyorum.
        post.setLikeCount((int)likeCount);
        postRepository.save(post);
    }

    //!Like post
    @Override
    public void likePost(String authHeader,Long postId){
        //!Token üzerinden aktif kullanıcıyı, ardından ilgili postu buluyorum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
//        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found!"));

        //!Eğer post daha önce beğenildiyse tekrardan beğenilmemesini sağlıyorum.
        if(likePostRepository.existsByPostIdAndUserId(postId, currentUser.getId())){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Post has already liked!");
            throw new BadRequestException("Post has already liked!");
        }
        //! YEni LikePost kaydı oluşturuyorum (user-post relation) ve like countu  yardımcı method ile güncelliyorum.
        likePostRepository.save(LikePost.builder().post(post).user(currentUser).build());
        implementLikeCount(post);
    }

    //! Unlike post
    @Override
    public void unLikePost(String authHeader,Long postId){
        //!Aynı şekilde user ve postu buluyorum.
        User currentUser = currentUserProvider.getCurrentUser(authHeader);
//        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found!"));

        //!Bu post için yapılan like'ı arıyorum ve beğeniyi kaldırıp tekrardan güncelliyorum.
//        LikePost like = likePostRepository.findByPostIdAndUserId(postId,currentUser.getId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Like not found!"));
        LikePost like = likePostRepository.findByPostIdAndUserId(postId,currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Like not found!"));

        likePostRepository.delete(like);
        implementLikeCount(post);
    }
}
