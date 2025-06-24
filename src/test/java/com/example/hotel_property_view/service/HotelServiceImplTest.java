package com.example.hotel_property_view.service;

import com.example.hotel_property_view.dto.*;
import com.example.hotel_property_view.entity.Address;
import com.example.hotel_property_view.entity.ArrivalTime;
import com.example.hotel_property_view.entity.Contacts;
import com.example.hotel_property_view.entity.Hotel;
import com.example.hotel_property_view.exception.ResourceNotFoundException;
import com.example.hotel_property_view.mapper.HotelMapper;
import com.example.hotel_property_view.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

  @Mock
  private HotelRepository hotelRepository;
  @Mock
  private HotelMapper hotelMapper;
  @InjectMocks
  private HotelServiceImpl hotelService;

  private Hotel hotelEntity1;
  private HotelShortInfoDto hotelShortDto1;
  private HotelDetailedInfoDto hotelDetailedDto1;
  private CreateHotelDto createHotelRequest;

  @BeforeEach
  void setUp() {
    Address address = new Address("10", "Солнечная", "Минск", "Беларусь", "220000");
    Contacts contacts = new Contacts("111-222-333", "sunhotel@example.com");
    ArrivalTime arrivalTime = new ArrivalTime("14:00", "12:00");
    hotelEntity1 = new Hotel(1L, "Отель Солнечный", "Уютный отель в центре города.", "SunGroup", address, contacts, arrivalTime, new HashSet<>(Set.of("WiFi", "Завтрак")));
    hotelShortDto1 = new HotelShortInfoDto(1L, "Отель Солнечный", "Уютный отель в центре города.", "10 Солнечная, Минск, 220000, Беларусь", "111-222-333");
    AddressDto addressDto = new AddressDto("10", "Солнечная", "Минск", "Беларусь", "220000");
    ContactsDto contactsDto = new ContactsDto("111-222-333", "sunhotel@example.com");
    ArrivalTimeDto arrivalTimeDto = new ArrivalTimeDto("14:00", "12:00");
    hotelDetailedDto1 = new HotelDetailedInfoDto(1L, "Отель Солнечный", "SunGroup", addressDto, contactsDto, arrivalTimeDto, new HashSet<>(Set.of("WiFi", "Завтрак")));
    createHotelRequest = new CreateHotelDto("Отель Лунный", "Тихий отель для отдыха.", "Moonlight Hotels", addressDto, contactsDto, arrivalTimeDto);
  }

  @Test
  void shouldReturnListOfHotelShortInfoWhenGetAllHotels() {
    when(hotelRepository.findAll()).thenReturn(List.of(hotelEntity1));
    when(hotelMapper.hotelsToHotelShortInfoDtos(List.of(hotelEntity1))).thenReturn(List.of(hotelShortDto1));
    List<HotelShortInfoDto> result = hotelService.getAllHotels();
    assertFalse(result.isEmpty());
    assertEquals("Отель Солнечный", result.get(0).getName());
    verify(hotelRepository).findAll();
  }

  @Test
  void shouldReturnDetailedInfoWhenHotelExistsById() {
    when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotelEntity1));
    when(hotelMapper.hotelToHotelDetailedInfoDto(hotelEntity1)).thenReturn(hotelDetailedDto1);
    HotelDetailedInfoDto result = hotelService.getHotelById(1L);
    assertEquals("Отель Солнечный", result.getName());
    verify(hotelRepository).findById(1L);
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenHotelNotExistsById() {
    when(hotelRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> hotelService.getHotelById(99L));
    verify(hotelRepository).findById(99L);
  }

  @Test
  void shouldReturnCreatedHotelShortInfoWhenCreateHotel() {
    Hotel newHotelEntity = new Hotel();
    Hotel savedHotelEntity = new Hotel(2L, "Отель Лунный", null, null, null, null, null, null);
    when(hotelMapper.createHotelDtoToHotel(createHotelRequest)).thenReturn(newHotelEntity);
    when(hotelRepository.save(newHotelEntity)).thenReturn(savedHotelEntity);
    HotelShortInfoDto expectedShortDto = new HotelShortInfoDto(2L, "Отель Лунный", null, null, null);
    when(hotelMapper.hotelToHotelShortInfoDto(savedHotelEntity)).thenReturn(expectedShortDto);
    HotelShortInfoDto result = hotelService.createHotel(createHotelRequest);
    assertEquals("Отель Лунный", result.getName());
    assertEquals(2L, result.getId());
    verify(hotelRepository).save(newHotelEntity);
  }

  @Test
  void shouldAddAmenitiesWhenHotelExists() {
    Set<String> newAmenities = Set.of("Бассейн");
    hotelEntity1.getAmenities().clear();
    hotelEntity1.getAmenities().add("WiFi");
    when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotelEntity1));
    when(hotelRepository.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));
    Set<String> combinedAmenities = new HashSet<>(hotelEntity1.getAmenities());
    combinedAmenities.addAll(newAmenities);
    HotelDetailedInfoDto updatedDetailedDto = new HotelDetailedInfoDto(
            hotelDetailedDto1.getId(), hotelDetailedDto1.getName(), hotelDetailedDto1.getBrand(),
            hotelDetailedDto1.getAddress(), hotelDetailedDto1.getContacts(), hotelDetailedDto1.getArrivalTime(),
            combinedAmenities
    );
    when(hotelMapper.hotelToHotelDetailedInfoDto(any(Hotel.class))).thenReturn(updatedDetailedDto);
    HotelDetailedInfoDto result = hotelService.addAmenitiesToHotel(1L, newAmenities);
    assertTrue(result.getAmenities().contains("Бассейн"));
    assertTrue(result.getAmenities().contains("WiFi"));
    assertEquals(2, result.getAmenities().size());
    verify(hotelRepository).save(any(Hotel.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnMatchingListWhenSearchHotelsByName() {
    when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotelEntity1));
    when(hotelMapper.hotelsToHotelShortInfoDtos(List.of(hotelEntity1))).thenReturn(List.of(hotelShortDto1));
    List<HotelShortInfoDto> result = hotelService.searchHotels("Солнечный", null, null, null, null);
    assertFalse(result.isEmpty());
    assertEquals("Отель Солнечный", result.get(0).getName());
    verify(hotelRepository).findAll(any(Specification.class));
  }

  @Test
  void shouldCallCorrectRepositoryMethodAndTransformWhenGetHistogramForCity() {
    List<Map<String, Object>> repoResult = List.of(Map.of("item", "Минск", "count", 5L));
    when(hotelRepository.countHotelsByCity()).thenReturn(repoResult);
    Map<String, Long> result = hotelService.getHistogram("city");
    assertEquals(5L, result.get("Минск"));
    verify(hotelRepository).countHotelsByCity();
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenGetHistogramForInvalidParameter() {
    assertThrows(IllegalArgumentException.class, () -> hotelService.getHistogram("неверный_параметр"));
  }
}