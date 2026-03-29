package com.berker.stayapi.service;

import com.berker.stayapi.dto.ReviewRequestDTO;
import com.berker.stayapi.model.Booking;
import com.berker.stayapi.model.Review;
import com.berker.stayapi.model.User;
import com.berker.stayapi.repository.BookingRepository;
import com.berker.stayapi.repository.ListingRepository;
import com.berker.stayapi.repository.ReviewRepository;
import com.berker.stayapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookingRepository bookingRepository,
                         UserRepository userRepository,
                         ListingRepository listingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    @Transactional
    public void reviewStay(ReviewRequestDTO dto, String username) {
        User guest = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findByIdAndGuestId(dto.getStayId(), guest.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found or does not belong to you"));

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new RuntimeException("You have already reviewed this stay");
        }

        Review review = Review.builder()
                .booking(booking)
                .guest(guest)
                .listing(booking.getListing())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        reviewRepository.save(review);

        // Update listing average rating
        var listing = booking.getListing();
        double currentTotal = listing.getAverageRating() * listing.getReviewCount();
        int newCount = listing.getReviewCount() + 1;
        double newAverage = (currentTotal + dto.getRating()) / newCount;
        listing.setReviewCount(newCount);
        listing.setAverageRating(Math.round(newAverage * 10.0) / 10.0);
        listingRepository.save(listing);
    }
}
