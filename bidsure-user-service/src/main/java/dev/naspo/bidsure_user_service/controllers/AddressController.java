package dev.naspo.bidsure_user_service.controllers;

import dev.naspo.bidsure_user_service.models.Address;
import dev.naspo.bidsure_user_service.models.User;
import dev.naspo.bidsure_user_service.services.AddressService;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/addresses")
public class AddressController {

    @Autowired
    AddressService addressService;

    @PostMapping
    public ResponseEntity<Address> createAddress(@Valid @RequestBody Address address) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(address));
    }

    // Returns all addresses for the user.
    @GetMapping("/user-addresses/{userId}")
    public ResponseEntity<List<Address>> getUserAddresses(@PathVariable int userId) {
        return ResponseEntity.ok(addressService.getAllUserAddresses(userId));
    }

    // Update a specific address based on the id.
    @PutMapping("/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable int addressId, @Valid @RequestBody Address updatedAddress) {
        addressService.updateAddress(updatedAddress);
        return ResponseEntity.noContent().build();
    }

    // Delete a specific address.
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable int addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }
}
