package com.elasri.cmsapp.controller;

import com.elasri.cmsapp.model.User;
import com.elasri.cmsapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.addUser(user); // Récupère le user avec le bon ID
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
        try {
            // Assure que l'ID vient du path et non du body
            user.setId(id);
            boolean updated = userService.updateUser(user);
            if (!updated) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Retourne le message exact pour debug
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) throws Exception {
        return userService.deleteUser(id);
    }

//    @GetMapping("/html")
//    public String html() throws Exception {
//        return userService.generateUsersHtml();
//    }

//    @GetMapping("/html/{id}")
//    public String htmlOne(@PathVariable int id) throws Exception {
//        return userService.generateUserHtml(id);
//    }
}
