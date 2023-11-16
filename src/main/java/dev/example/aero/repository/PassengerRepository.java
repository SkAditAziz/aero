package dev.example.aero.repository;

import dev.example.aero.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    @Query ("select p.password from Passenger p where p.contactNo =:contactNo")
    String findPasswordByContactNo(String contactNo);
    @Query ("select p.password from Passenger p where p.email =:email")
    String findPasswordByEmail(String email);
    Passenger findByContactNo(String contactNo);
    Passenger findByEmail(String email);
}
