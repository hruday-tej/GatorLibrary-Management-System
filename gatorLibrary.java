import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

class gatorLibrary {
    RedBlackTree libraryTree;

    public gatorLibrary() {
        this.libraryTree = new RedBlackTree();
    }

    public void printBookInfo(Book foundBook) {
        System.out.println("BookID = " + foundBook.bookId);
        System.out.println("Title = " + foundBook.bookName);
        System.out.println("Author = " + foundBook.authorName);
        System.out.println("Availability = " + foundBook.availabilityStatus);
        String borrowed = foundBook.borrowedBy == 0 ? "None" : String.valueOf(foundBook.borrowedBy);
        System.out.println("BorrowedBy = " + borrowed);
        System.out.println("Reservations = " + foundBook.getCurrentReservations().toString());
        System.out.println();
    }

    public void callAppropriateMethod(String methodName, String[] parameters) {
        switch (methodName) {
            case "PrintBook":
                int printBookId = Integer.parseInt(parameters[0]);
                RedBlackTreeNode foundBook = libraryTree.searchBook(printBookId);
                if (foundBook != null) {
                    printBookInfo(foundBook.book);
                } else {
                    System.out.println("Book Not found");
                }
                break;
            case "PrintBooks":
                int lowBookRange = Integer.parseInt(parameters[0]);
                int highBookRange = Integer.parseInt(parameters[1]);

                for (int i = lowBookRange; i <= highBookRange; i++) {
                    RedBlackTreeNode availableBook = libraryTree.searchBook(i);
                    if (availableBook != null) {
                        printBookInfo(availableBook.book);
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
                if (returnBook.book.borrowedBy == returnPatronId) {
                    System.out.println("Book " + returnBookId + " Returned by Patron " + returnPatronId);
                    System.out.println();
                    ReservationHeapNode nextReserved = returnBook.book.reservationHeap.removeMin();
                    if (nextReserved.patronId != -1) {
                        returnBook.book.borrowedBy = nextReserved.patronId;
                        System.out.println("Book " + returnBookId + " Allotted to Patron " + nextReserved.patronId);
                        System.out.println();
                    }
                } else {
                    System.out.println("something is fishy");
                }
                break;

            case "Quit":
                System.out.println("Program Terminated!!");
                System.exit(0);

            case "FindClosestBook":
                int closestTargetBookId = Integer.parseInt(parameters[0]);
                ArrayList<Book> closestBooks = libraryTree.closestBook(closestTargetBookId);
                if (Math.abs(closestBooks.get(0).bookId - closestTargetBookId) < Math
                        .abs(closestBooks.get(1).bookId - closestTargetBookId)) {
                    printBookInfo(closestBooks.get(0));
                } else if (Math.abs(closestBooks.get(0).bookId - closestTargetBookId) > Math
                        .abs(closestBooks.get(1).bookId - closestTargetBookId)) {
                    printBookInfo(closestBooks.get(1));
                } else {
                    if (closestBooks.get(0).bookId != closestBooks.get(1).bookId) {
                        printBookInfo(closestBooks.get(1));
                        printBookInfo(closestBooks.get(0));
                    } else {
                        printBookInfo(closestBooks.get(0));
                    }

                }

                break;

            case "DeleteBook":
                int deletionBookID = Integer.parseInt(parameters[0]);
                RedBlackTreeNode bookToBeDeleted = libraryTree.searchBook(deletionBookID);
                libraryTree.deleteNode(deletionBookID);
                System.out.println("Book " + deletionBookID +
                        " is no longer available. Reservations made by Patrons "
                        + bookToBeDeleted.book.getCurrentReservations().toString().replace("[", "").replace("]", "")
                        + " have been cancelled !!");
                System.out.println();
                break;

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

            System.out.println(methodInvocation.length);
            for (int i = 0; i < methodInvocation.length; i++) {
                String method = methodInvocation[i];
                String methodName = method.substring(0, method.indexOf("("));
                String parametersString = method.substring(method.indexOf("(") + 1,
                        method.indexOf(")"));
                String[] parameters = parametersString.split(", ");

                gatorLibrary gn = new gatorLibrary();
                // System.out.println("line number - > " + (i+1));
                // System.out.println("Performing --> " + methodInvocation[i]);
                // System.out.println();
                gn.callAppropriateMethod(methodName, parameters);
                // System.out.println("doneeeee");
            }
            // System.out.println(methodInvocation);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}