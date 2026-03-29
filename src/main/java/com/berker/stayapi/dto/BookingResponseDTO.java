package com.berker.stayapi.dto;

import java.util.List;

public class BookingResponseDTO {
    private Long bookingId, listingId;
    private String status, listingTitle, dateFrom, dateTo;
    private List<String> guestNames;

    public BookingResponseDTO() {}

    public Long getBookingId() { return bookingId; }
    public Long getListingId() { return listingId; }
    public String getStatus() { return status; }
    public String getListingTitle() { return listingTitle; }
    public String getDateFrom() { return dateFrom; }
    public String getDateTo() { return dateTo; }
    public List<String> getGuestNames() { return guestNames; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final BookingResponseDTO d = new BookingResponseDTO();
        public Builder bookingId(Long id) { d.bookingId = id; return this; }
        public Builder listingId(Long id) { d.listingId = id; return this; }
        public Builder status(String s) { d.status = s; return this; }
        public Builder listingTitle(String t) { d.listingTitle = t; return this; }
        public Builder dateFrom(String df) { d.dateFrom = df; return this; }
        public Builder dateTo(String dt) { d.dateTo = dt; return this; }
        public Builder guestNames(List<String> g) { d.guestNames = g; return this; }
        public BookingResponseDTO build() { return d; }
    }
}
