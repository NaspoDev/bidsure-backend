package dev.naspo.bidsure_user_service.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue
    private int id;

    private String username;

    private String password;

    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    // TODO: Is this valid for Hibernate? (Probably not)
    private Address address;
}
