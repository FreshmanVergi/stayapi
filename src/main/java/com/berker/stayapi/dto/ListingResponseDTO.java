package com.berker.stayapi.dto;

public class ListingResponseDTO {
    private Long id;
    private String title, description, country, city, hostUsername;
    private Integer noOfPeople, reviewCount;
    private Double price, averageRating;

    public ListingResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getCountry() { return country; }
    public void setCountry(String c) { this.country = c; }
    public String getCity() { return city; }
    public void setCity(String c) { this.city = c; }
    public String getHostUsername() { return hostUsername; }
    public void setHostUsername(String h) { this.hostUsername = h; }
    public Integer getNoOfPeople() { return noOfPeople; }
    public void setNoOfPeople(Integer n) { this.noOfPeople = n; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer r) { this.reviewCount = r; }
    public Double getPrice() { return price; }
    public void setPrice(Double p) { this.price = p; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double a) { this.averageRating = a; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ListingResponseDTO d = new ListingResponseDTO();
        public Builder id(Long id) { d.id = id; return this; }
        public Builder title(String t) { d.title = t; return this; }
        public Builder description(String desc) { d.description = desc; return this; }
        public Builder country(String c) { d.country = c; return this; }
        public Builder city(String c) { d.city = c; return this; }
        public Builder hostUsername(String h) { d.hostUsername = h; return this; }
        public Builder noOfPeople(Integer n) { d.noOfPeople = n; return this; }
        public Builder reviewCount(Integer r) { d.reviewCount = r; return this; }
        public Builder price(Double p) { d.price = p; return this; }
        public Builder averageRating(Double a) { d.averageRating = a; return this; }
        public ListingResponseDTO build() { return d; }
    }
}
