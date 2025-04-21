package pt.uminho.di.aa.miradourum.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int id;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "Rating")
    private Integer rating;

    @Column(name = "CreationDate")
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Column(name = "UserID")
    private Integer userid;


    @OneToMany(mappedBy = "review")
    private List<Image> images;

    @ManyToOne
    @JoinColumn(name = "PontoInteresseID") // this is the DB column that stores the FK
    private PontoInteresse pontoInteresse;


    public Review(Integer rating, String comment, Date creationDate, Integer userid, PontoInteresse pontoInteresse, List<Image> images) {
        this.rating = rating;
        this.comment = comment;
        this.creationDate = creationDate;
        this.userid = userid;
        this.images = images;
        this.userid = userid;
    }

    public Review() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }


    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
