package dev.example.aero.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import dev.example.aero.model.Ticket;
import lombok.Setter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class TicketPDFGenerator {
    private final Ticket ticket;
    @Setter
    private float spacing;

    public TicketPDFGenerator(Ticket ticket) {
        this.ticket = ticket;
        this.spacing = 200;
    }

    public void generatePDF() throws URISyntaxException, IOException, DocumentException {
        Path logoPath = Paths.get(ClassLoader.getSystemResource("aero_logo.png").toURI());

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        PdfWriter.getInstance(document, new FileOutputStream("ticket_" + ticket.getId() + ".pdf"));

        document.open();

        Image logoImage = Image.getInstance(logoPath.toAbsolutePath().toString());
        float logoXcoord = document.right() - logoImage.getScaledWidth();
        float logoYCoord = document.top() - logoImage.getScaledHeight();
        logoImage.setAbsolutePosition(logoXcoord, logoYCoord);
        document.add(logoImage);

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

        String seatNo = generateSeatNo();
        addInfoToPdf(document,"Seat", seatNo);

        document.close();
    }

    private void addInfoToPdf(Document document, String header, String value) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 16);

        Paragraph paragraph = new Paragraph();
        // temp
        paragraph.setSpacingBefore(spacing);
        if (spacing != 0) setSpacing(0);

        paragraph.setFont(headerFont);
        paragraph.add(new Chunk(header));
        paragraph.add(Chunk.NEWLINE);
        paragraph.setFont(valueFont);
        paragraph.add(new Chunk(value));

        document.add(paragraph);
        document.add(Chunk.NEWLINE);
    }

    private String generateSeatNo() {
        StringBuilder seatNo = new StringBuilder();
        String seatClassStr = String.valueOf(ticket.getSeatClassType()).substring(0,1);
        int lastSeatNo = getLastSeatNo();
        for (int i=1; i<=ticket.getTotalSeats(); i++) {
            seatNo.append(seatClassStr).append(lastSeatNo + i);
            if (i != ticket.getTotalSeats())
                seatNo.append(" - ");
        }
        return seatNo.toString();
    }

    private int getLastSeatNo() {
        return ticket.getFlightSchedule().getAvailableSeats();
    }
}
