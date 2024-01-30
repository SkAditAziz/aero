package dev.example.aero.service;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.FlightSearchReqDTO;
import dev.example.aero.dto.mapper.FlightDetailsResponseDTOMapper;
import dev.example.aero.model.Enumaration.TicketStatus;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.Ticket;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.PassengerRepository;
import dev.example.aero.repository.TicketRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightScheduleService {
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightService flightService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    public void addOrUpdateFlightSchedule(LocalDate flightDate, List<String> flightIDs) {
        if (flightIDs == null || flightIDs.isEmpty() || flightIDs.stream().anyMatch(String::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Flight Inserted");

        for (String flightId : flightIDs) {
            addOrUpdateFlightSchedule(flightDate, flightId);
        }
    }

    private void addOrUpdateFlightSchedule(LocalDate flightDate, String flightID) {
        if (flightID == null || flightID.isEmpty())
            return;

        Flight flight = flightRepository.findById(flightID).orElse(null);

        if (flight != null) {
            try {
                List<FlightSchedule> flightSchedules = flight.getSeatInfoList().stream()
                        .map(seatInfo -> new FlightSchedule(flightDate, flightID, seatInfo.getSeatClassType(), seatInfo.getAvailableSeats(), seatInfo.getFare()))
                        .collect(Collectors.toList());
                flightScheduleRepository.saveAll(flightSchedules);
            } catch (DataIntegrityViolationException e) {
                String errMsg = e.getRootCause().getMessage();
                System.out.println(extractFlightDetails(errMsg));
            }
        }
    }

    private String extractFlightDetails(String errorMessage) {
        int startIndex = errorMessage.indexOf("Duplicate entry '") + "Duplicate entry '".length();
        int endIndex = errorMessage.indexOf("'", startIndex);
        errorMessage = errorMessage.substring(startIndex, endIndex);
        String[] parts = errorMessage.split("-");
        String flightID = parts[3] + "-" + parts[4] + "-" + parts[5];
        String flightDate = parts[2] + "-" + parts[1] + "-" + parts[0];
        return "Flight " + flightID + " on " + flightDate + " has already been added";
    }


    public void addOrUpdateFlightScheduleWithFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        if (contentType.startsWith("text/csv") || fileName.endsWith(".csv")) {
            try (Reader reader = new InputStreamReader(file.getInputStream())) {
                Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);

                for (CSVRecord record : records) {
                    String dateString = record.get(0);
                    if (dateString.equals("Date")) {
                        continue; // skipping the header row
                    }
                    LocalDate flightDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    if (flightDate.isBefore(LocalDate.now())) {
                        continue;
                    }

                    String flightID = record.get(1);
                    String status = record.get(2);

                    if (status == null || status.isEmpty()) {
                        addOrUpdateFlightSchedule(flightDate, flightID);
                    } else if (status.equalsIgnoreCase(String.valueOf(TicketStatus.CANCELLED))) {
                        cancelFlight(flightDate, flightID);
                    }
                }
            }
        } else if (contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            try(Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.iterator();
                rowIterator.next(); // skipping the header row

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    String dateString = row.getCell(0).getStringCellValue();
                    if (dateString == null || dateString.isEmpty()) break; // end of the file

                    LocalDate flightDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    if (flightDate.isBefore(LocalDate.now())) {
                        continue;
                    }

                    String flightID = row.getCell(1).getStringCellValue();

                    Cell statusCell = row.getCell(2);
                    String status = null;

                    if (statusCell != null) if (statusCell.getCellType() == CellType.STRING) {
                        status = statusCell.getStringCellValue();
                    }

                    if (status == null || status.isEmpty()) {
                        addOrUpdateFlightSchedule(flightDate, flightID);
                    } else if (status.equalsIgnoreCase(String.valueOf(TicketStatus.CANCELLED))) {
                        cancelFlight(flightDate, flightID);
                    }
                }
            }
        }
    }

    private void cancelFlight(LocalDate flightDate, String flightID) {
            List<FlightSchedule> schedulesToCancel = flightScheduleRepository.findIdByflightDateAndflightID(flightDate, flightID);
        List<Ticket> ticketsToCancel = new ArrayList<>();
        for (FlightSchedule schedule : schedulesToCancel) {
            ticketsToCancel.addAll(ticketRepository.findByflightSchedule(schedule));
            // disabling schedule by making no available seat, adding a flag in the FlightSchedule class is a better solution
            schedule.setAvailableSeats(0);
            flightScheduleRepository.save(schedule);
        }
        for (Ticket t : ticketsToCancel) {
            t.setTicketStatus(TicketStatus.CANCELLED);
            ticketRepository.save(t);
            jmsTemplate.convertAndSend("messagequeue.q", t);
        }
    }

    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(String from, String to, String date, String classType, int noPassengers) {
        List<FlightDetailsResponseDTO> result = flightScheduleRepository.findAllByFlightDateAndFromAndToAndClass(
                        LocalDate.parse(date), from, to, classType).stream()
                .map(new FlightDetailsResponseDTOMapper(noPassengers, flightService))
                .collect(Collectors.toList());

        return result.isEmpty() ? Collections.emptyList() : result;
    }

    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(FlightSearchReqDTO flightSearchReqDTO) {
        return getFlightDetailsOnDate(
                flightSearchReqDTO.getFromCode(),
                flightSearchReqDTO.getToCode(),
                flightSearchReqDTO.getJourneyDate().toString(),
                flightSearchReqDTO.getSeatClass().getCode(),
                flightSearchReqDTO.getNoPassengers()
        );
    }
}