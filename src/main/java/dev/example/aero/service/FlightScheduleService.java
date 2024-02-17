package dev.example.aero.service;

import dev.example.aero.dto.FlightDetailsResponseDTO;
import dev.example.aero.dto.FlightSearchReqDTO;
import dev.example.aero.dto.mapper.FlightDetailsResponseDTOMapper;
import dev.example.aero.enumeration.FlightStatus;
import dev.example.aero.model.Flight;
import dev.example.aero.model.FlightSchedule;
import dev.example.aero.model.Ticket;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
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
    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightRepository flightRepository;
    private final TicketRepository ticketRepository;
    private final JmsTemplate jmsTemplate;

    public FlightScheduleService(FlightScheduleRepository flightScheduleRepository, FlightRepository flightRepository, TicketRepository ticketRepository, JmsTemplate jmsTemplate) {
        this.flightScheduleRepository = flightScheduleRepository;
        this.flightRepository = flightRepository;
        this.ticketRepository = ticketRepository;
        this.jmsTemplate = jmsTemplate;
    }

    public void addOrUpdateFlightSchedule(LocalDate flightDate, List<String> flightIDs) {
        if (flightIDs == null || flightIDs.isEmpty() || flightIDs.stream().anyMatch(String::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Flight Inserted");

        for (String flightId : flightIDs) {
            addOrUpdateSingleFlightSchedule(flightDate, flightId);
        }
    }

    @Transactional
    private void addOrUpdateSingleFlightSchedule(LocalDate flightDate, String flightID) {
        Flight flight = flightRepository.findById(flightID).orElse(null);

        if (flight != null) {
            try {
                List<FlightSchedule> flightSchedules = flight.getSeatInfoList().stream()
                        .map(seatInfo -> new FlightSchedule(flightDate, flightID, seatInfo.getSeatClassType(), seatInfo.getAvailableSeats(), seatInfo.getFare(), FlightStatus.UPCOMING))
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
        if (isCSV(file)) {
            addOrUpdateWithCSV(file);
        } else {
            throw new IllegalArgumentException("Unsupported File Format!");
        }
    }

    private boolean isEXCEL(MultipartFile file) {
        return file.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".xls");
    }

    private boolean isCSV(MultipartFile file) {
        return file.getContentType().startsWith("text/csv") || file.getOriginalFilename().endsWith(".csv");
    }

    private void addOrUpdateWithXCEL(MultipartFile file) throws IOException{
        try(Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // skipping the header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // TODO if the cell's type is not string, it won't work!
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
                    addOrUpdateSingleFlightSchedule(flightDate, flightID);
                } else if (status.equalsIgnoreCase(String.valueOf(FlightStatus.CANCELLED))) {
                    cancelFlight(flightDate, flightID);
                }
            }
        }
    }

    private void addOrUpdateWithCSV(MultipartFile file) throws IOException{
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
                    addOrUpdateSingleFlightSchedule(flightDate, flightID);
                } else if (status.equalsIgnoreCase(String.valueOf(FlightStatus.CANCELLED))) {
                    cancelFlight(flightDate, flightID);
                }
            }
        }
    }

    @Transactional
    private void cancelFlight(LocalDate flightDate, String flightID) {
        List<FlightSchedule> schedulesToCancel = flightScheduleRepository.findByflightDateAndflightID(flightDate, flightID);
        List<Ticket> ticketsToCancel = new ArrayList<>();
        for (FlightSchedule schedule : schedulesToCancel) {
            ticketsToCancel.addAll(ticketRepository.findByflightSchedule(schedule));
            // disabling schedule by making no available seat, adding a flag in the FlightSchedule class is a better solution
            schedule.setAvailableSeats(0);
            flightScheduleRepository.save(schedule);
        }
        for (Ticket t : ticketsToCancel) {
            t.setFlightStatus(FlightStatus.CANCELLED);
            ticketRepository.save(t);
            jmsTemplate.convertAndSend("messagequeue.q", t);
        }
    }

    public List<FlightDetailsResponseDTO> getFlightDetailsOnDate(String from, String to, String date, String classType, int noPassengers) {
        List<FlightDetailsResponseDTO> result = flightScheduleRepository.findAllByFlightDateAndFromAndToAndClass(
                        LocalDate.parse(date), from, to, classType).stream()
                .map(new FlightDetailsResponseDTOMapper(noPassengers, flightRepository))
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

    public void cancelFlightsOnDate(LocalDate flightDate, List<String> flightIDs) {
        if (flightIDs == null || flightIDs.isEmpty() || flightIDs.stream().anyMatch(String::isEmpty))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        for (String flightId : flightIDs) {
            cancelFlight(flightDate, flightId);
        }
    }
}