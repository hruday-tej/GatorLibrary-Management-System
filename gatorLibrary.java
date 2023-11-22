import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

class gatorLibrary {
    RedBlackTree libraryTree;

    public gatorLibrary() {
        this.libraryTree = new RedBlackTree();
    }

    /**
     * this function is used to print the details of the given book
     * @param book - the book object we want to print details about
     */
    public void printBookInfo(Book foundBook) {
        System.out.println("BookID = " + foundBook.bookId);
        System.out.println("Title = " + foundBook.bookName);
        System.out.println("Author = " + foundBook.authorName);
        String availabilityStringFilteterd = foundBook.availabilityStatus.matches("^\".*\"$") ? foundBook.availabilityStatus : "\"" + foundBook.availabilityStatus + "\"";
        System.out.println("Availability = " + availabilityStringFilteterd);
        String borrowed = foundBook.borrowedBy == -1 ? "None" : String.valueOf(foundBook.borrowedBy);
        System.out.println("BorrowedBy = " + borrowed);
        System.out.println("Reservations = " + foundBook.getCurrentReservations().toString());
        System.out.println();
    }

    /**
     * This function will call the respective functions in the redblacktree
     * @param methodName - the method we want to call in the RBT
     * @param parameters - the parameters required for the method.
     */
    public void callAppropriateMethod(String methodName, String[] parameters) {
        switch (methodName) {
            case "PrintBook":
                int printBookId = Integer.parseInt(parameters[0]);
                RedBlackTreeNode foundBook = libraryTree.searchBook(printBookId);
                if (foundBook != null) {
                    printBookInfo(foundBook.book);
                } else {
                    System.out.println("Book " + printBookId + " not found in the library");
                    System.out.println();
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
                libraryTree.insertBook(bookToBeInserted);
                break;

            case "BorrowBook":
                int borrowPatronId = Integer.parseInt(parameters[0]);
                int borrowBookId = Integer.parseInt(parameters[1]);
                int patronPriority = Integer.parseInt(parameters[2]);
                RedBlackTreeNode bookToBorrow = libraryTree.searchBook(borrowBookId);
                // System.out.println("BOOK TO BORROW -> "+bookToBorrow.book.bookId);
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
                if(returnBook == null)break;
                if (returnBook.book.borrowedBy == returnPatronId) {
                    System.out.println("Book " + returnBookId + " Returned by Patron " + returnPatronId);
                    System.out.println();
                    ReservationHeapNode nextReserved = returnBook.book.reservationHeap.removeMin();
                    if (nextReserved.patronId != -1) {
                        returnBook.book.borrowedBy = nextReserved.patronId;
                        System.out.println("Book " + returnBookId + " Allotted to Patron " + nextReserved.patronId);
                        System.out.println();
                    } else {
                        returnBook.book.availabilityStatus = "Yes";
                        returnBook.book.borrowedBy = -1;
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

            case "ColorFlipCount":
                System.out.println("Color Flip Count: " + libraryTree.colorFlipCount);
                System.out.println();
                break;

            case "DeleteBook":
                int deletionBookID = Integer.parseInt(parameters[0]);
                RedBlackTreeNode bookToBeDeleted = libraryTree.searchBook(deletionBookID);
                String reservationsList = bookToBeDeleted.book.getCurrentReservations().toString().replace("[", "")
                        .replace("]", "");
                // System.out.println(bookToBeDeleted.book.getCurrentReservations() + "????????????????");
                if (reservationsList.length() == 0) {
                    System.out.println("Book " + deletionBookID +
                            " is no longer available");
                } else {
                    if (bookToBeDeleted.book.getCurrentReservations().size() == 1) {
                        System.out.println("Book " + deletionBookID +
                                " is no longer available. Reservation made by Patron "
                                + reservationsList
                                + " has been cancelled!");
                    } else
                        System.out.println("Book " + deletionBookID +
                                " is no longer available. Reservations made by Patrons "
                                + reservationsList
                                + " have been cancelled!");
                }
                libraryTree.deleteBook(deletionBookID);
                System.out.println();
                break;
            case "PutDebugger":
                System.out.println(" --------------------- ");
                System.out.println();
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        PrintStream printStreamOutput = System.out;

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            FileOutputStream fileOutputStream = new FileOutputStream(args[0].substring(0,args[0].indexOf(".txt")) + "_" + "output_file.txt");

            // Create a new PrintStream that writes to the file
            PrintStream filePrintStream = new PrintStream(fileOutputStream);
            System.setOut(filePrintStream);

            String[] methodInvocation = sb.toString().split("\n");

            // System.out.println(methodInvocation.length);
            for (int i = 0; i < methodInvocation.length; i++) {
                String method = methodInvocation[i];
                String methodName = method.substring(0, method.indexOf("("));
                String parametersString = method.substring(method.indexOf("(") + 1,
                        method.indexOf(")"));
                String[] parameters = parametersString.split(",");
                for (int k = 0; k < parameters.length; k++) {
                    parameters[k] = parameters[k].trim();
                }
                gatorLibrary gatorLibrary = new gatorLibrary();
                // System.out.println("line number - > " + (i+1));
                // System.out.println("Performing --> " + methodInvocation[i]);
                // System.out.println();
                gatorLibrary.callAppropriateMethod(methodName, parameters);
            }
            // System.out.println(methodInvocation);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.setOut(printStreamOutput);
        }
    }
}