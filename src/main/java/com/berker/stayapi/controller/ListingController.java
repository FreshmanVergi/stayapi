package com.berker.stayapi.controller;

import com.berker.stayapi.dto.ApiResponse;
import com.berker.stayapi.dto.ListingRequestDTO;
import com.berker.stayapi.dto.ListingResponseDTO;
import com.berker.stayapi.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/listings")
@Tag(name = "Listings", description = "Listing management endpoints")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    @Operation(summary = "Insert a new listing (HOST only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ListingResponseDTO>> insertListing(
            @Valid @RequestBody ListingRequestDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Listing created successfully",
                listingService.insertListing(dto, auth.getName())));
    }

    @PostMapping("/upload")
    @Operation(summary = "Bulk insert listings via CSV (HOST only)",
               security = @SecurityRequirement(name = "bearerAuth"),
               description = "CSV columns (with header row): noOfPeople, country, city, price, title, description")
    public ResponseEntity<ApiResponse<List<ListingResponseDTO>>> insertByFile(
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        List<ListingResponseDTO> results = listingService.insertListingByFile(file, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(results.size() + " listings created", results));
    }

    @GetMapping
    @Operation(summary = "Query available listings",
               description = "Returns listings not booked in the given date range. " +
                             "Limited to 3 calls per day per user (or per IP if not logged in).")
    public ResponseEntity<ApiResponse<Page<ListingResponseDTO>>> queryListings(
            @RequestParam String dateFrom,
            @RequestParam String dateTo,
            @RequestParam Integer noOfPeople,
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam(defaultValue = "0") int page,
            Authentication auth,
            HttpServletRequest request) {

        // Use username for rate limiting if authenticated, otherwise use IP
        String identifier = (auth != null) ? auth.getName() : getClientIp(request);

        Page<ListingResponseDTO> results = listingService.queryListings(
                dateFrom, dateTo, noOfPeople, country, city, page, identifier);

        return ResponseEntity.ok(ApiResponse.success("Listings retrieved", results));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
