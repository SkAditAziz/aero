package dev.example.aero.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import dev.example.aero.model.*;
import dev.example.aero.model.Enumaration.SeatClassType;
import dev.example.aero.model.Enumaration.TicketStatus;
import dev.example.aero.repository.FlightRepository;
import dev.example.aero.repository.FlightScheduleRepository;
import dev.example.aero.repository.TicketRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private PassengerService passengerService;
    @Autowired
    private FlightScheduleService flightScheduleService;
    @PersistenceContext
    private EntityManager entityManager;

    // temporarily adding, PDF Ticket generating will be moved to a class
    @Getter
    @Setter
    private float spacing;

    private final Session session;

    public TicketService(EntityManager entityManager) {
        if (entityManager == null || (this.session = entityManager.unwrap(Session.class)) == null)
            throw new NullPointerException();
    }

    public void issueTicket(Map<String,Object> req) {
        // TODO use jwt authToken to retrieve passenger
        Passenger p = passengerService.getPassengerById(((Integer) req.get("userId")).longValue());
        FlightSchedule fs = flightScheduleService.getScheduleByDate(LocalDate.parse((String) req.get("date")));
        Flight f = fs.getFlights().stream()
                .filter(flight -> flight.getId().equals((String) req.get("flightId")))
                .findFirst()
                .orElse(null);
        SeatClassType sct = (SeatClassType) SeatClassType.fromCode((String) req.get("seatClassType"));
        int totalSeats = (int) req.get("noPassengers");
        double totalFare = (double) req.get("totalFare");
        TicketStatus ts = TicketStatus.UPCOMING;
        Ticket t = new Ticket("",f,p,fs,sct,totalSeats,totalFare,ts);
        //TODO check if the passenger has already bought 4 tickets on the same flight on the same schedule
        ticketRepository.save(t);
        updateSeatAllocation(t);
        try {
            generatePDF(t);
        } catch (Exception e) {
            System.out.println("Exception in generating pdf..................");
            e.printStackTrace();
        }
    }

    @Transactional
    private void updateSeatAllocation(Ticket ticket) {
        FlightSchedule desiredSchedule = ticket.getFlightSchedule();

        Flight desiredFlight = desiredSchedule.getFlights().stream()
                .filter(f -> f.getId().equals(ticket.getFlight().getId()))
                .findFirst()
                .orElse(null);

        if (desiredFlight != null) {
            session.detach(desiredFlight);
        }

        assert desiredFlight != null;
        SeatInfo seatInfo = desiredFlight.getSeatInfoList().stream()
                .filter(s -> s.getSeatClassType() == ticket.getSeatClassType())
                .findFirst()
                .orElse(null);

        assert seatInfo != null;
        seatInfo.setAvailableSeats(seatInfo.getAvailableSeats() - ticket.getTotalSeats());
        flightScheduleRepository.save(desiredSchedule);
    }

    private void generatePDF(Ticket ticket) throws URISyntaxException, IOException, DocumentException {
        Path logoPath = Paths.get(ClassLoader.getSystemResource("aero_logo.png").toURI());

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        PdfWriter.getInstance(document, new FileOutputStream("ticket_" + ticket.getId() + ".pdf"));

        document.open();

        Image logoImage = Image.getInstance(logoPath.toAbsolutePath().toString());
        float logoXcoord = document.right() - logoImage.getScaledWidth();
        float logoYCoord = document.top() - logoImage.getScaledHeight();
        logoImage.setAbsolutePosition(logoXcoord, logoYCoord);
        document.add(logoImage);
        setSpacing(200);

        String passengerName = ticket.getPassenger().getFirstName() + " " + ticket.getPassenger().getLastName();
        addInfoToPdf(document,"Passenger Name", passengerName);

        String from = ticket.getFlight().getFromAirport().getCity() + "\n" + ticket.getFlight().getFromAirport().getName();
        addInfoToPdf(document,"From", from);

        String to = ticket.getFlight().getToAirport().getCity() + "\n" + ticket.getFlight().getToAirport().getName();
        addInfoToPdf(document,"To",to);

        String airline = ticket.getFlight().getAirline();
        addInfoToPdf(document,"Carrier", airline);

        String flightNo = ticket.getFlight().getId();
        addInfoToPdf(document,"Flight", flightNo);

        String date = ticket.getFlightSchedule().getFlightDate()
                .format(DateTimeFormatter.ofPattern("d MMM, uuuu"));
        addInfoToPdf(document,"Departure Date", date);

        String departTime = ticket.getFlight().getDepartureTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        addInfoToPdf(document,"Time", departTime);

        String seatNo = generateSeatNo(ticket);
        addInfoToPdf(document,"Seat", seatNo);

        document.close();
    }

    private void addInfoToPdf(Document document, String header, String value) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 16);

        Paragraph paragraph = new Paragraph();
        // temp
        paragraph.setSpacingBefore(getSpacing());
        if (getSpacing() != 0) setSpacing(0);

        paragraph.setFont(headerFont);
        paragraph.add(new Chunk(header));
        paragraph.add(Chunk.NEWLINE);
        paragraph.setFont(valueFont);
        paragraph.add(new Chunk(value));

        document.add(paragraph);
        document.add(Chunk.NEWLINE);
    }

    private String generateSeatNo(Ticket ticket) {
        StringBuilder seatNo = new StringBuilder();
        String seatClassStr = String.valueOf(ticket.getSeatClassType()).substring(0,1);
        int lastSeatNo = getLastSeatNo(ticket);
        for (int i=1; i<=ticket.getTotalSeats(); i++) {
            seatNo.append(seatClassStr).append(String.valueOf(lastSeatNo + i));
            if (i != ticket.getTotalSeats())
                seatNo.append(" - ");
        }
        return seatNo.toString();
    }

    private int getLastSeatNo(Ticket ticket) {
        return ticket.getFlightSchedule().getFlights().stream()
                .filter(flight -> ticket.getFlight().getId().equals(flight.getId()))
                .findFirst()
                .orElse(null)
                .getSeatInfoList().stream()
                .filter(seatInfo -> seatInfo.getSeatClassType() == ticket.getSeatClassType())
                .findFirst()
                .orElse(null)
                .getAvailableSeats();
    }
}
