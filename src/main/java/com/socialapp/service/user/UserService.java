package com.socialapp.service.user;

import com.socialapp.dto.auth.PasswordChangeDto;
import com.socialapp.dto.auth.UserResponseDto;

public interface UserService {

    //!UserId'ye göre /me endpointi için profil bilgisi dööndürüyor.
    UserResponseDto getUserById(Long id, String authHeader);
    //!Şifre değiştirme (şifre doğrulamayı ve validasyonu sağladım)
    void changePassword(String authHeader, PasswordChangeDto pwDto);
    //! Mevcut kullanıcı, kendi hesabını silebilir. (adminin useri sildiği seneryo, AdminService içerisinde)
    void deleteMyAccount(String authHeader);

}
