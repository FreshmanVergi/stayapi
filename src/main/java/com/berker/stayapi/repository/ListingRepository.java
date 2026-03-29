package com.berker.stayapi.repository;

import com.berker.stayapi.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    @Query("""
        SELECT l FROM Listing l
        WHERE l.country = :country
          AND l.city = :city
          AND l.noOfPeople >= :noOfPeople
          AND l.id NOT IN (
              SELECT b.listing.id FROM Booking b
              WHERE b.dateFrom < :dateTo AND b.dateTo > :dateFrom
          )
    """)
    Page<Listing> findAvailableListings(
            @Param("country") String country,
            @Param("city") String city,
            @Param("noOfPeople") Integer noOfPeople,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable
    );

    @Query("SELECT l FROM Listing l WHERE (:country IS NULL OR l.country = :country) AND (:city IS NULL OR l.city = :city)")
    Page<Listing> findByCountryAndCity(
            @Param("country") String country,
            @Param("city") String city,
            Pageable pageable
    );
}
