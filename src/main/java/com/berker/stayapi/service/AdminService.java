package com.berker.stayapi.service;

import com.berker.stayapi.dto.ListingResponseDTO;
import com.berker.stayapi.model.Listing;
import com.berker.stayapi.repository.ListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final ListingRepository listingRepository;
    private static final int PAGE_SIZE = 10;

    public AdminService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Page<ListingResponseDTO> reportListings(String country, String city, int page) {
        Page<Listing> listings = listingRepository.findByCountryAndCity(
                country, city,
                PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "averageRating"))
        );

        return listings.map(listing -> ListingResponseDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .country(listing.getCountry())
                .city(listing.getCity())
                .noOfPeople(listing.getNoOfPeople())
                .price(listing.getPrice())
                .averageRating(listing.getAverageRating())
                .reviewCount(listing.getReviewCount())
                .hostUsername(listing.getHost().getUsername())
                .build());
    }
}
