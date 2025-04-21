package pt.uminho.di.aa.miradourum.models;

import jakarta.persistence.*;

@Entity
public class PontoInteresse
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String coordinates;
    private String name;
    private String description;
    private Integer difficulty;
    private Boolean accessibility;
    private Boolean state;
    private Double score;
    
    @ManyToMany(mappedBy = "PontoInteresse_User")
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
