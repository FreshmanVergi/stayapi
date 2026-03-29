package com.berker.stayapi.controller;

import com.berker.stayapi.dto.ApiResponse;
import com.berker.stayapi.dto.BookingRequestDTO;
import com.berker.stayapi.dto.BookingResponseDTO;
import com.berker.stayapi.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Stay booking endpoints")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Book a stay (GUEST only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<BookingResponseDTO>> bookStay(
            @Valid @RequestBody BookingRequestDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Stay booked successfully",
                bookingService.bookStay(dto, auth.getName())));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my bookings (GUEST only, paged)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Page<BookingResponseDTO>>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved",
                bookingService.getMyBookings(auth.getName(), page)));
    }
}
