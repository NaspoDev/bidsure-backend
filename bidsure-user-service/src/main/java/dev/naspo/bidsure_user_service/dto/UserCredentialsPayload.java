package dev.naspo.bidsure_user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

// The user credentials payload received upon login request.
@Getter
public class UserCredentialsPayload {
    @Email
    private String email;

    @NotEmpty
    private String password;
}
