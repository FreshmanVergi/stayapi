package com.berker.stayapi.service;

import com.berker.stayapi.dto.ListingRequestDTO;
import com.berker.stayapi.dto.ListingResponseDTO;
import com.berker.stayapi.model.Listing;
import com.berker.stayapi.model.QueryRateLimit;
import com.berker.stayapi.model.User;
import com.berker.stayapi.repository.ListingRepository;
import com.berker.stayapi.repository.QueryRateLimitRepository;
import com.berker.stayapi.repository.UserRepository;
import com.opencsv.CSVReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final QueryRateLimitRepository rateLimitRepository;

    private static final int PAGE_SIZE = 10;
    private static final int DAILY_QUERY_LIMIT = 3;

    public ListingService(ListingRepository listingRepository,
                          UserRepository userRepository,
                          QueryRateLimitRepository rateLimitRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.rateLimitRepository = rateLimitRepository;
    }

    @Transactional
    public ListingResponseDTO insertListing(ListingRequestDTO dto, String username) {
        User host = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Listing listing = Listing.builder()
                .host(host)
                .noOfPeople(dto.getNoOfPeople())
                .country(dto.getCountry())
                .city(dto.getCity())
                .price(dto.getPrice())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        return mapToResponse(listingRepository.save(listing));
    }

    @Transactional
    public List<ListingResponseDTO> insertListingByFile(MultipartFile file, String username) {
        User host = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ListingResponseDTO> results = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            boolean firstLine = true;
            while ((line = reader.readNext()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.length < 5) continue;

                Listing listing = Listing.builder()
                        .host(host)
                        .noOfPeople(Integer.parseInt(line[0].trim()))
                        .country(line[1].trim())
                        .city(line[2].trim())
                        .price(Double.parseDouble(line[3].trim()))
                        .title(line[4].trim())
                        .description(line.length > 5 ? line[5].trim() : "")
                        .build();

                results.add(mapToResponse(listingRepository.save(listing)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }

        return results;
    }

    @Transactional
    public Page<ListingResponseDTO> queryListings(
            String dateFrom, String dateTo,
            Integer noOfPeople, String country, String city,
            int page, String identifier) {

        // Use username if provided (authenticated), else fallback to IP
        checkRateLimit(identifier);

        LocalDate from = LocalDate.parse(dateFrom);
        LocalDate to = LocalDate.parse(dateTo);

        if (!from.isBefore(to)) {
            throw new RuntimeException("dateFrom must be before dateTo");
        }

        Page<Listing> listings = listingRepository.findAvailableListings(
                country, city, noOfPeople, from, to,
                PageRequest.of(page, PAGE_SIZE)
        );

        return listings.map(this::mapToResponse);
    }

    private void checkRateLimit(String identifier) {
        LocalDate today = LocalDate.now();
        QueryRateLimit rateLimit = rateLimitRepository
                .findByIdentifierAndQueryDate(identifier, today)
                .orElseGet(() -> {
                    QueryRateLimit r = QueryRateLimit.builder()
                            .identifier(identifier)
                            .queryDate(today)
                            .callCount(0)
                            .build();
                    return rateLimitRepository.save(r);
                });

        if (rateLimit.getCallCount() >= DAILY_QUERY_LIMIT) {
            throw new RuntimeException("Daily query limit of " + DAILY_QUERY_LIMIT + " reached. Try again tomorrow.");
        }

        rateLimit.setCallCount(rateLimit.getCallCount() + 1);
        rateLimitRepository.save(rateLimit);
    }

    private ListingResponseDTO mapToResponse(Listing listing) {
        return ListingResponseDTO.builder()
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
                .build();
    }
}
