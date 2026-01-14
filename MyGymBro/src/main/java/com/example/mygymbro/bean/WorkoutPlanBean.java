package com.example.mygymbro.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutPlanBean {

    private String name;
    private String comment;
    private int id;
    private Date creationDate;
    // Lista di EXERCISE BEAN, non di entity!
    private List<WorkoutExerciseBean> exerciseList = new ArrayList<>();

    public WorkoutPlanBean() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public List<WorkoutExerciseBean> getExerciseList() { return exerciseList; }
    public void setExerciseList(List<WorkoutExerciseBean> exerciseList) {
        this.exerciseList = exerciseList;
    }
    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public void addExerciseBean(WorkoutExerciseBean exerciseBean) {
        this.exerciseList.add(exerciseBean);
    }
}