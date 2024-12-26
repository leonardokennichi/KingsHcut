package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReservationHeader {
    private String reservationId;
    private LocalDate date;
    private LocalTime reservationTime;
    private LocalTime endTime;
    private String reservationStatus;
    private String serviceName; // You might not need this here, see note below

    // Constructor for fetching data from the database
    public ReservationHeader(String reservationId, LocalDate date, LocalTime reservationTime, LocalTime endTime, String reservationStatus) {
        this.reservationId = reservationId;
        this.date = date;
        this.reservationTime = reservationTime;
        this.endTime = endTime;
        this.reservationStatus = reservationStatus;
    }

    // Constructor (Potentially used when adding a new reservation)
    public ReservationHeader(LocalDate date, LocalTime reservationTime, String serviceName) {
        this.date = date;
        this.reservationTime = reservationTime;
        this.serviceName = serviceName; // You might set endTime and status later
    }

    // Getters and Setters
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public boolean matches(String reservationString) {
	    // Define a logic to match the reservation string with this ReservationHeader
	    return reservationString.contains(this.date.toString()) 
	        && reservationString.contains(this.reservationTime.format(DateTimeFormatter.ofPattern("HH:mm")));
	}
}