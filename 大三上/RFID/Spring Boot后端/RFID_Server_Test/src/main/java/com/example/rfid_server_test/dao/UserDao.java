package com.example.rfid_server_test.dao;

import com.example.rfid_server_test.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(User user){
        jdbcTemplate.update("insert into information(student_id,student_name,student_card_id) values (?,?,?)",
                user.getStudentId(),
                user.getStudentName(),
                user.getStudentCardId());
    }

}
