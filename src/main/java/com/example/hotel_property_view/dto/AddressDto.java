package com.example.hotel_property_view.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
  @NotBlank(message = "House number cannot be blank")
  private String houseNumber;

  @NotBlank(message = "Street cannot be blank")
  private String street;

  @NotBlank(message = "City cannot be blank")
  private String city;

  @NotBlank(message = "County cannot be blank")
  private String county;

  @NotBlank(message = "Post code cannot be blank")
  private String postCode;
}