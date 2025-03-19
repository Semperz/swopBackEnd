package edu.badpals.swopbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private String email;
    private String password;
    private String fullName;
    private String billingAddress;
    private String defaultShippingAddress;
    private String country;
    private String phone;

}