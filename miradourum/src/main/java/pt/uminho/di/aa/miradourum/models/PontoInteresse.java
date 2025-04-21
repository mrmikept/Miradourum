package pt.uminho.di.aa.miradourum.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class PontoInteresse
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "Coordinates")
    private String coordinates;
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;
    @Column(name = "Dificulty")
    private Integer difficulty;
    @Column(name = "Accessibility")
    private Boolean accessibility;
    @Column(name = "State")
    private Boolean state;
    @Column(name = "Premium")
    private Boolean premium;
    @Column(name = "Score")
    private Double score;
    @Column(name = "CreationDate")
    private LocalDateTime creationDate;

    @ManyToMany(mappedBy = "pontoInteresse")
    private List<User> userList;

    public PontoInteresse(Double score, Boolean state, Boolean accessibility, Integer difficulty, String description, String name, String coordinates) {
        this.score = score;
        this.state = state;
        this.accessibility = accessibility;
        this.difficulty = difficulty;
        this.description = description;
        this.name = name;
        this.coordinates = coordinates;
    }

    public PontoInteresse() {
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
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

    public Boolean getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Boolean accessibility) {
        this.accessibility = accessibility;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
