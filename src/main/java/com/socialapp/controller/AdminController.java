package com.socialapp.controller;

import com.socialapp.model.User;
import com.socialapp.service.admin.AdminService;
import com.socialapp.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    //!Case'de belirtilen; adminin useri silebilmesi için gerekli endpointi bu controllerde tanımladım, business logic kısmı ise adminService dosyasında mevcut.
    //!Ayrıca authorization için currenUserProvider (util klasöründe) kullanarak mevcut kullanıcının admin mi user mi olduğunu kontrol edilmesini sağladım.
    private  final AdminService adminService;
    private  final CurrentUserProvider currentUserProvider;

    //!Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){

        User currentUser = currentUserProvider.getCurrentUser(authHeader);

        if(!currentUserProvider.isAdmin(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete anyone!");

        adminService.deleteUserByAdmin(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully!");
    }
}
