package com.berker.stayapi.repository;

import com.berker.stayapi.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestId(Long guestId);

    Page<Booking> findByGuestId(Long guestId, Pageable pageable);

    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.listing.id = :listingId
          AND b.dateFrom < :dateTo
          AND b.dateTo > :dateFrom
    """)
    boolean existsOverlappingBooking(
            @Param("listingId") Long listingId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
    );

    Optional<Booking> findByIdAndGuestId(Long bookingId, Long guestId);
}
