package com.example.mygymbro.model;

import java.util.Date;
import java.util.List;
public class WorkoutPlan {

    private int id;
    private String name;
    private Date creationDate;
    private String comment;
    private List<WorkoutExercise> listaEsercizi;

    public WorkoutPlan(int id, String name, Date creationDate, String comment) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.comment = comment;
    }

    public void addExercise(WorkoutExercise ex) {

        listaEsercizi.add(ex);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void removeExercise(WorkoutExercise ex) {
        listaEsercizi.remove(ex);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getComment() {
        return comment;
    }

    public List<WorkoutExercise> getListaEsercizi() {
        return listaEsercizi;
    }

    public int getEstimatedMinutes() {
        int totalSeconds = 0;
        for(WorkoutExercise ex : listaEsercizi) {
            totalSeconds += ex.getSets() * (45 + ex.getRestTime()); // 45s media per serie
        }
        return totalSeconds / 60;
    }

}
