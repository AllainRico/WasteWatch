package com.example.loginandregister.collectorResidentVerification;

public class ResidentModel {
    private String firstName;
    private String lastName;

    public ResidentModel() {

    }
    public ResidentModel(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }
}
