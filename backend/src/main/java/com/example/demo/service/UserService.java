package com.example.demo.service;

import com.example.demo.model.Historique;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.HistoriqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoriqueRepository historiqueRepository;

    public void addStressLevel(Long userId, int stressValue) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.addStressLevel(stressValue); // Ajouter un nouveau niveau de stress
        userRepository.save(user);
    }

    public List<Integer> getStressLevels(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return user.getStressValues();
    }

    public double calculateAverageStressLevel(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return user.calculateAverageStressLevel();
    }

    // Méthodes existantes
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    // Nouvelles méthodes pour la gestion du profil
    public User updateProfile(Long userId, User updatedUser, MultipartFile imageFile) throws IOException {
        return userRepository.findById(userId)
                .map(user -> {
                    // Mise à jour des champs du profil
                    if (updatedUser.getFirstName() != null) {
                        user.setFirstName(updatedUser.getFirstName());
                    }
                    if (updatedUser.getLastName() != null) {
                        user.setLastName(updatedUser.getLastName());
                    }
                    if (updatedUser.getPhoneNumber() != null) {
                        user.setPhoneNumber(updatedUser.getPhoneNumber());
                    }
                    if (updatedUser.getAddress() != null) {
                        user.setAddress(updatedUser.getAddress());
                    }
                    if (updatedUser.getEmail() != null) {
                        user.setEmail(updatedUser.getEmail());
                    }

                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(updatedUser.getPassword());
                    }

                    // Gestion de l'image
                    if (imageFile != null && !imageFile.isEmpty()) {
                        String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                        String uploadDir = "uploads/users/";
                        Path uploadPath = Paths.get(uploadDir);

                        // Crée le répertoire si nécessaire
                        if (!Files.exists(uploadPath)) {
                            try {
                                Files.createDirectories(uploadPath);
                            } catch (IOException e) {
                                throw new RuntimeException("Impossible de créer le répertoire pour l'upload des fichiers", e);
                            }
                        }

                        Path filePath = uploadPath.resolve(userId + "_" + fileName);
                        try {
                            Files.write(filePath, imageFile.getBytes());
                            user.setImageUrl("/uploads/users/" + userId + "_" + fileName);
                        } catch (IOException e) {
                            throw new RuntimeException("Erreur lors de l'enregistrement de l'image", e);
                        }
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer et supprimer tous les historiques de l'utilisateur
        List<Historique> historiques = historiqueRepository.findByUser_Id(id);
        for (Historique historique : historiques) {
            historique.getExercise().getHistoriques().remove(historique);
            historique.setExercise(null);
            historique.setUser(null);
        }
        historiqueRepository.deleteAll(historiques);

        // Supprimer l'utilisateur
        userRepository.delete(user);
    }
    public Long getTotalUsers() {
        return userRepository.count();
    }
}
