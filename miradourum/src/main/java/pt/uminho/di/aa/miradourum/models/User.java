package pt.uminho.di.aa.miradourum.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    @Column(name = "Email")
    private String email;
    @Column(name = "Username")
    private String username;
    @Column(name = "Password")
    private String password;
    @Column(name = "Role")
    private Integer role;
    @Column(name = "ProfileImage")
    private String profileImage;
    @Column(name = "PremiumEndDate")
    private Date premiumEndDate;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "PontoInteresse_User", // Use the actual table name as it exists in your DB
            joinColumns = @JoinColumn(name = "UserID"), // Column in join table referring to User
            inverseJoinColumns = @JoinColumn(name = "PontoInteresseID") // Column referring to PontoInteresse
    )
    private List<PontoInteresse> pontoInteresse;

    public User() {}

    public User(String username, String email, String password, Integer role, String ProfileImage,Date premiumEndDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.premiumEndDate = premiumEndDate;
        this.profileImage = ProfileImage;
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Date getPremiumEndDate() {
        return premiumEndDate;
    }

    public void setPremiumEndDate(Date premiumEndDate) {
        this.premiumEndDate = premiumEndDate;
    }


    public List<PontoInteresse> getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(List<PontoInteresse> pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
