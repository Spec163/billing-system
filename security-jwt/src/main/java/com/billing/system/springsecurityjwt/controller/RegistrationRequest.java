package com.billing.system.springsecurityjwt.controller;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ToString
public class RegistrationRequest {

    @NotBlank
    @Size(min = 3, max = 25)
    private String login;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(min = 3)
    private String phoneNumber;
}
