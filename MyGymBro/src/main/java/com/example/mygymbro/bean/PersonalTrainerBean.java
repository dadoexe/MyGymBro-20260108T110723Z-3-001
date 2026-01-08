package com.example.mygymbro.bean;

import java.util.ArrayList;
import java.util.List;

public class PersonalTrainerBean extends UserBean {

    private String certificationCode;

    // NOTA: Qui usiamo AthleteBean, non l'entity Athlete!
    private List<AthleteBean> managedAthletes = new ArrayList<>();

    public PersonalTrainerBean() {
        // Costruttore vuoto
    }

    public String getCertificationCode() {
        return certificationCode;
    }

    public void setCertificationCode(String certificationCode) {
        this.certificationCode = certificationCode;
    }

    public List<AthleteBean> getManagedAthletes() {
        return managedAthletes;
    }

    public void setManagedAthletes(List<AthleteBean> managedAthletes) {
        this.managedAthletes = managedAthletes;
    }

    // Metodo helper per la View
    public void addAthleteBean(AthleteBean athleteBean) {
        this.managedAthletes.add(athleteBean);
    }
}