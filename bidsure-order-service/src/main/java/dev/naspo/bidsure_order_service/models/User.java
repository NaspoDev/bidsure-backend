package dev.naspo.bidsure_order_service.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private int id;

    @NotEmpty
    @Size(max = 50)
    private String username;

    @NotEmpty
    private String password;

    @Email
    private String email;

    @NotEmpty
    @Size(max = 50)
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    private String lastName;

    @NotEmpty
    private String phoneNumber;
}
