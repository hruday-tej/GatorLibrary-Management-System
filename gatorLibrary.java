import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

class gatorLibrary {
    RedBlackTree libraryTree;

    public gatorLibrary() {
        this.libraryTree = new RedBlackTree();
    }

    public void printBookInfo(RedBlackTreeNode foundBook) {
        System.out.println("Book ID = " + foundBook.book.bookId);
        System.out.println("Title = " + foundBook.book.bookName);
        System.out.println("Author = " + foundBook.book.authorName);
        System.out.printf("Availability = %s\n", foundBook.book.availabilityStatus);
        String borrowed = foundBook.book.borrowedBy == 0 ? "None" : String.valueOf(foundBook.book.borrowedBy);
        System.out.println("Borrowed by = " + borrowed);
        System.out.println("Reservations = " + foundBook.book.getCurrentReservations().toString());
        System.out.println();
    }

    public void callAppropriateMethod(String methodName, String[] parameters) {
        switch (methodName) {
            case "PrintBook":
                int printBookId = Integer.parseInt(parameters[0]);
                RedBlackTreeNode foundBook = libraryTree.searchBook(printBookId);
                if (foundBook != null) {
                    printBookInfo(foundBook);
                } else {
                    System.out.println("Book Not found");
                }
                break;
            case "PrintBooks":
                int lowBookRange = Integer.parseInt(parameters[0]);
                int highBookRange = Integer.parseInt(parameters[1]);

                for(int i=lowBookRange; i<= highBookRange;i++){
                    RedBlackTreeNode availableBook = libraryTree.searchBook(i);
                    if(availableBook != null){
                        printBookInfo(availableBook);
                    }
                }
                break;
            case "InsertBook":
                int insertBookId = Integer.parseInt(parameters[0]);
                String bookName = parameters[1];
                String authorName = parameters[2];
                String availabilityStatus = parameters[3];
                Book bookToBeInserted = new Book(insertBookId, bookName, authorName, availabilityStatus);
                libraryTree.insertNode(bookToBeInserted);
                break;

            case "BorrowBook":
                int borrowPatronId = Integer.parseInt(parameters[0]);
                int borrowBookId = Integer.parseInt(parameters[1]);
                int patronPriority = Integer.parseInt(parameters[2]);
                RedBlackTreeNode bookToBorrow = libraryTree.searchBook(borrowBookId);

                if (bookToBorrow != null) {
                    if (bookToBorrow.book.availabilityStatus.equals("No")) {
                        bookToBorrow.book.addPatronToWaitList(
                                new ReservationHeapNode(LocalDateTime.now(), borrowPatronId, patronPriority));
                        System.out.println("Book " + borrowBookId + " Reserved by Patron " + borrowPatronId);
                        System.out.println();
                    } else {
                        bookToBorrow.book.availabilityStatus = "No";
                        bookToBorrow.book.borrowedBy = borrowPatronId;
                        System.out.println("Book " + borrowBookId + " Borrowed by Patron " + borrowPatronId);
                        System.out.println();
                    }
                }
                break;

            case "ReturnBook":
                int returnPatronId = Integer.parseInt(parameters[0]);
                int returnBookId = Integer.parseInt(parameters[1]);

                RedBlackTreeNode returnBook = libraryTree.searchBook(returnBookId);
                if(returnBook.book.borrowedBy == returnPatronId){
                        System.out.println("Book " + returnBookId + " Returned by Patron " + returnPatronId);
                        System.out.println();
                    ReservationHeapNode nextReserved = returnBook.book.reservationHeap.removeMin();

                    if(nextReserved != null){
                        returnBook.book.borrowedBy = nextReserved.patronId;
                        System.out.println("Book " + returnBookId + " Allotted to Patron " + nextReserved.patronId);
                        System.out.println();
                    }
                }
            
            case "Quit":
                System.out.println("Program Terminated!!");
                System.exit(0);
            default:
                break;
        }
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            String[] methodInvocation = sb.toString().split("\n");
            // System.out.println(methodInvocation[0]);
            for (int i = 0; i < methodInvocation.length; i++) {
                String method = methodInvocation[i];
                String methodName = method.substring(0, method.indexOf("("));
                String parametersString = method.substring(method.indexOf("(") + 1,
                        method.indexOf(")"));
                String[] parameters = parametersString.split(", ");

                gatorLibrary gn = new gatorLibrary();

                gn.callAppropriateMethod(methodName, parameters);
            }
            // System.out.println(methodInvocation);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}