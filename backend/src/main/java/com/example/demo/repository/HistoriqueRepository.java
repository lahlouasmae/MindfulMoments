package com.example.demo.repository;

import com.example.demo.model.Historique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByUser_Id(Long userId);

    Optional<Historique> findByUser_IdAndExercise_Id(Long userId, Long exerciseId);

    @Modifying
    @Transactional
    void deleteByUser_IdAndExercise_Id(Long userId, Long exerciseId);

    @Modifying
    @Transactional
    void deleteByUser_Id(Long userId);
    @Query("SELECT COUNT(h) FROM Historique h WHERE h.user.id = :userId")
    Long countByUserId(Long userId);
}