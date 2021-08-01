package com.example.project_exam.services;

import com.example.project_exam.models.User;
import com.example.project_exam.respository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public AbService<User, UserRepo> getService() {
        return new AbService<>(this.userRepo);
    }

    public boolean save(User user) {
        try {
            User saveUser = new User();
            saveUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            saveUser.setId_number(user.getId_number());
            saveUser.setName(user.getName());
            saveUser.setEmail(user.getEmail());
            saveUser.setAddress(user.getAddress());
            saveUser.setPhone_number(user.getPhone_number());
            saveUser.setEmoji(user.getEmoji());

            userRepo.save(saveUser);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void deleteUser(Long id){
        userRepo.deleteById(id);
    }
}
