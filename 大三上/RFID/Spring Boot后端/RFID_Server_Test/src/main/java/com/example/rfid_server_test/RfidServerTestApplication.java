package com.example.rfid_server_test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class RfidServerTestApplication {


    public static void main(String[] args) {
        SpringApplication.run(RfidServerTestApplication.class, args);

    }


}
