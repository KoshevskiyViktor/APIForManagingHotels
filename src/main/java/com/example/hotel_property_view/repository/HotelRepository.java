package com.example.hotel_property_view.repository;

import com.example.hotel_property_view.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

  @Query("SELECT h.brand as item, COUNT(h) as count FROM Hotel h GROUP BY h.brand")
  List<Map<String, Object>> countHotelsByBrand();


  @Query("SELECT h.address.city as item, COUNT(h) as count FROM Hotel h GROUP BY h.address.city")
  List<Map<String, Object>> countHotelsByCity();

  @Query("SELECT h.address.county as item, COUNT(h) as count FROM Hotel h GROUP BY h.address.county")
  List<Map<String, Object>> countHotelsByCounty();

  @Query("SELECT a as item, COUNT(h) as count FROM Hotel h JOIN h.amenities a GROUP BY a")
  List<Map<String, Object>> countHotelsByAmenity();
}