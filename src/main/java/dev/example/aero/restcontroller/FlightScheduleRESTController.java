package dev.example.aero.restcontroller;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.service.FlightScheduleService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class FlightScheduleRESTController {
    private final FlightScheduleService flightScheduleService;
    private final FlightScheduleRepository flightScheduleRepository;

    public FlightScheduleRESTController(FlightScheduleService flightScheduleService, FlightScheduleRepository flightScheduleRepository) {
        this.flightScheduleService = flightScheduleService;
        this.flightScheduleRepository = flightScheduleRepository;
    }

    @GetMapping("/find")
    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(
            @RequestParam String from, @RequestParam String to, @RequestParam String date,
            @RequestParam String classType, @RequestParam int noPassengers) {
        List<FlightDetailsResponseDTO> flightResponseDTOList = flightScheduleService.getFlightDetailsOnDate(from,to,date,classType,noPassengers);
        if (flightResponseDTOList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No flight found");
        return flightResponseDTOList;
    }

    @PostMapping("/insert")
    public String addFlightSchedule(@RequestBody Map<String,Object> req) {
        try {
            LocalDate flightDate = LocalDate.parse((String) req.get("flightDate"));
            List<String> flightIDs = (List<String>) req.get("flightIds");
            flightScheduleService.addOrUpdateFlightSchedule(flightDate, flightIDs);
            return "Schedule Updated Successfully!";
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Already have the Flight(s) On the day");
        }
    }

    @GetMapping("/flightids")
    public List<String> getFlightIDs(@RequestParam String date) {
        LocalDate flightDate = LocalDate.parse(date);
        return flightScheduleRepository.getAlreadyAddedFlightIDsOnDate(flightDate);
    }

    @GetMapping("/canceled-flightids")
    public List<String> getCancelledFlightIDs(@RequestParam String date) {
        LocalDate flightDate = LocalDate.parse(date);
        return flightScheduleRepository.getAlreadyCancelledFlightIDsOnDate(flightDate);
    }
}
