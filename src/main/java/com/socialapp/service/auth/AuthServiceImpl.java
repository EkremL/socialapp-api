package com.socialapp.service.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.socialapp.exception.BadRequestException;
import com.socialapp.exception.NotFoundException;
import com.socialapp.exception.UnAuthorizedException;
import com.socialapp.util.CurrentUserProvider;
import com.socialapp.util.JwtConfig;
import com.socialapp.dto.auth.LoginRequestDto;
import com.socialapp.dto.auth.LoginResponseDto;
import com.socialapp.dto.auth.UserDto;
import com.socialapp.model.Role;
import com.socialapp.model.Token;
import com.socialapp.model.User;
import com.socialapp.repository.TokenRepository;
import com.socialapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;
    private final JwtConfig jwtConfig;
    private final CurrentUserProvider currentUserProvider;
    //* SIGNUP LOGIC
    @Override
    public void signUp(UserDto userDto){
        //!User repositoryde tanımlanan existsByUsername ile user daha önce kayıtlı mı kontrol ediyoruz.
        if(userRepository.existsByUsername(userDto.getUsername())){
//            throw new RuntimeException("Username already exists!");
            throw new BadRequestException("Username already exists!");}
        //!Aynı kontrolü sonradan eklediğim email için de yapıyorum.
        if(userRepository.existsByEmail(userDto.getEmail())){
//            throw new RuntimeException("Email is already taken!");
            throw new BadRequestException("Email is already taken!");}

        //!ModelMapper sayesinde new instance oluşturmak yerine direkt User'a dönüştürürerek clean kod prensibini benimsedim.
        User user = modelMapper.map(userDto, User.class);

        //!Spring Security kullanmadan şifreyi manual olarak hashledim.(Aynı methodu admin initializerde de kullandım.)
        String hashedPw = BCrypt.withDefaults().hashToString(12,userDto.getPassword().toCharArray());
        user.setPassword(hashedPw);

        //!Register olan User'ların rolünü default olarak USER olarak atadım ve databaseye kaydettim.
        user.setRole(Role.USER);

        userRepository.save(user);

    }

    //* LOGIN LOGIC
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        //! Kullanıcıyı veritabanından bulup hata varsa kullanıcı bulunamadı hatası sağladım.
//        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found!"));
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(() ->  new NotFoundException("User not found!"));

        //! Şifreler birbirini sağlıyor mu kontrol ettikten sonra. İlk parametrede girilen password, 2. parametrede ise kullanıcının kayıtlı parolası karşılaştırarak kontrol ettim.
        BCrypt.Result result = BCrypt.verifyer().verify(loginRequestDto.getPassword().toCharArray(),user.getPassword());

        //!Şifreler eşleşmiyorsa hata
        if (!result.verified){
//            throw new RuntimeException("Invalid credentials!");
            throw new BadRequestException("Invalid credentials!");
        }

        //!Login işleminde Jwt token oluşturmak ve DB'ye kaydetme işlemlerini tanımladım.
        //! Çünkü jwtconfig dosyasında sadece string token üretiliyor, dbye kaydedilmiyor fakat burada loginden sonra token üretilip db'ye kaydediliyor.
        Map<String,Object> claims = new HashMap<>();
        //! Tokeni oluşturmak için User rolünü claim olarak ekledim.
        claims.put("role",user.getRole().name());

        String jwt = jwtConfig.generateToken(user.getUsername(),claims);
        Date expirationDate = jwtConfig.extractExpiration(jwt);
        String expiresAt = expirationDate.toString();

        //!Save işlemleri
        Token token = new Token();
        token.setToken(jwt);
        token.setUser(user);
        token.setUsername(user.getUsername());
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);

        return new LoginResponseDto(jwt,expiresAt);
    }
    //*LOGOUT LOGIC
    @Override
    public void logout(String authHeader){
        if(authHeader == null || !authHeader.startsWith(("Bearer "))){
//            throw new RuntimeException("Token not found in header");
            throw new UnAuthorizedException("Authorization header is missing or invalid!");
        }

        String token = authHeader.substring(7);
        //!Aynı işlemin benzerini CurrentUserProvider yardımcı classında da kullandım. Amaç tokenin başındaki "Bearer " kısmını temizlemek ve yalnızca tokeni almak.

        //!Token veritabanında bulunup pasif hale getiriliyor.
//        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token not found in database!"));
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new UnAuthorizedException("Token not found or invalid!"));

        savedToken.setExpired(true);
        savedToken.setRevoked(true);

        tokenRepository.save(savedToken);
    }

    //* ME (GET CURRENT PROFILE PAGE) LOGIC
    @Override
    public UserDto getCurrentUser(String authHeader){
        //!Headerdeki mevcut kullanıcıyı currentUserProvider sayesinde aldım.
        User user = currentUserProvider.getCurrentUser(authHeader);
        //!ModelMapper ile User - UserDto dönüşümü sağladım.
//        UserDto userDto = modelMapper.map(user, UserDto.class);
//
//        return userDto;

        return modelMapper.map(user, UserDto.class);
    }

}
