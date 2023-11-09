// package GatorLibrary;

import java.time.LocalDate;
import java.time.LocalDateTime;

// import GatorLibrary.ReservationHeap;
public class TestRunner {
    public static void main(String[] args) {
        ReservationHeap rHeap = new ReservationHeap(4);
        // LocalDateTime l = new LocalDate
        rHeap.insert(new ReservationHeapNode(LocalDateTime.parse("2017-01-14T15:32:56.000"), 101, 3));
        rHeap.insert(new ReservationHeapNode(LocalDateTime.parse("2017-01-13T15:32:56.000"), 101, 5));
        rHeap.insert(new ReservationHeapNode(LocalDateTime.parse("2017-01-13T15:32:56.000"), 101, 4));

        System.out.println(rHeap.removeMin().priorityNumber);
        System.out.println(rHeap.removeMin().priorityNumber);
        System.out.println(rHeap.removeMin().priorityNumber);


    }
}
