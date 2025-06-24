package com.example.hotel_property_view.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHotelDto {
  @NotBlank(message = "Hotel name cannot be blank")
  private String name;

  private String description;

  @NotBlank(message = "Brand cannot be blank")
  private String brand;

  @NotNull(message = "Address cannot be null")
  @Valid
  private AddressDto address;

  @NotNull(message = "Contacts cannot be null")
  @Valid
  private ContactsDto contacts;

  @NotNull(message = "Arrival time cannot be null")
  @Valid
  private ArrivalTimeDto arrivalTime;
}