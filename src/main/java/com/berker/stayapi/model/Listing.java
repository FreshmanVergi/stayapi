package com.berker.stayapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User host;

    @Column(nullable = false)
    private Integer noOfPeople;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Double averageRating = 0.0;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    public Listing() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getHost() { return host; }
    public void setHost(User host) { this.host = host; }
    public Integer getNoOfPeople() { return noOfPeople; }
    public void setNoOfPeople(Integer n) { this.noOfPeople = n; }
    public String getCountry() { return country; }
    public void setCountry(String c) { this.country = c; }
    public String getCity() { return city; }
    public void setCity(String c) { this.city = c; }
    public Double getPrice() { return price; }
    public void setPrice(Double p) { this.price = p; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double r) { this.averageRating = r; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer c) { this.reviewCount = c; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Listing l = new Listing();
        public Builder host(User h) { l.host = h; return this; }
        public Builder noOfPeople(Integer n) { l.noOfPeople = n; return this; }
        public Builder country(String c) { l.country = c; return this; }
        public Builder city(String c) { l.city = c; return this; }
        public Builder price(Double p) { l.price = p; return this; }
        public Builder title(String t) { l.title = t; return this; }
        public Builder description(String d) { l.description = d; return this; }
        public Listing build() { return l; }
    }
}
