package com.example.project_exam.respository;

import com.example.project_exam.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends PagingAndSortingRepository<User,Long> {
    User findByEmail(String email);
}
