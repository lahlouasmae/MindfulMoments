package com.example.myapplication.api;

import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Historique;
import com.example.myapplication.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/users/signup")
    Call<User> signup(@Body User user);
    @POST("api/users/login")
    Call<User> login(@Body User user);

    @GET("api/users/{id}")
    Call<User> getProfile(@Path("id") Long id);


        @Multipart
        @PUT("api/users/{userId}")
        Call<User> updateProfile(
                @Path("userId") Long userId,
                @Part("user") RequestBody userJson,
                @Part MultipartBody.Part file  // Changé de 'image' à 'file'
        );

    @GET("/api/users")
    Call<List<User>> getAllUsers();
    @DELETE("/api/users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);
    @POST("/api/exercises")
    Call<Exercise> addExercise(@Body Exercise exercise);
    @DELETE("/api/exercises/{id}")
    Call<Void> deleteExercise(@Path("id") Long id);
    @PUT("/api/exercises/{id}")
    Call<Exercise> updateExercise(@Path("id") Long id, @Body Exercise exercise);
    // Exercices
    @GET("/api/exercises/completed")
    Call<List<Exercise>> getCompletedExercises();

    @GET("/api/exercises")
    Call<List<Exercise>> getExercises();

    @PUT("/api/exercises/{id}/toggle")
    Call<Exercise> toggleExerciseCompletion(@Path("id") Long id);

    @GET("api/exercises/{id}")
    Call<Exercise> getExerciseById(@Path("id") Long id);
    @GET("/api/exercises")
    Call<List<Exercise>> getAllExercises();

    @GET("/api/exercises/stress/{level}")
    Call<List<Exercise>> getExercisesWithinStressRange(@Path("level") int stressLevel);
    @Multipart
    @POST("api/exercises/{id}/uploadImage")
    Call<Exercise> uploadExerciseImage(
            @Path("id") Long id,
            @Part MultipartBody.Part file // Fichier image
    );
    // Endpoints pour l'historique
    @GET("/api/historique/user/{userId}")
    Call<List<Historique>> getHistoriqueForUser(@Path("userId") Long userId);

    @POST("/api/historique/user/{userId}/exercise/{exerciseId}")
    Call<Void> ajouterHistorique(@Path("userId") Long userId, @Path("exerciseId") Long exerciseId);

    @DELETE("/api/historique/user/{userId}/exercise/{exerciseId}")
    Call<Void> supprimerHistorique(@Path("userId") Long userId, @Path("exerciseId") Long exerciseId);
    @GET("api/users/{userId}/stress/data")
    Call<List<Object>> getStressData(@Path("userId") Long userId);
    @POST("api/users/{userId}/stress")
    Call<Void> addStressLevel(@Path("userId") Long userId, @Query("stressValue") int stressValue);
    @GET("/api/users/{userId}/stress/average")
    Call<String> getAverageStressLevel(@Path("userId") Long userId);
    @Multipart
    @POST("api/exercises/{id}/uploadVideo")
    Call<Exercise> uploadExerciseVideo(
            @Path("id") Long id,
            @Part MultipartBody.Part file
    );
    @GET("/api/exercises/count")
    Call<Long> getExerciseCount();

    @GET("/api/historique/count/{userId}")
    Call<Long> getHistoriqueCount(@Path("userId") long userId);
    @GET("/api/users/count")
    Call<Long> getTotalUsers();

}