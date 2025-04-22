package pt.uminho.di.aa.miradourum.dtos.PontoInteresse;

import pt.uminho.di.aa.miradourum.models.PontoInteresse;

import java.time.LocalDateTime;

public class PIDetailsDto {
    private double latitude;
    private double longitude;
    private String name;
    private String description;
    private Integer difficulty;
    private boolean accessibility;
    private boolean premium;
    private double score;
    private LocalDateTime creationDate;

    public PIDetailsDto() {
    }

    public PIDetailsDto(double latitude, double longitude, String name, String description, Integer difficulty, boolean accessibility, boolean premium, double score, LocalDateTime creationDate) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.accessibility = accessibility;
        this.premium = premium;
        this.score = score;
        this.creationDate = creationDate;
    }

    public PontoInteresse toPontoInteresse() {
        PontoInteresse p = new PontoInteresse();
        p.setLatitude(this.latitude);
        p.setLongitude(this.longitude);
        p.setName(this.name);
        p.setDescription(this.description);
        p.setDifficulty(this.difficulty);
        p.setAccessibility(this.accessibility);
        p.setPremium(this.premium);
        p.setScore(this.score);
        p.setCreationDate(this.creationDate);

        return p;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isAccessibility() {
        return accessibility;
    }

    public void setAccessibility(boolean accessibility) {
        this.accessibility = accessibility;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
