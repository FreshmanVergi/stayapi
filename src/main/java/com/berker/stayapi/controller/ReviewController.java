package com.berker.stayapi.controller;

import com.berker.stayapi.dto.ApiResponse;
import com.berker.stayapi.dto.ReviewRequestDTO;
import com.berker.stayapi.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Stay review endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Review a stay (GUEST only, must have a booking)",
               security = @SecurityRequirement(name = "bearerAuth"),
               description = "Only guests who booked a stay can review it. One review per booking. Rating 1-5.")
    public ResponseEntity<ApiResponse<Void>> reviewStay(
            @Valid @RequestBody ReviewRequestDTO dto,
            Authentication auth) {
        reviewService.reviewStay(dto, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", null));
    }
}
