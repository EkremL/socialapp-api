package com.socialapp.controller;

import com.socialapp.dto.auth.LoginRequestDto;
import com.socialapp.dto.auth.LoginResponseDto;
import com.socialapp.dto.auth.UserDto;
import com.socialapp.service.auth.AuthService;
import com.socialapp.util.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //!Sırasıyla register, login , logout ve me endpointleri ile belirtilen bütün auth endpointlerini oluşturdum ve katmanlı mimariden ötürü business-logic'i authService dosyasında oluşturdum.


    //!REGISTER
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserDto userDto){
        authService.signUp(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }
    //!LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request){
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    //!LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader ("Authorization") String authHeader){
        authService.logout(authHeader);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //!ME (mevcut kullanıcının kendi profil sayfasını görüntüleyen endpoint (sonradan e-mail ekledim ki profil sayfası fazla boş kalmasın istedim.)
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(authService.getCurrentUser(authHeader));
    }


}
