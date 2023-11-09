// package GatorLibrary;
import java.time.LocalDateTime;

public class ReservationHeapNode {
    LocalDateTime timeOfReservation;
    int patronId;
    int priorityNumber;

    public ReservationHeapNode(LocalDateTime timeOfReservation, int patronId, int priorityNumber){
        this.timeOfReservation = timeOfReservation;
        this.patronId = patronId;
        this.priorityNumber = priorityNumber;
    }

}
