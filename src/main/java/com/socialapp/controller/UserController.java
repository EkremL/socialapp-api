package com.socialapp.controller;

import com.socialapp.dto.auth.PasswordChangeDto;
import com.socialapp.dto.auth.UserDto;
import com.socialapp.dto.auth.UserResponseDto;
import com.socialapp.dto.post.PostResponseDto;
import com.socialapp.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    //!Sırasıyla tekli kullanıcıyı getirme, kullanıcının şifresini güncellemesi ve user silme(yalnızca user ve admin tarafından silinebilme özelliği)
    //!endpointlerini oluşturdum ve business-logic'i userService dosyasında oluşturdum.
    private final UserService userService;


    //!Get single user
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){

        return ResponseEntity.ok(userService.getUserById(id,authHeader));
    }
    //! Change password
    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody PasswordChangeDto pwDto) {
        userService.changePassword(authHeader,pwDto);
        return ResponseEntity.ok("Password updated successfully!");
    }
    //! Delete account
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(@RequestHeader("Authorization") String authHeader){
        userService.deleteMyAccount(authHeader);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Account has deleted successfully!");
    }

    //!Get deleted users (only for admins after soft delete)
    @GetMapping("/deleted")
    public List<UserDto> getDeletedUsers(@RequestHeader("Authorization") String authHeader){
        return userService.getDeletedUsers(authHeader);
    }
    //!Get deleted user by id (only for admins after soft delete)
    @GetMapping("/deleted/{id}")
    public UserDto getDeletedUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id){
        return userService.getDeletedUserById(authHeader, id);
    }
    //!Restore deleted user by id (only for admins after soft delete)
    @PutMapping("/{id}/restore")
    public UserDto restoreUser(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable Long id){
        return userService.restoreUser(authHeader, id);
    }

}
