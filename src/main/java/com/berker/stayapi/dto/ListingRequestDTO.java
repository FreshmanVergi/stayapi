package com.berker.stayapi.dto;

import jakarta.validation.constraints.*;

public class ListingRequestDTO {
    @NotNull @Min(1) private Integer noOfPeople;
    @NotBlank private String country;
    @NotBlank private String city;
    @NotNull @Min(0) private Double price;
    @NotBlank private String title;
    private String description;

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
}
