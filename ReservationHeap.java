public class ReservationHeap {
    // Class to implement Min Heap from scratch instead of using Priority Queue library
    
    public ReservationHeapNode[] heapArray;
    public int size;
    public int maximumCapacity;

    public ReservationHeap(int maximumCapacity){
        this.maximumCapacity = maximumCapacity;
        this.size = 0;
        heapArray = new ReservationHeapNode[this.maximumCapacity];
    }

    private void swap(int i, int j) {
        ReservationHeapNode temp = heapArray[i];
        heapArray[i] = heapArray[j];
        heapArray[j] = temp;
    }

    public void bottomUpHeapify() {
        int idx = size - 1;
        while (idx > 0) {
            int parentIdx = (idx - 1) / 2;
    
            if (heapArray[idx].priorityNumber < heapArray[parentIdx].priorityNumber) {
                swap(idx, parentIdx);
                idx = parentIdx;
            } else if (heapArray[idx].priorityNumber == heapArray[parentIdx].priorityNumber) {
                if (heapArray[idx].timeOfReservation.isBefore(heapArray[parentIdx].timeOfReservation) ||
                    heapArray[idx].timeOfReservation.equals(heapArray[parentIdx].timeOfReservation)) {
                    swap(idx, parentIdx);
                    idx = parentIdx;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }
    

    private void topDownHeapify() {
        int idx = 0;
        while (true) {
            int leftChildIndex = 2 * idx + 1;
            int rightChildIndex = 2 * idx + 2;
            int smallest = idx;

            if (leftChildIndex < size && heapArray[leftChildIndex].priorityNumber < heapArray[smallest].priorityNumber) {
                smallest = leftChildIndex;
            }else if(leftChildIndex < size && heapArray[leftChildIndex].priorityNumber == heapArray[smallest].priorityNumber){
                if(heapArray[leftChildIndex].timeOfReservation.isBefore(heapArray[smallest].timeOfReservation)){
                    smallest = leftChildIndex;
                }
            }

            if (rightChildIndex < size && heapArray[rightChildIndex].priorityNumber < heapArray[smallest].priorityNumber) {
                smallest = rightChildIndex;
            }else if(rightChildIndex < size && heapArray[rightChildIndex].priorityNumber == heapArray[smallest].priorityNumber){
                if(heapArray[rightChildIndex].timeOfReservation.isBefore(heapArray[smallest].timeOfReservation)){
                    smallest = rightChildIndex;
                }
            }

            if (smallest != idx) {
                swap(idx, smallest);
                idx = smallest;
            } else {
                break;
            }
        }
    }

    public void insert(ReservationHeapNode node){
        if(size == maximumCapacity){
            System.out.println("Heap is Full");
            return;
        }

        heapArray[size] = node;
        size++;
        bottomUpHeapify();
    }

    public ReservationHeapNode removeMin(){
        if(size == 0){
            return new ReservationHeapNode(null, -1, -1);
        }

        ReservationHeapNode minElement = heapArray[0];
        heapArray[0] = heapArray[size - 1];
        size--;
        topDownHeapify();
        return minElement;
    }
}