package com.example.demo.service;

import com.example.demo.model.Exercise;
import com.example.demo.model.Historique;
import com.example.demo.model.User;
import com.example.demo.repository.ExerciseRepository;
import com.example.demo.repository.HistoriqueRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private HistoriqueRepository historiqueRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public List<Exercise> getCompletedExercisesForUser(Long userId) {
        return historiqueRepository.findByUser_Id(userId)
                .stream()
                .map(historique -> {
                    Exercise exercise = historique.getExercise();
                    if (exercise != null) {
                        exercise.setCompleted(true);
                    }
                    return exercise;
                })
                .filter(exercise -> exercise != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public Exercise toggleExerciseCompletion(Long exerciseId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec ID : " + userId));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercice introuvable avec ID : " + exerciseId));

        Optional<Historique> historiqueOptional = historiqueRepository.findByUser_IdAndExercise_Id(userId, exerciseId);

        if (historiqueOptional.isPresent()) {
            // Supprime l'historique si l'exercice était complété
            historiqueRepository.delete(historiqueOptional.get());
            exercise.setCompleted(false);
        } else {
            // Crée un nouvel historique si l'exercice n'était pas complété
            Historique historique = new Historique();
            historique.setUser(user);
            historique.setExercise(exercise);
            historique.setName(exercise.getName());
            historique.setType(exercise.getType());
            historique.setMaxStressLevel(exercise.getMaxStressLevel());
            historique.setMinStressLevel(exercise.getMinStressLevel());
            historique.setCompleted(true);
            historiqueRepository.save(historique);

            exercise.setCompleted(true);
        }

        return exercise;
    }
    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }
    /**
     * Met à jour un exercice existant.
     *
     * @param id       ID de l'exercice.
     * @param exercise Données mises à jour.
     * @return Exercice mis à jour.
     */
    @Transactional
    public Exercise updateExercise(Long id, Exercise exercise) {
        Exercise existingExercise = getExerciseById(id);

        if (existingExercise == null) {
            throw new IllegalArgumentException("Exercice non trouvé avec l'ID : " + id);
        }

        // Update all fields of the exercise
        existingExercise.setName(exercise.getName());
        existingExercise.setType(exercise.getType());
        existingExercise.setMinStressLevel(exercise.getMinStressLevel());
        existingExercise.setMaxStressLevel(exercise.getMaxStressLevel());
        existingExercise.setDuration(exercise.getDuration());
        existingExercise.setCalories(exercise.getCalories());
        existingExercise.setDifficulty(exercise.getDifficulty());
        existingExercise.setDescription(exercise.getDescription());
        existingExercise.setInstructions(exercise.getInstructions());
        existingExercise.setBenefits(exercise.getBenefits());

        // Update image URL and video URL if they are provided
        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            existingExercise.setImageUrl(exercise.getImageUrl());
        }

        if (exercise.getVideoUrl() != null && !exercise.getVideoUrl().isEmpty()) {
            existingExercise.setVideoUrl(exercise.getVideoUrl());
        }

        // Save updated exercise back to the repository
        return exerciseRepository.save(existingExercise);
    }
    /**
     * Supprime un exercice par son ID.
     *
     * @param id ID de l'exercice.
     */
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new RuntimeException("Exercice non trouvé avec l'ID : " + id);
        }
        exerciseRepository.deleteById(id);
    }




    /**
     * Récupère un exercice par son ID.
     *
     * @param id ID de l'exercice.
     * @return Exercice correspondant.
     */

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id).orElse(null);
    }

    public List<Exercise> getExercisesByStressLevel(int stressLevel) {
        return exerciseRepository.findByStressLevel(stressLevel);
    }
    public Exercise uploadImageToExercise(Long id, MultipartFile file) throws IOException {
        Exercise exercise = exerciseRepository.findById(id).orElse(null);
        if (exercise != null) {
            // Sauvegarder l'image et obtenir le chemin ou l'URL
            String imageUrl = saveImage(file);

            // Mettre à jour l'exercice avec l'URL de l'image
            exercise.setImageUrl(imageUrl);
            return exerciseRepository.save(exercise);
        }
        return null;
    }
    public Exercise uploadVideoToExercise(Long id, MultipartFile file) throws IOException {
        Exercise exercise = exerciseRepository.findById(id).orElse(null);
        if (exercise != null) {
            // Sauvegarder la vidéo et obtenir le chemin ou l'URL
            String videoUrl = saveVideo(file);

            // Mettre à jour l'exercice avec l'URL de la vidéo
            exercise.setVideoUrl(videoUrl);
            return exerciseRepository.save(exercise);
        }
        return null;
    }
    private String saveVideo(MultipartFile file) throws IOException {
        String uploadDir = "uploads/videos/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        // Créer le dossier si nécessaire
        Files.createDirectories(filePath.getParent());

        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retourner le chemin ou l'URL de la vidéo
        return "/uploads/videos/" + fileName;
    }
    // Méthode pour sauvegarder l'image localement (ou cloud)
    private String saveImage(MultipartFile file) throws IOException {
        String uploadDir = "uploads/images/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        // Créez le dossier si nécessaire
        Files.createDirectories(filePath.getParent());

        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retourner le chemin ou l'URL de l'image
        return "/uploads/images/" + fileName;
    }
    public Long getExerciseCount() {
        return exerciseRepository.count();
    }
}