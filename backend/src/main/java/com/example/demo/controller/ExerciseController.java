package com.example.demo.controller;

import com.example.demo.model.Exercise;
import com.example.demo.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    /**
     * Récupère la liste de tous les exercices.
     *
     * @return ResponseEntity contenant la liste des exercices
     */
    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        try {
            List<Exercise> exercises = exerciseService.getAllExercises();
            exercises.forEach(exercise -> exercise.setHistoriques(null)); // Supprime les historiques de la réponse
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * Récupère la liste des exercices complétés pour un utilisateur donné.
     *
     * @param userId L'ID de l'utilisateur
     * @return ResponseEntity contenant la liste des exercices complétés
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<Exercise>> getCompletedExercisesForUser(@PathVariable Long userId) {
        try {
            List<Exercise> completedExercises = exerciseService.getCompletedExercisesForUser(userId);
            return ResponseEntity.ok(completedExercises);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Bascule l'état de complétion d'un exercice pour un utilisateur donné.
     *
     * @param exerciseId L'ID de l'exercice
     * @param userId     L'ID de l'utilisateur
     * @return ResponseEntity contenant l'exercice mis à jour
     */
    @PutMapping("/{exerciseId}/toggle/{userId}")
    public ResponseEntity<Exercise> toggleExerciseCompletion(
            @PathVariable Long exerciseId, @PathVariable Long userId) {
        try {
            Exercise updatedExercise = exerciseService.toggleExerciseCompletion(exerciseId, userId);
            return ResponseEntity.ok(updatedExercise);
        } catch (IllegalArgumentException e) {
            // Gestion des erreurs spécifiques (par exemple, entité non trouvée)
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping
    public ResponseEntity<Exercise> addExercise(@RequestBody Exercise exercise) {
        try {
            Exercise savedExercise = exerciseService.saveExercise(exercise);
            return ResponseEntity.ok(savedExercise);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    /**
     * Met à jour un exercice.
     *
     * @param id       ID de l'exercice.
     * @param exercise Nouvelles données de l'exercice.
     * @return L'exercice mis à jour.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise exercise) {
        try {
            Exercise updatedExercise = exerciseService.updateExercise(id, exercise);
            return ResponseEntity.ok(updatedExercise);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Supprime un exercice par son ID.
     *
     * @param id ID de l'exercice.
     * @return Confirmation de suppression.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        try {
            exerciseService.deleteExercise(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    /**
     * Récupère un exercice par son ID.
     *
     * @param id ID de l'exercice.
     * @return L'exercice correspondant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        try {
            Exercise exercise = exerciseService.getExerciseById(id);
            if (exercise != null) {
                exercise.setHistoriques(null); // Supprime les historiques de la réponse pour éviter les données inutiles
                return ResponseEntity.ok(exercise);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stress/{stressLevel}")
    public ResponseEntity<List<Exercise>> getExercisesByStressLevel(@PathVariable int stressLevel) {
        List<Exercise> exercises = exerciseService.getExercisesByStressLevel(stressLevel);
        return ResponseEntity.ok(exercises);
    }
    @PostMapping("/{id}/uploadImage")
    public ResponseEntity<Exercise> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Exercise updatedExercise = exerciseService.uploadImageToExercise(id, file);
            if (updatedExercise != null) {
                return ResponseEntity.ok(updatedExercise);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/{id}/uploadVideo")
    public ResponseEntity<Exercise> uploadVideo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Exercise updatedExercise = exerciseService.uploadVideoToExercise(id, file);
            if (updatedExercise != null) {
                return ResponseEntity.ok(updatedExercise);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/videos/{filename}")
    public ResponseEntity<Resource> getVideo(@PathVariable String filename) {
        Path videoPath = Paths.get("uploads/videos/" + filename);
        Resource videoResource = new FileSystemResource(videoPath);

        if (videoResource.exists()) {
            return ResponseEntity.ok().contentType(MediaType.valueOf("video/mp4"))
                    .body(videoResource);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Path imagePath = Paths.get("uploads/images/" + filename);
        Resource imageResource = new FileSystemResource(imagePath);

        if (imageResource.exists() && imageResource.isReadable()) {
            String contentType = "image/webp"; // Changez dynamiquement selon le type d'image si nécessaire
            if (filename.toLowerCase().endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
            else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getExerciseCount() {
        try {
            Long count = exerciseService.getExerciseCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
