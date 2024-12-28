package com.example.demo.service;

import com.example.demo.model.Exercise;
import com.example.demo.model.Historique;
import com.example.demo.model.User;
import com.example.demo.repository.HistoriqueRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoriqueService {

    @Autowired
    private HistoriqueRepository historiqueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Historique> getHistoriqueForUser(Long userId) {
        return historiqueRepository.findByUser_Id(userId).stream()
                .peek(historique -> {
                    if (historique.getExercise() != null) {
                        historique.setExerciseId(historique.getExercise().getId());
                    }
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void ajouterHistorique(Long userId, Long exerciseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));

        Optional<Historique> existingHistorique = historiqueRepository.findByUser_IdAndExercise_Id(userId, exerciseId);

        if (existingHistorique.isEmpty()) {
            Historique historique = new Historique();
            historique.setUser(user);
            historique.setExercise(exercise);
            historique.setCompleted(true);

            // Copie des champs existants
            historique.setName(exercise.getName());
            historique.setType(exercise.getType());
            historique.setMinStressLevel(exercise.getMinStressLevel());
            historique.setMaxStressLevel(exercise.getMaxStressLevel());

            // Copie des nouveaux champs
            historique.setImageUrl(exercise.getImageUrl());
            historique.setVideoUrl(exercise.getVideoUrl());
            historique.setDuration(exercise.getDuration());
            historique.setCalories(exercise.getCalories());
            historique.setDifficulty(exercise.getDifficulty());
            historique.setDescription(exercise.getDescription());
            historique.setInstructions(exercise.getInstructions());
            historique.setBenefits(exercise.getBenefits());

            historiqueRepository.save(historique);
        }
    }

    @Transactional
    public void supprimerHistorique(Long userId, Long exerciseId) {
        historiqueRepository.deleteByUser_IdAndExercise_Id(userId, exerciseId);
    }
    public Long getHistoriqueCountByUserId(Long userId) {
        return historiqueRepository.countByUserId(userId);
    }
}