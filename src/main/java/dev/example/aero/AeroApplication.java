package dev.example.aero;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.dao.FlightDAO;
import dev.example.aero.dao.FlightScheduleDAO;
import dev.example.aero.model.Airport;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.SeatInfo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class AeroApplication {

	public static void main(String[] args) {
		SpringApplication.run(AeroApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(String[] args, AirportDao airportDao, FlightDAO flightDAO, FlightScheduleDAO flightScheduleDAO){
		return runner -> {
			System.out.println("hello Aero!");


// Creating Schedule
//			Flight f = flightDAO.findById("CCU-DAC-001").orElse(null);
//			System.out.println(f.getDuration());

//			List<String> flightcodelists = new ArrayList<>(
//					List.of(
//							"CCU-DAC-001","CGP-CCU-001","CXB-DAC-001","CXB-DAC-002","DAC-CXB-001","DAC-CXB-002","DAC-CXB-003",
//							"DAC-RJH-001","DAC-ZYL-001"
//					)
//			);
//			List<String> flightcodelists = new ArrayList<>(
//					List.of("DAC-CXB-005")
//			);
//			FlightSchedule fs = new FlightSchedule(LocalDate.of(2023,11,15),flightcodelists);
//			flightScheduleDAO.save(fs);


//			Airport bar = new Airport("BRT","Barisal Airport","Borisal");
//			airportDao.save(bar);


//	new SeatInfo
//			List<SeatInfo> seatInfoList = new ArrayList<>(
//					List.of(
//						new SeatInfo( SeatClassType.BUSINESS, 20, 8000),
//						new SeatInfo( SeatClassType.ECONOMY, 60, 4500)
//					)
//			);

//	Updating SeatInfo for the existing flights

//			Flight f = flightDAO.findById("CGP-CCU-001").orElse(null);
//			seatInfoList.forEach( seatInfo -> seatInfo.setFlight(f));
//            assert f != null;
//            f.setSeatInfoList(seatInfoList);
//			flightDAO.save(f);
//
// 	Adding seatinfo to flights
//			Airport a1 = airportDao.findByID("DAC");
//			Airport a2 = airportDao.findByID("CXB");
//
//			Flight f1 = new Flight("1","Emirates",a1,a2,
//					LocalTime.of(11,15), ZoneId.of("Asia/Dhaka"),
//					LocalTime.of(12,0), ZoneId.of("Asia/Dhaka"),
//					297.65);
//
//			seatInfoList.forEach( seatInfo -> seatInfo.setFlight(f1));
//			f1.setSeatInfoList(seatInfoList);
//			flightDAO.save(f1);

//			airportDao.deleteByID("BRT");

		};
	}
}
