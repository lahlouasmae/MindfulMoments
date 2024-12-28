package com.example.myapplication;

import android.content.Context;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class SimpleTokenizer {
    private final Map<String, Integer> vocab;
    private static final int UNKNOWN_TOKEN = 1;
    private static final int MAX_LENGTH = 128;

    public SimpleTokenizer(Context context) {
        vocab = loadVocabulary(context);
    }

    private Map<String, Integer> loadVocabulary(Context context) {
        Map<String, Integer> vocabMap = new HashMap<>();
        try {
            InputStreamReader isr = new InputStreamReader(
                    context.getAssets().open("roberta_vocab.jsonx"),
                    StandardCharsets.UTF_8
            );
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONObject jsonVocab = new JSONObject(content.toString());
            Iterator<String> keys = jsonVocab.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                vocabMap.put(key, jsonVocab.getInt(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vocabMap;
    }

    public long[] tokenize(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        long[] tokens = new long[MAX_LENGTH];

        tokens[0] = vocab.getOrDefault("<s>", UNKNOWN_TOKEN);

        int position = 1;
        for (String word : words) {
            if (position < MAX_LENGTH - 1) {
                tokens[position] = vocab.getOrDefault(word, UNKNOWN_TOKEN);
                position++;
            }
        }

        if (position < MAX_LENGTH) {
            tokens[position] = vocab.getOrDefault("</s>", UNKNOWN_TOKEN);
            position++;
        }

        while (position < MAX_LENGTH) {
            tokens[position] = vocab.getOrDefault("<pad>", 0);
            position++;
        }

        return tokens;
    }
}