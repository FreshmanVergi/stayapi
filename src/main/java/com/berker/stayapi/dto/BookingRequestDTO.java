package com.berker.stayapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BookingRequestDTO {
    @NotNull private Long listingId;
    @NotBlank private String dateFrom;
    @NotBlank private String dateTo;
    @NotNull private List<String> guestNames;

    public Long getListingId() { return listingId; }
    public void setListingId(Long l) { this.listingId = l; }
    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String d) { this.dateFrom = d; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String d) { this.dateTo = d; }
    public List<String> getGuestNames() { return guestNames; }
    public void setGuestNames(List<String> g) { this.guestNames = g; }
}
