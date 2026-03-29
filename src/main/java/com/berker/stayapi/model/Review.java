package com.berker.stayapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Listing listing;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    public Review() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking b) { this.booking = b; }
    public User getGuest() { return guest; }
    public void setGuest(User g) { this.guest = g; }
    public Listing getListing() { return listing; }
    public void setListing(Listing l) { this.listing = l; }
    public Integer getRating() { return rating; }
    public void setRating(Integer r) { this.rating = r; }
    public String getComment() { return comment; }
    public void setComment(String c) { this.comment = c; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Review r = new Review();
        public Builder booking(Booking b) { r.booking = b; return this; }
        public Builder guest(User g) { r.guest = g; return this; }
        public Builder listing(Listing l) { r.listing = l; return this; }
        public Builder rating(Integer rating) { r.rating = rating; return this; }
        public Builder comment(String c) { r.comment = c; return this; }
        public Review build() { return r; }
    }
}
