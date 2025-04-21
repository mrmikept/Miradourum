package pt.uminho.di.aa.miradourum.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
}
