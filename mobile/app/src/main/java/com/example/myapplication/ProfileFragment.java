package com.example.myapplication;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.User;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import java.io.InputStream;

public class ProfileFragment extends Fragment {
    private static final String BASE_URL = "http://10.0.2.2:8080"; // Pour l'émulateur

    private static final String TAG = "ProfileFragment";
    private EditText firstNameEditText, lastNameEditText, phoneEditText, addressEditText;
    private Button updateButton;
    private ImageView profileImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private Uri selectedImageUri;
    private ApiService apiService;
    private Long userId;
    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private static final String IMAGE_BASE_URL = BASE_URL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getLong("USER_ID", -1);

        if (userId == -1) {
            showError("Session expirée");
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        try {
            initializeViews(view);
            loadProfile();
            setupUpdateButton();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: ", e);
        }

        return view;
    }

    private void initializeViews(View view) {
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        updateButton = view.findViewById(R.id.updateButton);
        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        profileImageView = view.findViewById(R.id.profileImageView);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        selectImageButton.setOnClickListener(v -> openImagePicker());
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupUpdateButton() {
        updateButton.setOnClickListener(v -> {
            try {
                updateProfile();
            } catch (Exception e) {
                Log.e(TAG, "Error updating profile: ", e);
                showError("Erreur lors de la mise à jour");
            }
        });
    }

    private void openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                launchImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                launchImagePicker();
            }
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Sélectionner une image"),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            String realPath = getRealPathFromURI(selectedImageUri);
            Log.d(TAG, "URI sélectionnée : " + selectedImageUri);
            Log.d(TAG, "Chemin réel de l'image : " + realPath);

