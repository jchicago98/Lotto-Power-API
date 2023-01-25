package com.example.lottopower.controllers;

import com.example.lottopower.config.TokenGenerator;
import com.example.lottopower.models.Users;
import com.example.lottopower.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("user")
public class UserController {

    private UserService userService;

    private static final String ROLE_USER = "ROLE_USER";

    public UserController(UserService userService){
        this.userService = userService;
    }


    @PostMapping("register-user")
    public ResponseEntity registerUser(@RequestBody Users users){
        users.setRoles(ROLE_USER);
        Users user = this.userService.registerUser(users);

        HttpStatus status;
        ResponseEntity response;
        if(user != null){
            status = HttpStatus.OK;
            response = new ResponseEntity(user,status);
        }else{
            status  = HttpStatus.BAD_REQUEST;
            response = new ResponseEntity("USER NOT ADDED",status);
        }
        return response;
    }

    @PostMapping("login")
    public ResponseEntity<String> checkLoginCredentials(@RequestBody Users user) {
        String [] roles = {"ROLE_USER"};
        String token = TokenGenerator.generateToken(user.getEmailAddress(), roles);
        try {
            if (userService.checkLoginCredentials(user.getEmailAddress(), user.getPassword())) {
                userService.updateAccessTokenForUserLoggedIn(user.getEmailAddress(),token);
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Incorrect Email or Password", HttpStatus.UNAUTHORIZED);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all")
    public ResponseEntity<List<Users>> getAllUsers(@RequestBody Users userWithAccessToken){
        List<Users> allUsers = new ArrayList<>();
        try {
            if (userService.isAuthenticated(userWithAccessToken)) {
                allUsers = this.userService.getAllUsers();
                return new ResponseEntity<>(allUsers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(allUsers, HttpStatus.UNAUTHORIZED);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(allUsers, HttpStatus.BAD_REQUEST);
        }
    }



}
