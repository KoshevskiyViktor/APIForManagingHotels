package com.example.hotel_property_view.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDetailedInfoDto {
  private Long id;
  private String name;
  private String brand;
  private AddressDto address;
  private ContactsDto contacts;
  private ArrivalTimeDto arrivalTime;
  private Set<String> amenities;
}