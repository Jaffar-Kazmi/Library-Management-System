package src;

import java.time.LocalDate;

public class Order {
    Book book;
    String orderedBy;
    LocalDate orderedOn;

    Order(Book book, String orderedBy, LocalDate orderedOn) {
        this.book = book;
        this.orderedBy = orderedBy;
        this.orderedOn = orderedOn;
    }

    @Override
    public String toString() {
        return book + " | Ordered By " + orderedBy + " | on " + orderedOn + " |";
    }
}
