package com.example.rfid_server_test.controller;

import com.example.rfid_server_test.dao.UserDao;
import com.example.rfid_server_test.entity.User;
import com.example.rfid_server_test.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDao dao;

//    @RequestMapping("/getAllUser")
//    public String findAll(){
////        List<User> list = new ArrayList<User>();
////        list = userRepository.findAll();
////        return list;
//        return dao.getAllUser();
//    }
    @RequestMapping("/getAllUser")
    public List<User> findAll(){
        List<User> list = new ArrayList<User>();
        list = userRepository.findAll();
        return list;
    }
    @RequestMapping("/getByCardId")
    public User getByUserName(String cardId){
        User user = userRepository.findUserByStudentCardId(cardId);
        return user;
    }
    @PostMapping("/addCardSignle")
    public String addCard(@RequestParam("studentCardId")String cardId){
        Random r = new Random();
        String parm1,parm2;
        parm1=""+r.nextLong();
        parm2=""+r.nextLong();
        User user = new User(parm1,parm2,cardId);
        dao.insert(user);
        //userRepository.save(user);
        return "success！！";
    }
    @PostMapping("/addCardAll")
    public String addCardAll(@RequestParam("studentId")String studentId,
                             @RequestParam("studentName")String studentName,
                             @RequestParam("studentCardId")String cardId){
        User user = new User(studentId,studentName,cardId);
        dao.insert(user);
        //userRepository.save(user);
        return "success！！";
    }


}
