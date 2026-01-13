package com.example.mygymbro.dao;

import com.example.mygymbro.bean.ExerciseBean;
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
    // Chiave API (Registrati su RapidAPI per averne una vera, o usa una demo se disponibile)
    private static final String API_KEY = "INSERISCI_QUI_LA_TUA_CHIAVE_RAPIDAPI";
    private static final String API_HOST = "exercisedb.p.rapidapi.com";

    @Override
    public List<ExerciseBean> findAll() {
        List<ExerciseBean> myBeanList = new ArrayList<>();

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

            // Controllo se la chiamata è andata a buon fine (Code 200)
            if (response.statusCode() == 200) {
                String jsonBody = response.body();

                // 3. Parsing JSON con GSON
                Gson gson = new Gson();
                // Dico a Gson: "Guarda che mi arriva una Lista di ApiExerciseDto"
                Type listType = new TypeToken<List<ApiExerciseDto>>(){}.getType();
                List<ApiExerciseDto> apiList = gson.fromJson(jsonBody, listType);

                // 4. ADAPTER: Converto i DTO dell'API nei miei Bean
                for (ApiExerciseDto dto : apiList) {
                    ExerciseBean bean = new ExerciseBean();
                    bean.setName(dto.name); // Mapping diretto
                    bean.setMuscleGroup(dto.bodyPart); // Mapping con cambio nome
                    bean.setGifUrl(dto.gifUrl);

                    // Uniamo le istruzioni in una stringa unica per la descrizione
                    if (dto.instructions != null && !dto.instructions.isEmpty()) {
                        bean.setDescription(String.join(". ", dto.instructions));
                    }

                    myBeanList.add(bean);
                }
            } else {
                System.err.println("Errore API: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // In caso di errore, potresti ritornare una lista vuota o lanciare un'eccezione custom
        }

        return myBeanList;
    }

    // --- CLASSE INTERNA PER MAPPING JSON (DTO) ---
    // Questa classe serve SOLO per leggere il JSON esatto che arriva dall'API.
    // I campi devono avere lo stesso nome del JSON (case sensitive).
    private class ApiExerciseDto {
        String bodyPart;
        String equipment;
        String gifUrl;
        String id;
        String name;
        String target;
        List<String> instructions; // L'API restituisce un array di stringhe
    }
}