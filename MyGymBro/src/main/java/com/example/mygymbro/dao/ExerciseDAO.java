package com.example.mygymbro.dao;

import com.example.mygymbro.bean.ExerciseBean;
import java.util.List;

public interface ExerciseDAO {
    // Ora ritorna ExerciseBean, non Exercise (Model)
    List<ExerciseBean> findAll();
    List<ExerciseBean> findByName(String name); // Cambiato in List per coerenza con la ricerca parziale
}
