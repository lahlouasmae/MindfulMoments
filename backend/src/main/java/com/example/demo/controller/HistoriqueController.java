package com.example.demo.controller;

import com.example.demo.model.Historique;
import com.example.demo.service.HistoriqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
public class HistoriqueController {

    @Autowired
    private HistoriqueService historiqueService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Historique>> getHistoriqueForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(historiqueService.getHistoriqueForUser(userId));
    }

    @PostMapping("/user/{userId}/exercise/{exerciseId}")
    public ResponseEntity<Void> ajouterHistorique(
            @PathVariable Long userId,
            @PathVariable Long exerciseId) {
        try {
            historiqueService.ajouterHistorique(userId, exerciseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/user/{userId}/exercise/{exerciseId}")
    public ResponseEntity<Void> supprimerHistorique(
            @PathVariable Long userId,
            @PathVariable Long exerciseId) {
        try {
            historiqueService.supprimerHistorique(userId, exerciseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/count/{userId}")
    public ResponseEntity<Long> getHistoriqueCount(@PathVariable Long userId) {
        Long count = historiqueService.getHistoriqueCountByUserId(userId);
        return ResponseEntity.ok(count);
    }
}