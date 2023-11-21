import java.util.ArrayList;

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
        this.borrowedBy = -1;
        reservationHeap = new ReservationHeap(1000001);
    }

    public ArrayList<Integer> getCurrentReservations(){
        if(reservationHeap.heapArray.length == 0)return new ArrayList<Integer>();
        ArrayList<Integer> reservedPatrons = new ArrayList<Integer>();
        for(int i=0;i<reservationHeap.size;i++){
            if(reservationHeap.heapArray[i] != null)
            reservedPatrons.add(reservationHeap.heapArray[i].patronId);
        }

        return reservedPatrons;
    }

    public void addPatronToWaitList(ReservationHeapNode patronNode){
        reservationHeap.insert(patronNode);
    }
}
