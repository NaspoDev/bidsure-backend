package dev.naspo.bidsure_user_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;

    @NotEmpty
    @Column(name = "phone_number")
    private String phoneNumber;
}
