package com.example.hotel_property_view.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalTimeDto {
  @NotBlank(message = "Check-in time cannot be blank")
  @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Check-in time must be in HH:mm format")
  private String checkIn;

  @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Check-out time must be in HH:mm format")
  private String checkOut;
}