            profileImageView.setImageURI(selectedImageUri);
        } else {
            Log.e(TAG, "Aucune image sélectionnée ou opération annulée.");
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String result = null;
        if (getContext() != null && getContext().getContentResolver() != null) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (idx != -1) {
                    result = cursor.getString(idx);
                }
                cursor.close();
            }
        }
        return result;
    }

    private void loadImageFromUrl(String imageUrl) {
        if (getContext() == null) return;

        Log.d(TAG, "Tentative de chargement de l'image : " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.d(TAG, "URL d'image nulle ou vide");
            profileImageView.setImageResource(R.drawable.default_profile);
            return;
        }

        String fullImageUrl;
        if (!imageUrl.startsWith("http")) {
            fullImageUrl = IMAGE_BASE_URL + (imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl);
        } else {
            fullImageUrl = imageUrl;
        }

        Log.d(TAG, "URL d'image complète : " + fullImageUrl);

        Glide.with(requireContext())
                .load(fullImageUrl + "?timestamp=" + System.currentTimeMillis())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Échec du chargement de l'image", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.d(TAG, "Image chargée avec succès");
                        return false;
                    }
                })
                .into(profileImageView);
    }




    private void updateProfile() {
        try {
            // Préparer les données utilisateur sans les champs non reconnus
            JsonObject userUpdateData = new JsonObject();
            userUpdateData.addProperty("id", userId);
            userUpdateData.addProperty("firstName", firstNameEditText.getText().toString().trim());
            userUpdateData.addProperty("lastName", lastNameEditText.getText().toString().trim());
            userUpdateData.addProperty("email", emailEditText.getText().toString().trim());
            userUpdateData.addProperty("phoneNumber", phoneEditText.getText().toString().trim());
            userUpdateData.addProperty("address", addressEditText.getText().toString().trim());

            // N'ajouter le mot de passe que s'il est rempli
            String password = passwordEditText.getText().toString().trim();
            if (!password.isEmpty()) {
                userUpdateData.addProperty("password", password);
            }

            // Convertir en JSON
            String userJson = userUpdateData.toString();
            Log.d(TAG, "User JSON: " + userJson);

            // Créer la partie user
            RequestBody userPart = RequestBody.create(
                    MediaType.parse("application/json"),
                    userJson
            );

            // Gérer l'image si sélectionnée
            MultipartBody.Part filePart = null;
            if (selectedImageUri != null) {
                try {
                    ContentResolver contentResolver = requireContext().getContentResolver();
                    String mimeType = contentResolver.getType(selectedImageUri);

                    InputStream inputStream = contentResolver.openInputStream(selectedImageUri);
                    if (inputStream != null) {
                        byte[] fileBytes = IOUtils.toByteArray(inputStream);
                        inputStream.close();

                        RequestBody requestFile = RequestBody.create(
                                MediaType.parse(mimeType != null ? mimeType : "image/*"),
                                fileBytes
                        );

                        filePart = MultipartBody.Part.createFormData("file", "profile.jpg", requestFile);
                        Log.d(TAG, "Image prepared successfully");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error preparing image", e);
                    showError("Erreur lors de la préparation de l'image: " + e.getMessage());
                    return;
                }
            }

            // Faire l'appel API
            Call<User> call = apiService.updateProfile(userId, userPart, filePart);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (getActivity() == null) return;

                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Profile updated successfully");
                            showSuccess("Profil mis à jour avec succès");
                            loadProfile();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string() : "";
                                Log.e(TAG, "Server error: " + response.code() + " - " + errorBody);
                                showError("Erreur " + response.code() +
                                        (errorBody.isEmpty() ? "" : ": " + errorBody));
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading error body", e);
                                showError("Erreur " + response.code());
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    if (getActivity() == null) return;
                    Log.e(TAG, "Network failure", t);
                    getActivity().runOnUiThread(() ->
                            showError("Erreur réseau: " + t.getMessage()));
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in updateProfile", e);
            showError("Erreur inattendue: " + e.getMessage());
        }
    }
    private void loadProfile() {
        apiService.getProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    User user = response.body();

                    // Mise à jour des champs de texte
                    firstNameEditText.setText(user.getFirstName());
                    lastNameEditText.setText(user.getLastName());
                    emailEditText.setText(user.getEmail());
                    phoneEditText.setText(user.getPhoneNumber());
                    addressEditText.setText(user.getAddress());

                    // Ne pas afficher le mot de passe pour des raisons de sécurité
                    passwordEditText.setText("");

                    // Gestion de l'image de profil
                    String imageUrl = user.getImageUrl();
                    Log.d(TAG, "URL de l'image du profil : " + imageUrl);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Construire l'URL complète
                        final String fullImageUrl = imageUrl.startsWith("http")
                                ? imageUrl
                                : BASE_URL + (imageUrl.startsWith("/") ? "" : "/") + imageUrl + "?t=" + System.currentTimeMillis();

                        Log.d(TAG, "URL complète de l'image : " + fullImageUrl);

                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .timeout(30000);

                        try {
                            Glide.with(requireContext())
                                    .load(fullImageUrl)
                                    .apply(requestOptions)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                    Target<Drawable> target, boolean isFirstResource) {
                                            Log.e(TAG, "Échec du chargement de l'image: " + fullImageUrl, e);
                                            if (e != null) {
                                                for (Throwable t : e.getRootCauses()) {
                                                    Log.e(TAG, "Cause: " + t.getMessage());
                                                }
                                            }
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model,
                                                                       Target<Drawable> target, DataSource dataSource,
                                                                       boolean isFirstResource) {
                                            Log.d(TAG, "Image chargée avec succès");
                                            return false;
                                        }
                                    })
                                    .into(profileImageView);
                        } catch (Exception e) {
                            Log.e(TAG, "Erreur lors du chargement de l'image", e);
                            profileImageView.setImageResource(R.drawable.default_profile);
                        }
                    } else {
                        Log.d(TAG, "Aucune image de profil. Utilisation de l'image par défaut.");
                        profileImageView.setImageResource(R.drawable.default_profile);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Erreur inconnue";
                        Log.e(TAG, "Erreur serveur: " + response.code() + " - " + errorBody);
                        showError("Erreur lors du chargement du profil: " + response.code());
                    } catch (IOException e) {
                        Log.e(TAG, "Erreur lors de la lecture de l'erreur", e);
                        showError("Erreur lors du chargement du profil");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur lors du chargement du profil", t);
                showError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    private void safeSetText(EditText editText, String text) {
        if (editText != null) {
            editText.setText(text != null ? text : "");
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void showSuccess(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firstNameEditText = null;
        lastNameEditText = null;
        phoneEditText = null;
        addressEditText = null;
        updateButton = null;
        profileImageView = null;
    }
}