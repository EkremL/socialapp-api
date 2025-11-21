package com.socialapp.repository;

import com.socialapp.model.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository  extends JpaRepository<Post,Long> {
    //! Tüm postları kullanıcı ve commentleriyle birlikte çağırmak için kullandım.
    @EntityGraph(attributePaths = {"user","comments","comments.user"})
    List<Post> findAll();

    //! Tek bir postu user bilgisiyle birlikte getiriyor.
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findById(Long id);

    //*Alttaki 2 methodu soft delete mantığında adminler için ekledik. Adminler silinen postları görüntüleyebilir ve dilerse restore ederek normal hale getirebilir!
    //!Soft deleteden dolayı silinenleri listelemek için ekliyoruz
    @Query(value = "SELECT * FROM post WHERE is_deleted = true", nativeQuery = true) //*Query -> Custom SQL, nativequery -> JPQL değil, direkt SQL
    List<Post> findDeletedPosts();
    //!Silinen postu geri getiriyoruz (restore) (service içinde kullanılıyor)
    @Query(value = "SELECT * FROM post WHERE id = :id", nativeQuery = true) //! : -> SQL’de parameter binding formatıdır.
    Optional<Post> findEvenIfDeleted(@Param("id") Long id); //!Spring’e "bu parametre SQL’deki :id’dir" demek
    //!// Kullanıcının tüm postları (silinmiş dahil)
    // NEDEN? User silinince/restore edilince, o user’a ait TÜM postları topluca işlemek için.
    @Query(value = "SELECT * FROM post WHERE user_id = :userId", nativeQuery = true)
    List<Post> findEvenIfDeletedByUserId(@Param("userId") Long userId);

    List<Post> findAllByUserId(Long userId);
}

