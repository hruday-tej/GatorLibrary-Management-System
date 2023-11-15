import java.util.ArrayList;
import java.util.Arrays;

public class Book {
    int bookId;
    String bookName;
    String authorName;
    String availabilityStatus;
    int borrowedBy;
    ReservationHeap reservationHeap;

    public Book(int bookId, String bookName, String authorName, String availabilityStatus){
        this.bookId = bookId;
        this.bookName = bookName;
        this.authorName = authorName;
        this.availabilityStatus = availabilityStatus;
        // this.borrowedBy = borrowedBy;
        reservationHeap = new ReservationHeap(100000001);
    }

    public ArrayList<Integer> getCurrentReservations(){
        if(reservationHeap.heapArray.length == 0)return new ArrayList<Integer>();
        ArrayList<Integer> reservedPatrons = new ArrayList<Integer>();
        for(int i=0;i<reservationHeap.heapArray.length;i++){
            if(reservationHeap.heapArray[i] != null)
            reservedPatrons.add(reservationHeap.heapArray[i].patronId);
        }

        return reservedPatrons;
    }

    public void addPatronToWaitList(ReservationHeapNode patronNode){
        reservationHeap.insert(patronNode);
    }
}
