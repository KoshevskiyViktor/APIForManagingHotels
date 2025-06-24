package com.example.hotel_property_view.service;

import com.example.hotel_property_view.dto.CreateHotelDto;
import com.example.hotel_property_view.dto.HotelDetailedInfoDto;
import com.example.hotel_property_view.dto.HotelShortInfoDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface HotelService {
  List<HotelShortInfoDto> getAllHotels();
  HotelDetailedInfoDto getHotelById(Long id);
  List<HotelShortInfoDto> searchHotels(String name, String brand, String city, String county, List<String> amenities);
  HotelShortInfoDto createHotel(CreateHotelDto createHotelDto);
  HotelDetailedInfoDto addAmenitiesToHotel(Long hotelId, Set<String> amenities);
  Map<String, Long> getHistogram(String param);
}