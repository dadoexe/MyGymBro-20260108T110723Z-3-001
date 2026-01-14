package com.example.mygymbro.dao;

import com.example.mygymbro.model.Exercise;
import com.example.mygymbro.model.MuscleGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RestApiExerciseDAO implements ExerciseDAO {

    // URL dell'API esterna
    private static final String API_URL = "https://exercisedb.p.rapidapi.com/exercises?limit=10";
    // Chiave API (Sostituisci con la tua chiave reale se vuoi testare)
    private static final String API_KEY = "b5a76e4d57msh6edf21dcd3dd851p199802jsne8b14218cbd4";
    private static final String API_HOST = "exercisedb.p.rapidapi.com";

    @Override
    public List<Exercise> findAll() {
        // Lista di Model che restituiremo
        List<Exercise> modelList = new ArrayList<>();

        try {
            // 1. Costruzione della richiesta HTTP (Java 11+)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("X-RapidAPI-Key", API_KEY)
                    .header("X-RapidAPI-Host", API_HOST)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            // 2. Invio richiesta e ricezione risposta
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Controllo risposta
            if (response.statusCode() == 200) {
                String jsonBody = response.body();

                // Parsing JSON con GSON
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ApiExerciseDto>>() {}.getType();
                List<ApiExerciseDto> apiList = gson.fromJson(jsonBody, listType);

                // 4. Mapping: DTO (Api) -> MODEL (Dominio)
                if (apiList != null) {
                    for (ApiExerciseDto dto : apiList) {

                        // Generiamo un ID numerico basato sull'hash dell'ID stringa dell'API
                        // (Non perfetto, ma funzionale per la demo in RAM)
                        int fakeId = (dto.id != null) ? dto.id.hashCode() : 0;

                        // Uniamo le istruzioni (che sono una lista) in un'unica stringa
                        String description = (dto.instructions != null) ? String.join(" ", dto.instructions) : "Nessuna descrizione";

                        // Gestione Enum MuscleGroup (con fallback se non matcha)
                        MuscleGroup mg = MuscleGroup.CHEST; // Default
                        try {
                            if (dto.bodyPart != null) {
                                // Sostituisce spazi con underscore e mette in maiuscolo (es: "upper legs" -> "UPPER_LEGS")
                                String enumName = dto.bodyPart.toUpperCase().replace(" ", "_");
                                mg = MuscleGroup.valueOf(enumName);
                            }
                        } catch (IllegalArgumentException e) {
                            // Se il muscolo dell'API non esiste nel nostro Enum, teniamo il default (o gestisci diversamente)
                            System.out.println("Gruppo muscolare non mappato: " + dto.bodyPart);
                        }

                        // Creazione del MODEL
                        Exercise model = new Exercise(fakeId, dto.name, description, mg);
                        modelList.add(model);
                    }
                }
            } else {
                System.err.println("Errore API: Codice " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return modelList;
    }

    @Override
    public Exercise findByName(String name) {
        // Implementazione inefficiente ma funzionante: scarica tutto e filtra in memoria
        return findAll().stream()
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // --- CLASSE INTERNA PER MAPPING JSON (DTO) ---
    private class ApiExerciseDto {
        String bodyPart;
        String equipment;
        String gifUrl;
        String id;
        String name;
        String target;
        List<String> instructions;
    }
}

