package com.example.hotel_property_view.controller;

import com.example.hotel_property_view.dto.*;
import com.example.hotel_property_view.exception.GlobalExceptionHandler;
import com.example.hotel_property_view.exception.ResourceNotFoundException;
import com.example.hotel_property_view.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
@Import(GlobalExceptionHandler.class)
class HotelControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private HotelService hotelService;
  @Autowired
  private ObjectMapper objectMapper;

  private HotelShortInfoDto hotelShortDto;
  private HotelDetailedInfoDto hotelDetailedDto;
  private CreateHotelDto createHotelRequest;

  @BeforeEach
  void setUp() {
    hotelShortDto = new HotelShortInfoDto(1L, "Звезда", "Отель в центре", "ул. Главная, 1", "555-1234");
    AddressDto address = new AddressDto("1", "Главная", "Город X", "Страна Y", "001");
    ContactsDto contacts = new ContactsDto("555-1234", "zvezda@example.com");
    ArrivalTimeDto arrivalTime = new ArrivalTimeDto("15:00", "11:00");
    hotelDetailedDto = new HotelDetailedInfoDto(1L, "Звезда Подробно", "StarHotels", address, contacts, arrivalTime, Set.of("Парковка"));
    createHotelRequest = new CreateHotelDto("Комета", "Новый отель", "GalaxyGroup", address, contacts, arrivalTime);
  }

  @Test
  void shouldReturnListOfHotelsWhenGetAllHotels() throws Exception {
    when(hotelService.getAllHotels()).thenReturn(List.of(hotelShortDto));
    mockMvc.perform(get("/property-view/hotels"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name", is("Звезда")));
  }

  @Test
  void shouldReturnHotelDetailsWhenHotelExistsById() throws Exception {
    when(hotelService.getHotelById(1L)).thenReturn(hotelDetailedDto);
    mockMvc.perform(get("/property-view/hotels/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Звезда Подробно")));
  }

  @Test
  void shouldReturnNotFoundStatusWhenHotelNotExistsById() throws Exception {
    when(hotelService.getHotelById(99L)).thenThrow(new ResourceNotFoundException("Отель не найден"));
    mockMvc.perform(get("/property-view/hotels/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Отель не найден")));
  }

  @Test
  void shouldReturnCreatedStatusAndHotelWhenCreateHotelWithValidData() throws Exception {
    HotelShortInfoDto createdHotelShortDto = new HotelShortInfoDto(2L, "Комета", null, null, null);
    when(hotelService.createHotel(any(CreateHotelDto.class))).thenReturn(createdHotelShortDto);
    mockMvc.perform(post("/property-view/hotels")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createHotelRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is("Комета")))
            .andExpect(jsonPath("$.id", is(2)));
  }

  @Test
  void shouldReturnBadRequestWhenCreateHotelWithMissingName() throws Exception {
    CreateHotelDto invalidDto = new CreateHotelDto();
    invalidDto.setBrand("SomeBrand");
    mockMvc.perform(post("/property-view/hotels")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.name", is("Hotel name cannot be blank")));
  }

  @Test
  void shouldReturnBadRequestWhenCreateHotelWithInvalidEmailFormat() throws Exception {
    AddressDto address = new AddressDto("1", "Тестовая", "Город", "Страна", "12345");
    ContactsDto contactsWithInvalidEmail = new ContactsDto("1234567890", "невалидный_email");
    ArrivalTimeDto arrivalTime = new ArrivalTimeDto("14:00", "12:00");
    CreateHotelDto dtoWithInvalidEmail = new CreateHotelDto("Тестовый Отель", "Описание", "ТестБренд", address, contactsWithInvalidEmail, arrivalTime);
    mockMvc.perform(post("/property-view/hotels")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dtoWithInvalidEmail)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors['contacts.email']", is("Email should be valid")));
  }

  @Test
  void shouldReturnOkAndUpdateAmenitiesWhenAddAmenitiesToHotel() throws Exception {
    Set<String> amenitiesToAdd = Set.of("СПА");
    HotelDetailedInfoDto updatedHotel = new HotelDetailedInfoDto(
            hotelDetailedDto.getId(), hotelDetailedDto.getName(), hotelDetailedDto.getBrand(),
            hotelDetailedDto.getAddress(), hotelDetailedDto.getContacts(), hotelDetailedDto.getArrivalTime(),
            Set.of("Парковка", "СПА")
    );
    when(hotelService.addAmenitiesToHotel(eq(1L), eq(amenitiesToAdd))).thenReturn(updatedHotel);
    mockMvc.perform(post("/property-view/hotels/1/amenities")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(amenitiesToAdd)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amenities", hasItems("СПА", "Парковка")));
  }

  @Test
  void shouldReturnBadRequestMessageWhenAddAmenitiesToHotelWithEmptyList() throws Exception {
    when(hotelService.addAmenitiesToHotel(eq(1L), eq(Collections.emptySet())))
            .thenThrow(new IllegalArgumentException("Amenities list cannot be null or empty."));
    mockMvc.perform(post("/property-view/hotels/1/amenities")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Collections.emptySet())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Amenities list cannot be null or empty.")));
  }

  @Test
  void shouldReturnMatchingHotelsWhenSearchHotelsByCity() throws Exception {
    when(hotelService.searchHotels(isNull(), isNull(), eq("Город X"), isNull(), isNull()))
            .thenReturn(List.of(hotelShortDto));
    mockMvc.perform(get("/property-view/search").param("city", "Город X"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name", is("Звезда")));
  }

  @Test
  void shouldReturnHistogramDataWhenGetHistogramByCounty() throws Exception {
    when(hotelService.getHistogram("county")).thenReturn(Map.of("Страна Y", 3L));
    mockMvc.perform(get("/property-view/histogram/county"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.['Страна Y']", is(3)));
  }
}