package com.socialapp.config;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.socialapp.model.Role;
import com.socialapp.model.User;
import com.socialapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    //! Lombok sayesinde @RequiredArgsConstructor kullanarak constructor DI yerine daha kısa yolla sağlayıp kod tekrarını kısalttım.
    private final UserRepository userRepository;
    //? Casede belirtildiği üzere, server ilk defa ayağa kaldırıldığında admin oluşturuluyor, eğer admin varsa sonraki çalıştırıldığında exists bilgisi veriliyor.
    //? Fakat anladığım kadarıyla bu şekilde yaptım,yani veritabanında bir admin oluşuyor fakat adminin token alması için login endpointine username ve şifresini yazması gerekmektedir.
    @Bean
    public CommandLineRunner createAdminUser(){
        return  args -> {
            if(!userRepository.existsByUsername("admin")){
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(
                        BCrypt.withDefaults().hashToString(12,"Admin123*".toCharArray())
                );
                admin.setRole(Role.ADMIN);
                admin.setEmail("admin@admin.com");
                userRepository.save(admin);
                System.out.println("Admin is successfully created.");
            }else {
                System.out.println("Admin already exists.");
            }
        };
    }
}
