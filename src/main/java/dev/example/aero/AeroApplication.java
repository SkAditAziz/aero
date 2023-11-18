package dev.example.aero;

import dev.example.aero.model.*;
import dev.example.aero.model.Enumaration.TicketStatus;
import dev.example.aero.repository.*;
import dev.example.aero.model.Enumaration.SeatClassType;
import org.hibernate.Hibernate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AeroApplication {

	public static void main(String[] args) {
		SpringApplication.run(AeroApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(String[] args, AirportRepository airportRepository, FlightRepository flightRepository,
											   FlightScheduleRepository flightScheduleRepository, PassengerRepository passengerRepository,
												TicketRepository ticketRepository){
		return runner -> {
			System.out.println("hello Aero!");

//			FlightSchedule fs = flightScheduleRepository.findByFlightDate(LocalDate.parse("2023-12-01"));
//
//			/*	The error you're encountering, "failed to lazily initialize a collection," typically occurs when you try to access a lazily-loaded collection outside the scope of an active Hibernate session. In your case, it seems that the FlightSchedule entity has a lazily-loaded collection (flights), and when you're trying to access it outside the method where it was fetched, Hibernate is unable to initialize the collection.*/
//			//Hibernate.initialize(fs.getFlights());
//
//			Flight desiredFlight = fs.getFlights().stream()
//					.filter(flight -> flight.getId().equals("DAC-CXB-002"))
//					.findFirst()
//					.orElse(null);
//			if(desiredFlight == null)
//				System.out.println("desiredFlight == null");
//			Flight modelFlight = flightRepository.findById("DAC-CXB-002").orElse(null);
//			if(modelFlight == null)
//				System.out.println("modelFlight == null");
//			System.out.println(desiredFlight.equals(modelFlight));
//			System.out.println("-------------");
//			System.out.println(desiredFlight == modelFlight);

//  Adding Ticket
//			Flight f = flightRepository.findById("CXB-DAC-002").orElse(null);
//			Passenger p = passengerRepository.findById(21L).orElse(null);
//			FlightSchedule fs = flightScheduleRepository.findByFlightDate(LocalDate.of(2023,11,20));
//			SeatClassType s = SeatClassType.BUSINESS;
//			int totalSeat = 2;
//			double totalFare = 13000.0;
//			TicketStatus ts = TicketStatus.UPCOMING;
//
//			Ticket t = new Ticket("0",f,p,fs,s,totalSeat,totalFare,ts);
//			ticketRepository.save(t);

// Creating Schedule
//			Flight f = flightRepository.findById("CCU-DAC-001").orElse(null);
//			System.out.println(f.getDuration());
//
//			List<String> flightcodelists = new ArrayList<>(
//					List.of(
//							"CCU-DAC-001","CGP-CCU-001","CXB-DAC-001","CXB-DAC-002","DAC-CXB-001","DAC-CXB-002","DAC-CXB-003",
//							"DAC-RJH-001","DAC-ZYL-001", "DAC-CXB-004", "DAC-CXB-005"
//					)
//			);
			
//			List<String> flightcodelists = new ArrayList<>(
//					List.of("DAC-CXB-005")
//			);
			
//			FlightSchedule fs = new FlightSchedule(LocalDate.of(2023,11,15),flightcodelists);
//			flightScheduleRepository.save(fs);
			
//			Airport bar = new Airport("BRT","Barisal Airport","Borisal");
//			airportRepository.save(bar);
			
//	new SeatInfo
//			List<SeatInfo> seatInfoList = new ArrayList<>(
//					List.of(
//						new SeatInfo( SeatClassType.BUSINESS, 10, 6000),
//						new SeatInfo( SeatClassType.ECONOMY, 40, 3500)
//					)
//			);

//	Updating SeatInfo for the existing flights

//			Flight f = flightRepository.findById("CGP-CCU-001").orElse(null);
//			seatInfoList.forEach( seatInfo -> seatInfo.setFlight(f));
//          assert f != null;
//          f.setSeatInfoList(seatInfoList);
//			flightRepository.save(f);

// 	Adding seatinfo to flights
//			Airport a1 = airportRepository.findById("RJH").orElse(null);
//			Airport a2 = airportRepository.findById("DAC").orElse(null);
//
//			Flight f1 = new Flight("1","US-Bangla",a1,a2,
//					LocalTime.of(16,15), ZoneId.of("Asia/Dhaka"),
//					LocalTime.of(16,45), ZoneId.of("Asia/Dhaka"),
//					192);
//
//			f1.setSeatInfoList(seatInfoList);
//			flightRepository.save(f1);

//	 Delete an airport
//			airportRepository.deleteById("BRT");
		};
	}
}
