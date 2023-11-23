package dev.example.aero.util;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dev.example.aero.model.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Getter
@Setter
public class TicketPDFGenerator {
    private final Ticket ticket;
    private static final float SPACING = 10f;
    private static final float BORDER_AFTER = 30f;
    private static final float PARAGRAPH_LEADING = 30f;
    private final Font headerFont;
    private final Font valueFont;
    private final Font coloredFont;

    public TicketPDFGenerator(Ticket ticket) {
        this.ticket = ticket;
        headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        valueFont = FontFactory.getFont(FontFactory.COURIER, 18);
        coloredFont= FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
    }

    public byte[] generatePDF() throws URISyntaxException, IOException, DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();

        document.add(generateHeader());

        document.add(generateBorder());

        String passengerName = ticket.getPassenger().getFirstName() + " " + ticket.getPassenger().getLastName();
        document.add(addInfoToTable(new PdfPTable(1), new String[]{"Passenger"}, new String[]{passengerName}));

        String from = ticket.getFlight().getFromAirport().getCity() + "\n" + ticket.getFlight().getFromAirport().getName();
        document.add(addInfoToTable(new PdfPTable(1), new String[]{"From"}, new String[]{from}));

        String to = ticket.getFlight().getToAirport().getCity() + "\n" + ticket.getFlight().getToAirport().getName();
        document.add(addInfoToTable(new PdfPTable(1), new String[]{"To"}, new String[]{to}));

        String airline = ticket.getFlight().getAirline();
        String flightNo = ticket.getFlight().getId();
        document.add(addInfoToTable(new PdfPTable(new float[]{1,1}), new String[]{"Carrier", "Flight"}, new String[]{airline,flightNo}));


        String date = ticket.getFlightSchedule().getFlightDate()
                .format(DateTimeFormatter.ofPattern("d MMM, uuuu"));
        String departTime = ticket.getFlight().getDepartureTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        document.add(addInfoToTable(new PdfPTable(new float[]{1,1}), new String[]{"Departure Date", "Time"}, new String[]{date,departTime}));

        String seatNo = generateSeatNo();
        document.add(addInfoToTable(new PdfPTable(1), new String[]{"Seat"}, new String[]{seatNo}));

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private PdfPTable generateHeader() throws DocumentException, IOException, URISyntaxException {
        Path logoPath = Paths.get(ClassLoader.getSystemResource("aero_logo.png").toURI());

        PdfPTable table = new PdfPTable(2);
        table.setWidths(new float[]{1, 2});

        PdfPCell idCell = new PdfPCell(new Phrase("#" + ticket.getId(), headerFont));
        idCell.setBorder(0);
        idCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        idCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

        Image logoImage = Image.getInstance(logoPath.toAbsolutePath().toString());
        PdfPCell logoCell = new PdfPCell(logoImage);
        logoCell.setBorder(0);
        logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(idCell);
        table.addCell(logoCell);

        return table;
    }

    private PdfPTable generateBorder() {
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderColor(BaseColor.GRAY);
        lineCell.setFixedHeight(SPACING);

        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setSpacingBefore(SPACING);
        lineTable.addCell(lineCell);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingAfter(BORDER_AFTER);

        return lineTable;
    }

    private PdfPTable addInfoToTable(PdfPTable table, String[] header, String[] value) {
        for (int i = 0; i < header.length; i++) {
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(0);

            Paragraph paragraph = new Paragraph();
            paragraph.setFont(valueFont);
            paragraph.add(new Chunk(header[i]));
            paragraph.add(Chunk.NEWLINE);
            paragraph.setLeading(PARAGRAPH_LEADING);

            if (header[i].equals("Flight")){
                Chunk valueChunk = new Chunk(value[i], coloredFont);
                paragraph.add(valueChunk);
            } else {
                paragraph.setFont(headerFont);
                paragraph.add(new Chunk(value[i]));
            }
            infoCell.addElement(paragraph);
            table.addCell(infoCell);
        }
        table.setSpacingAfter(SPACING);
        return table;
    }

    private String generateSeatNo() {
        String seatClassStr = String.valueOf(ticket.getSeatClassType()).substring(0, 1);
        int lastSeatNo = getLastSeatNo();

        return IntStream.rangeClosed(1, ticket.getTotalSeats())
                .mapToObj(i -> seatClassStr + (lastSeatNo + i))
                .collect(Collectors.joining(" - "));
    }

    private int getLastSeatNo() {
        return ticket.getFlightSchedule().getAvailableSeats();
    }
}
