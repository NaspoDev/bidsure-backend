package dev.naspo.bidsure_user_service.services;

import dev.naspo.bidsure_user_service.models.Address;
import dev.naspo.bidsure_user_service.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Responsible for Address related business logic.
@Service
public class AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Transactional
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    @Transactional
    public List<Address> getAllUserAddresses(int userId) {
        return addressRepository.findAllUserAddresses(userId);
    }

    @Transactional
    public void updateAddress(Address address) {
        addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(int addressId) {
        addressRepository.deleteById(addressId);
    }
}
