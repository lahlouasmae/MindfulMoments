package com.example.demo.controller;

import com.example.demo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email et mot de passe sont requis");
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email déjà existant");
        }

        user.setStressValues(new ArrayList<>()); // Initialiser la liste des niveaux de stress
        User savedUser = userService.registerUser(user);
        savedUser.setPassword(null);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        if ("admin@gmail.com".equals(user.getEmail()) && "admin".equals(user.getPassword())) {
            User admin = new User();
            admin.setId(0L); // ID spécial pour admin
            admin.setEmail("admin@gmail.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setHistoriques(new ArrayList<>()); // Initialiser la liste d'historiques
            return ResponseEntity.ok(admin);
        }

        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword())) {
            User loggedInUser = existingUser.get();
            loggedInUser.setPassword(null);
            loggedInUser.setHistoriques(null); // Ne pas exposer les historiques dans la réponse de login
            return ResponseEntity.ok(loggedInUser);
        }

        return ResponseEntity.status(401).body("Identifiants incorrects");
    }

    @PostMapping("/{userId}/stress")
    public ResponseEntity<?> addStressLevel(@PathVariable Long userId, @RequestParam int stressValue) {
        try {
            userService.addStressLevel(userId, stressValue);
            return ResponseEntity.ok("Niveau de stress ajouté avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }
    }

    @GetMapping("/{userId}/stress")
    public ResponseEntity<?> getStressLevels(@PathVariable Long userId) {
        try {
            List<Integer> stressLevels = userService.getStressLevels(userId);
            return ResponseEntity.ok(stressLevels);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }
    }

   /* @GetMapping("/{userId}/stress/average")
    public ResponseEntity<?> getAverageStressLevel(@PathVariable Long userId) {
        try {
            double average = userService.calculateAverageStressLevel(userId);
            return ResponseEntity.ok("Moyenne des niveaux de stress : " + average);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }
    }*/

    @GetMapping("/{userId}/stress/data")
    public ResponseEntity<?> getStressData(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            return ResponseEntity.ok(user.getStressValues());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            user.setPassword(null);
            user.setHistoriques(null); // Ne pas exposer les historiques
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestPart("user") String userJson,
            @RequestPart(value = "file", required = false) MultipartFile file  // Changé de 'image' à 'file'
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User updatedUser = mapper.readValue(userJson, User.class);
            User updated = userService.updateProfile(userId, updatedUser, file);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("Utilisateur supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<User> normalUsers = users.stream()
                .filter(user -> !"admin@gmail.com".equals(user.getEmail()))
                .peek(user -> {
                    user.setPassword(null);
                    user.setHistoriques(null);
                })
                .toList();

        if (normalUsers.isEmpty()) {
            return ResponseEntity.ok("Aucun utilisateur trouvé");
        }

        return ResponseEntity.ok(normalUsers);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalUsers() {
        try {
            Long userCount = userService.getTotalUsers();
            return ResponseEntity.ok(userCount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
