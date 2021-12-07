package com.example.rfid_server_test.dao;

import com.example.rfid_server_test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByStudentCardId(String cardId);

}
