package com.example.hotel_property_view.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "amenities")
@Entity
@Table(name = "hotels")
public class Hotel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;


  @Lob
  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String brand;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "houseNumber", column = @Column(name = "house_number", nullable = false)),
          @AttributeOverride(name = "street", column = @Column(name = "street", nullable = false)),
          @AttributeOverride(name = "city", column = @Column(name = "city", nullable = false)),
          @AttributeOverride(name = "county", column = @Column(name = "county", nullable = false)),
          @AttributeOverride(name = "postCode", column = @Column(name = "post_code", nullable = false))
  })
  private Address address;
  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "phone", column = @Column(name = "phone", nullable = false)),
          @AttributeOverride(name = "email", column = @Column(name = "email", nullable = false))
  })
  private Contacts contacts;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "checkIn", column = @Column(name = "check_in", nullable = false)),
          @AttributeOverride(name = "checkOut", column = @Column(name = "check_out" /*, nullable = true */))
  })
  private ArrivalTime arrivalTime;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"))
  @Column(name = "amenity")
  private Set<String> amenities = new HashSet<>();
}