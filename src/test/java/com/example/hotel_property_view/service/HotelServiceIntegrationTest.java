package com.example.hotel_property_view.service;

import com.example.hotel_property_view.dto.*;
import com.example.hotel_property_view.repository.HotelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotelServiceIntegrationTest {

  @Autowired
  private HotelService hotelService;

  @Autowired
  private HotelRepository hotelRepository;

  @BeforeEach
  void setUpDatabase() {
    hotelRepository.deleteAll();
  }

  @AfterEach
  void tearDownDatabase() {
    hotelRepository.deleteAll();
  }

  private CreateHotelDto createSampleHotelDto(String name, String city, String brand) {
    AddressDto address = new AddressDto("1", "Улица " + name, city, "Страна", "12345");
    ContactsDto contacts = new ContactsDto("000-" + name.length(), name.toLowerCase().replace(" ", "") + "@example.com");
    ArrivalTimeDto arrival = new ArrivalTimeDto("14:00", "12:00");
    return new CreateHotelDto(name, "Описание для " + name, brand, address, contacts, arrival);
  }

  @Test
  void shouldCreateHotelAndRetrieveById() {
    CreateHotelDto hotelParadiseDto = createSampleHotelDto("Рай", "Сочи", "ParadiseGroup");
    HotelShortInfoDto createdHotel = hotelService.createHotel(hotelParadiseDto);
    assertNotNull(createdHotel.getId());
    assertEquals("Рай", createdHotel.getName());
    HotelDetailedInfoDto fetchedHotel = hotelService.getHotelById(createdHotel.getId());
    assertEquals("Рай", fetchedHotel.getName());
    assertEquals("ParadiseGroup", fetchedHotel.getBrand());
    assertEquals("Сочи", fetchedHotel.getAddress().getCity());
    assertTrue(fetchedHotel.getAmenities().isEmpty());
  }

  @Test
  void shouldGetAllHotelsWhenMultipleHotelsExist() {
    hotelService.createHotel(createSampleHotelDto("Отель Альфа", "Москва", "Alpha Hotels"));
    hotelService.createHotel(createSampleHotelDto("Отель Бета", "Санкт-Петербург", "Beta Resorts"));
    List<HotelShortInfoDto> allHotels = hotelService.getAllHotels();
    assertEquals(2, allHotels.size());
  }

  @Test
  void shouldAddAndRetrieveAmenities() {
    HotelShortInfoDto createdHotel = hotelService.createHotel(createSampleHotelDto("Отель Удобный", "Киев", "Comfort Inn"));
    Long hotelId = createdHotel.getId();
    Set<String> amenitiesToAdd = Set.of("Бассейн", "Спортзал", "WiFi бесплатный");
    hotelService.addAmenitiesToHotel(hotelId, amenitiesToAdd);
    HotelDetailedInfoDto fetchedHotel = hotelService.getHotelById(hotelId);
    assertEquals(3, fetchedHotel.getAmenities().size());
    assertTrue(fetchedHotel.getAmenities().contains("Спортзал"));
    assertTrue(fetchedHotel.getAmenities().contains("WiFi бесплатный"));
  }

  @Test
  void shouldSearchHotelsByCityAndAmenity() {
    HotelShortInfoDto hotelMinskWifi = hotelService.createHotel(createSampleHotelDto("Минск Плаза", "Минск", "Plaza"));
    hotelService.addAmenitiesToHotel(hotelMinskWifi.getId(), Set.of("WiFi", "Конференц-зал"));
    HotelShortInfoDto hotelMinskPool = hotelService.createHotel(createSampleHotelDto("Минск Аква", "Минск", "Aqua"));
    hotelService.addAmenitiesToHotel(hotelMinskPool.getId(), Set.of("Бассейн", "WiFi"));
    HotelShortInfoDto hotelGomel = hotelService.createHotel(createSampleHotelDto("Гомель Люкс", "Гомель", "Luxury"));
    hotelService.addAmenitiesToHotel(hotelGomel.getId(), Set.of("WiFi"));

    List<HotelShortInfoDto> minskHotelsWithWifi = hotelService.searchHotels(null, null, "Минск", null, List.of("WiFi"));
    assertEquals(2, minskHotelsWithWifi.size());
    List<HotelShortInfoDto> hotelsWithPool = hotelService.searchHotels(null, null, null, null, List.of("Бассейн"));
    assertEquals(1, hotelsWithPool.size());
    assertEquals("Минск Аква", hotelsWithPool.get(0).getName());
    List<HotelShortInfoDto> minskHotelsWithConfAndWifi = hotelService.searchHotels(null, null, "Минск", null, List.of("WiFi", "Конференц-зал"));
    assertEquals(1, minskHotelsWithConfAndWifi.size());
    assertEquals("Минск Плаза", minskHotelsWithConfAndWifi.get(0).getName());
  }

  @Test
  void shouldGetCorrectHistogramData() {
    HotelShortInfoDto h1 = hotelService.createHotel(createSampleHotelDto("Отель 1", "Город X", "Бренд A"));
    hotelService.addAmenitiesToHotel(h1.getId(), Set.of("P1"));
    HotelShortInfoDto h2 = hotelService.createHotel(createSampleHotelDto("Отель 2", "Город Y", "Бренд B"));
    hotelService.addAmenitiesToHotel(h2.getId(), Set.of("P1", "P2"));
    HotelShortInfoDto h3 = hotelService.createHotel(createSampleHotelDto("Отель 3", "Город X", "Бренд A"));
    hotelService.addAmenitiesToHotel(h3.getId(), Set.of("P2"));

    Map<String, Long> brandHistogram = hotelService.getHistogram("brand");
    assertEquals(2, brandHistogram.get("Бренд A"));
    assertEquals(1, brandHistogram.get("Бренд B"));
    Map<String, Long> cityHistogram = hotelService.getHistogram("city");
    assertEquals(2, cityHistogram.get("Город X"));
    assertEquals(1, cityHistogram.get("Город Y"));
    Map<String, Long> amenityHistogram = hotelService.getHistogram("amenities");
    assertEquals(2, amenityHistogram.get("P1"));
    assertEquals(2, amenityHistogram.get("P2"));
  }
}