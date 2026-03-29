package com.berker.stayapi.service;

import com.berker.stayapi.dto.BookingRequestDTO;
import com.berker.stayapi.dto.BookingResponseDTO;
import com.berker.stayapi.model.Booking;
import com.berker.stayapi.model.Listing;
import com.berker.stayapi.model.User;
import com.berker.stayapi.repository.BookingRepository;
import com.berker.stayapi.repository.ListingRepository;
import com.berker.stayapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    private static final int PAGE_SIZE = 10;

    public BookingService(BookingRepository bookingRepository,
                          ListingRepository listingRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponseDTO bookStay(BookingRequestDTO dto, String username) {
        User guest = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Listing listing = listingRepository.findById(dto.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + dto.getListingId()));

        LocalDate from = LocalDate.parse(dto.getDateFrom());
        LocalDate to = LocalDate.parse(dto.getDateTo());

        if (!from.isBefore(to)) {
            throw new RuntimeException("dateFrom must be before dateTo");
        }

        if (dto.getGuestNames() == null || dto.getGuestNames().isEmpty()) {
            throw new RuntimeException("At least one guest name is required");
        }

        if (dto.getGuestNames().size() > listing.getNoOfPeople()) {
            throw new RuntimeException("Number of guests (" + dto.getGuestNames().size()
                    + ") exceeds listing capacity of " + listing.getNoOfPeople());
        }

        boolean overlapping = bookingRepository.existsOverlappingBooking(listing.getId(), from, to);
        if (overlapping) {
            throw new RuntimeException("Listing is already booked for the selected dates");
        }

        Booking booking = Booking.builder()
                .listing(listing)
                .guest(guest)
                .dateFrom(from)
                .dateTo(to)
                .guestNames(dto.getGuestNames())
                .build();

        Booking saved = bookingRepository.save(booking);

        return BookingResponseDTO.builder()
                .bookingId(saved.getId())
                .status("Successful")
                .listingId(listing.getId())
                .listingTitle(listing.getTitle())
                .dateFrom(saved.getDateFrom().toString())
                .dateTo(saved.getDateTo().toString())
                .guestNames(saved.getGuestNames())
                .build();
    }

    // NEW: list my bookings (paged)
    public Page<BookingResponseDTO> getMyBookings(String username, int page) {
        User guest = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByGuestId(guest.getId(), PageRequest.of(page, PAGE_SIZE))
                .map(b -> BookingResponseDTO.builder()
                        .bookingId(b.getId())
                        .status("Confirmed")
                        .listingId(b.getListing().getId())
                        .listingTitle(b.getListing().getTitle())
                        .dateFrom(b.getDateFrom().toString())
                        .dateTo(b.getDateTo().toString())
                        .guestNames(b.getGuestNames())
                        .build());
    }
}
