package com.berker.stayapi.controller;

import com.berker.stayapi.dto.ApiResponse;
import com.berker.stayapi.dto.ListingResponseDTO;
import com.berker.stayapi.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin-only endpoints")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/reports")
    @Operation(summary = "Report listings with ratings (ADMIN only, paged, sorted by rating desc)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Page<ListingResponseDTO>>> reportListings(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(ApiResponse.success("Report generated",
                adminService.reportListings(country, city, page)));
    }
}
