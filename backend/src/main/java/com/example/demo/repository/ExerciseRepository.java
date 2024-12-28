package com.example.demo.repository;

import com.example.demo.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("SELECT e FROM Exercise e WHERE :stressLevel BETWEEN e.minStressLevel AND e.maxStressLevel")
    List<Exercise> findByStressLevel(@Param("stressLevel") int stressLevel);
}
