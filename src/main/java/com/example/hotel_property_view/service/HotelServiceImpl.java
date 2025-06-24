package com.example.hotel_property_view.service;

import com.example.hotel_property_view.dto.CreateHotelDto;
import com.example.hotel_property_view.dto.HotelDetailedInfoDto;
import com.example.hotel_property_view.dto.HotelShortInfoDto;
import com.example.hotel_property_view.entity.Hotel;
import com.example.hotel_property_view.exception.ResourceNotFoundException;
import com.example.hotel_property_view.mapper.HotelMapper;
import com.example.hotel_property_view.repository.HotelRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;

  @Override
  @Transactional(readOnly = true)
  public List<HotelShortInfoDto> getAllHotels() {
    return hotelMapper.hotelsToHotelShortInfoDtos(hotelRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public HotelDetailedInfoDto getHotelById(Long id) {
    Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
    return hotelMapper.hotelToHotelDetailedInfoDto(hotel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<HotelShortInfoDto> searchHotels(String name, String brand, String city, String county, List<String> amenities) {
    Specification<Hotel> spec = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (StringUtils.hasText(name)) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
      }
      if (StringUtils.hasText(brand)) {
        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("brand")), brand.toLowerCase()));
      }
      if (StringUtils.hasText(city)) {
        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("address").get("city")), city.toLowerCase()));
      }
      if (StringUtils.hasText(county)) {
        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("address").get("county")), county.toLowerCase()));
      }

      if (!CollectionUtils.isEmpty(amenities)) {
        for (String amenity : amenities) {
          Subquery<Long> subquery = query.subquery(Long.class);
          Root<Hotel> subRoot = subquery.correlate(root);
          Join<Hotel, String> subAmenities = subRoot.join("amenities");
          subquery.select(criteriaBuilder.literal(1L))
                  .where(criteriaBuilder.equal(subAmenities, amenity));
          predicates.add(criteriaBuilder.exists(subquery));
        }
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
    List<Hotel> hotels = hotelRepository.findAll(spec);
    return hotelMapper.hotelsToHotelShortInfoDtos(hotels);
  }


  @Override
  @Transactional
  public HotelShortInfoDto createHotel(CreateHotelDto createHotelDto) {
    Hotel hotel = hotelMapper.createHotelDtoToHotel(createHotelDto);
    Hotel savedHotel = hotelRepository.save(hotel);
    return hotelMapper.hotelToHotelShortInfoDto(savedHotel);
  }

  @Override
  @Transactional
  public HotelDetailedInfoDto addAmenitiesToHotel(Long hotelId, Set<String> newAmenities) {
    Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
    hotel.getAmenities().addAll(newAmenities);

    Hotel updatedHotel = hotelRepository.save(hotel);
    return hotelMapper.hotelToHotelDetailedInfoDto(updatedHotel);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Long> getHistogram(String param) {
    List<Map<String, Object>> results;
    switch (param.toLowerCase()) {
      case "brand":
        results = hotelRepository.countHotelsByBrand();
        break;
      case "city":
        results = hotelRepository.countHotelsByCity();
        break;
      case "county":
        results = hotelRepository.countHotelsByCounty();
        break;
      case "amenities":
        results = hotelRepository.countHotelsByAmenity();
        break;
      default:
        throw new IllegalArgumentException("Invalid histogram parameter: " + param +
                ". Allowed values are: brand, city, county, amenities.");
    }
    return results.stream()
            .collect(Collectors.toMap(
                    map -> (String) map.get("item"),
                    map -> (Long) map.get("count")
            ));
  }
}