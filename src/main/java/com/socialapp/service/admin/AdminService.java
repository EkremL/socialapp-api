package com.socialapp.service.admin;


public interface AdminService {
    //!Admin tarafından user silme işlemi (implementasyonu adminServiceImpl içinde tanımladım.
    void deleteUserByAdmin(Long id,String authHeader);
}
