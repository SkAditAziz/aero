package dev.example.aero.repository;

import dev.example.aero.model.Flight;
import dev.example.aero.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    @Query("select p.password from Passenger p where p.contactNo =:contactNo")
    String findPasswordByContactNo(String contactNo);
    @Query("select p.password from Passenger p where p.email =:email")
    String findPasswordByEmail(String email);
    Passenger findByContactNo(String contactNo);
    Passenger findByEmail(String email);
    @Modifying
    @Query("UPDATE Passenger p SET p.distanceFlied = p.distanceFlied + (SELECT f.distance FROM Flight f WHERE f=:flight) WHERE p=:passenger")
    void addDistanceFlied(Passenger passenger, Flight flight);
    @Query("SELECT p.id FROM Passenger p WHERE p.email=:email")
    int getIdByEmail(String email);
    @Query("SELECT p.email FROM Passenger p WHERE p.contactNo=:contactNo")
    String findEmailByContactNo(String contactNo);

    @Query("SELECT p.lastName from Passenger p WHERE p.contactNo = :username OR p.email = :username")
    String getLastNameByContactOrEmail(String username);
    @Query("SELECT p from Passenger p WHERE p.contactNo = :currentUsername OR p.email = :currentUsername")
    Passenger findByUsername(String currentUsername);
}
