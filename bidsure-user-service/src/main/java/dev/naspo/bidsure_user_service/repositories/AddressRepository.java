package dev.naspo.bidsure_user_service.repositories;

import dev.naspo.bidsure_user_service.models.Address;
import dev.naspo.bidsure_user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("select a from Address a where a.userId = ?1")
    List<Address> findAllUserAddresses(int userId);
}
