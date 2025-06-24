package com.example.hotel_property_view.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelShortInfoDto {
  private Long id;
  private String name;
  private String description;
  private String address;
  private String phone;
}