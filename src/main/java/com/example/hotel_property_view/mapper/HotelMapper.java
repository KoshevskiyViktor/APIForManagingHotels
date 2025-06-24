package com.example.hotel_property_view.mapper;

import com.example.hotel_property_view.dto.*;
import com.example.hotel_property_view.entity.Address;
import com.example.hotel_property_view.entity.ArrivalTime;
import com.example.hotel_property_view.entity.Contacts;
import com.example.hotel_property_view.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface HotelMapper {

  HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);


  AddressDto addressToAddressDto(Address address);
  Address addressDtoToAddress(AddressDto addressDto);

  ContactsDto contactsToContactsDto(Contacts contacts);
  Contacts contactsDtoToContacts(ContactsDto contactsDto);

  ArrivalTimeDto arrivalTimeToArrivalTimeDto(ArrivalTime arrivalTime);
  ArrivalTime arrivalTimeDtoToArrivalTime(ArrivalTimeDto arrivalTimeDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "amenities", ignore = true)
  Hotel createHotelDtoToHotel(CreateHotelDto createHotelDto);

  @Mapping(source = "address", target = "address", qualifiedByName = "formatAddressToString")
  @Mapping(source = "contacts.phone", target = "phone")
  HotelShortInfoDto hotelToHotelShortInfoDto(Hotel hotel);

  List<HotelShortInfoDto> hotelsToHotelShortInfoDtos(List<Hotel> hotels);
  Set<HotelShortInfoDto> hotelsToHotelShortInfoDtos(Set<Hotel> hotels);

  @Mapping(source = "address", target = "address")
  @Mapping(source = "contacts", target = "contacts")
  @Mapping(source = "arrivalTime", target = "arrivalTime")
  @Mapping(source = "amenities", target = "amenities")
  HotelDetailedInfoDto hotelToHotelDetailedInfoDto(Hotel hotel);


  @Named("formatAddressToString")
  default String formatAddressToString(Address address) {
    if (address == null) {
      return null;
    }
    return String.format("%s %s, %s, %s, %s",
            address.getHouseNumber(),
            address.getStreet(),
            address.getCity(),
            address.getPostCode(),
            address.getCounty());
  }
}