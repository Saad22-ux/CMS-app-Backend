package com.elasri.cmsapp.controller;

import com.elasri.cmsapp.model.User;
import com.elasri.cmsapp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<User> all() throws Exception {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User one(@PathVariable int id) throws Exception {
        return userService.getUserById(id);
    }

    @PostMapping
    public void create(@RequestBody User user) throws Exception {
        userService.addUser(user);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable int id,
                          @RequestBody User user) throws Exception {
        user.setId(id);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) throws Exception {
        return userService.deleteUser(id);
    }

    @GetMapping("/html")
    public String html() throws Exception {
        return userService.generateUsersHtml();
    }

    @GetMapping("/html/{id}")
    public String htmlOne(@PathVariable int id) throws Exception {
        return userService.generateUserHtml(id);
    }
}
