package com.berker.stayapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User guest;

    @Column(nullable = false)
    private LocalDate dateFrom;

    @Column(nullable = false)
    private LocalDate dateTo;

    @ElementCollection
    @CollectionTable(name = "booking_guests", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "guest_name")
    private List<String> guestNames;

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Listing getListing() { return listing; }
    public void setListing(Listing l) { this.listing = l; }
    public User getGuest() { return guest; }
    public void setGuest(User g) { this.guest = g; }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate d) { this.dateFrom = d; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate d) { this.dateTo = d; }
    public List<String> getGuestNames() { return guestNames; }
    public void setGuestNames(List<String> n) { this.guestNames = n; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Booking b = new Booking();
        public Builder listing(Listing l) { b.listing = l; return this; }
        public Builder guest(User g) { b.guest = g; return this; }
        public Builder dateFrom(LocalDate d) { b.dateFrom = d; return this; }
        public Builder dateTo(LocalDate d) { b.dateTo = d; return this; }
        public Builder guestNames(List<String> n) { b.guestNames = n; return this; }
        public Booking build() { return b; }
    }
}
