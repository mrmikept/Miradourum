package pt.uminho.di.aa.miradourum.models;

import jakarta.persistence.*;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String email;
    private String password;
    private Boolean premium;
    private String premiumEndDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
    @ManyToMany
    private List<PontoInteresse> pontoInteresses;

    public User() {}

    public User(String username, String email, String password, Boolean premium, String premiumEndDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.premium = premium;
        this.premiumEndDate = premiumEndDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getPremiumEndDate() {
        return premiumEndDate;
    }

    public void setPremiumEndDate(String premiumEndDate) {
        this.premiumEndDate = premiumEndDate;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<PontoInteresse> getPontoInteresses() {
        return pontoInteresses;
    }

    public void setPontoInteresses(List<PontoInteresse> pontoInteresses) {
        this.pontoInteresses = pontoInteresses;
    }
}
