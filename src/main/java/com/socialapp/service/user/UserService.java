package com.socialapp.service.user;

import com.socialapp.dto.auth.PasswordChangeDto;
import com.socialapp.dto.auth.UserDto;
import com.socialapp.dto.auth.UserResponseDto;

import java.util.List;

public interface UserService {

    //!UserId'ye göre /me endpointi için profil bilgisi dööndürüyor.
    UserResponseDto getUserById(Long id, String authHeader);
    //!Şifre değiştirme (şifre doğrulamayı ve validasyonu sağladım)
    void changePassword(String authHeader, PasswordChangeDto pwDto);
    //! Mevcut kullanıcı, kendi hesabını silebilir. (adminin useri sildiği seneryo, AdminService içerisinde)
    void deleteMyAccount(String authHeader);
    //! Silinen useri geri getirir (sadece adminler için, ayrıca sadece user degil, postları, commentleri, likeları da gelir (soft delete mantıgı))
    UserDto restoreUser(String authHeader, Long id);
    List<UserDto> getDeletedUsers(String authHeader);
    UserDto getDeletedUserById(String authHeader, Long userId);
}
