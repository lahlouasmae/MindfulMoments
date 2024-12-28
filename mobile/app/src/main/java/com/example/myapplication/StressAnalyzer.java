package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.IValue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StressAnalyzer {
    private final Module model;
    private final SimpleTokenizer tokenizer;
    private final Context context;

    public StressAnalyzer(Context context) {
        this.context = context;
        this.tokenizer = new SimpleTokenizer(context);
        this.model = loadModel();
    }

    private Module loadModel() {
        try {
            String modelPath = assetFilePath("roberta_stress_model.ptx");
            File modelFile = new File(modelPath);

            // Vérification du fichier
            if (!modelFile.exists()) {
                throw new IOException("Le fichier modèle n'existe pas");
            }

            // Log de la taille du fichier
            Log.d("StressAnalyzer", "Taille du modèle: " + modelFile.length() + " bytes");

            return Module.load(modelPath);
        } catch (IOException e) {
            Log.e("StressAnalyzer", "Erreur de chargement du modèle", e);
            throw new RuntimeException("Erreur de chargement du modèle", e);
        }
    }

    private String assetFilePath(String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
            }
            return file.getAbsolutePath();
        }
    }

    public float analyzeStress(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0f;
        }

        try {
            // Tokenisation
            long[] tokens = tokenizer.tokenize(text);

            // Création du tensor
            long[] shape = new long[]{1, 128}; // Batch size 1, sequence length 128
            Tensor inputTensor = Tensor.fromBlob(tokens, shape);

            // Prédiction
            Tensor outputTensor = model.forward(IValue.from(inputTensor)).toTensor();
            float[] scores = outputTensor.getDataAsFloatArray();

            return scores[0];
        } catch (Exception e) {
            Log.e("StressAnalyzer", "Erreur lors de l'analyse", e);
            return 0.0f;
        }
    }
}
