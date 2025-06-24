package com.example.hotel_property_view.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Contacts {
  @NotBlank(message = "Phone cannot be blank")
  private String phone;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email should be valid")
  private String email;
}