package com.example.hotel_property_view.controller;

import com.example.hotel_property_view.dto.CreateHotelDto;
import com.example.hotel_property_view.dto.HotelDetailedInfoDto;
import com.example.hotel_property_view.dto.HotelShortInfoDto;
import com.example.hotel_property_view.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/property-view")
@RequiredArgsConstructor
@Tag(name = "Hotel API", description = "API for managing hotel properties")
public class HotelController {

  private final HotelService hotelService;

  @Operation(summary = "Get list of all hotels (short info)", responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = HotelShortInfoDto.class)))
  })
  @GetMapping("/hotels")
  public ResponseEntity<List<HotelShortInfoDto>> getAllHotels() {
    return ResponseEntity.ok(hotelService.getAllHotels());
  }

  @Operation(summary = "Get detailed information for a specific hotel by ID", responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved hotel details",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = HotelDetailedInfoDto.class))),
          @ApiResponse(responseCode = "404", description = "Hotel not found",
                  content = @Content)
  })
  @GetMapping("/hotels/{id}")
  public ResponseEntity<HotelDetailedInfoDto> getHotelById(
          @Parameter(description = "ID of the hotel to be retrieved", required = true)
          @PathVariable Long id) {
    return ResponseEntity.ok(hotelService.getHotelById(id));
  }

  @Operation(summary = "Search for hotels based on criteria (short info)", responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved list of matching hotels",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = HotelShortInfoDto.class)))
  })
  @GetMapping("/search")
  public ResponseEntity<List<HotelShortInfoDto>> searchHotels(
          @Parameter(description = "Filter by hotel name (partial match, case-insensitive)") @RequestParam(required = false) String name,
          @Parameter(description = "Filter by hotel brand (exact match, case-insensitive)") @RequestParam(required = false) String brand,
          @Parameter(description = "Filter by city (exact match, case-insensitive)") @RequestParam(required = false) String city,
          @Parameter(description = "Filter by county (exact match, case-insensitive)") @RequestParam(required = false) String county,
          @Parameter(description = "Filter by amenities (hotel must have ALL specified amenities)") @RequestParam(required = false) List<String> amenities
  ) {
    return ResponseEntity.ok(hotelService.searchHotels(name, brand, city, county, amenities));
  }

  @Operation(summary = "Create a new hotel", responses = {
          @ApiResponse(responseCode = "201", description = "Hotel created successfully",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = HotelShortInfoDto.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input data",
                  content = @Content)
  })
  @PostMapping("/hotels")
  public ResponseEntity<HotelShortInfoDto> createHotel(@Valid @RequestBody CreateHotelDto createHotelDto) {
    HotelShortInfoDto createdHotel = hotelService.createHotel(createHotelDto);
    return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
  }

  @Operation(summary = "Add a list of amenities to a specific hotel", responses = {
          @ApiResponse(responseCode = "200", description = "Amenities added successfully, returns updated hotel details",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = HotelDetailedInfoDto.class))),
          @ApiResponse(responseCode = "404", description = "Hotel not found",
                  content = @Content),
          @ApiResponse(responseCode = "400", description = "Invalid input (e.g., empty list of amenities)",
                  content = @Content)
  })
  @PostMapping("/hotels/{id}/amenities")
  public ResponseEntity<HotelDetailedInfoDto> addAmenitiesToHotel(
          @Parameter(description = "ID of the hotel to add amenities to", required = true) @PathVariable Long id,
          @Parameter(description = "List of amenities to add", required = true,
                  schema = @Schema(type = "array", example = "[\"Free parking\", \"Free WiFi\"]"))
          @RequestBody Set<String> amenities
  ) {
    if (amenities == null || amenities.isEmpty()) {
      throw new IllegalArgumentException("Amenities list cannot be null or empty.");
    }
    HotelDetailedInfoDto updatedHotel = hotelService.addAmenitiesToHotel(id, amenities);
    return ResponseEntity.ok(updatedHotel);
  }

  @Operation(summary = "Get a histogram of hotels grouped by a specified parameter", responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved histogram data"),
          @ApiResponse(responseCode = "400", description = "Invalid histogram parameter",
                  content = @Content)
  })
  @GetMapping("/histogram/{param}")
  public ResponseEntity<Map<String, Long>> getHistogram(
          @Parameter(description = "Parameter to group by. Allowed values: brand, city, county, amenities",
                  required = true, example = "city")
          @PathVariable String param
  ) {
    return ResponseEntity.ok(hotelService.getHistogram(param));
  }
}