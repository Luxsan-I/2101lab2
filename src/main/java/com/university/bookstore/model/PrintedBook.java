package com.university.bookstore.model;

import java.util.regex.Pattern;

/**
 * Represents a physical printed book in the bookstore inventory.
 * Extends Material class to demonstrate inheritance.
 * 
 * <p>This class maintains backward compatibility with the original Book class
 * while integrating into the new polymorphic material hierarchy.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class PrintedBook extends Material {
    
    private static final Pattern ISBN_13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern ISBN_10_PATTERN = Pattern.compile("^\\d{9}[\\dX]$");
    
    private final String isbn;
    private final String author;
    private final int pages;
    private final String publisher;
    private final boolean hardcover;
    
    /**
     * Creates a new PrintedBook with validation.
     * 
     * @param isbn the International Standard Book Number
     * @param title the book title
     * @param author the primary author
     * @param price the price in dollars
     * @param year the publication year
     * @param pages number of pages
     * @param publisher the publishing company
     * @param hardcover true if hardcover, false if paperback
     */
    public PrintedBook(String isbn, String title, String author, double price, 
                       int year, int pages, String publisher, boolean hardcover) {
        super(validateIsbn(isbn), title, price, year, MaterialType.BOOK);
        this.isbn = this.id;
        this.author = validateStringField(author, "Author");
        this.pages = validatePages(pages);
        this.publisher = validateStringField(publisher, "Publisher");
        this.hardcover = hardcover;
    }
    
    /**
     * Convenience constructor for backward compatibility.
     */
    public PrintedBook(String isbn, String title, String author, double price, int year) {
        this(isbn, title, author, price, year, 0, "Unknown", false);
    }
    
    private static String validateIsbn(String isbn) {
        if (isbn == null) {
            throw new NullPointerException("ISBN cannot be null");
        }
        
        String cleaned = isbn.replaceAll("-", "").trim();
        
        if (!ISBN_10_PATTERN.matcher(cleaned).matches() && 
            !ISBN_13_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                "ISBN must be 10 or 13 digits. Provided: " + isbn);
        }
        
        return cleaned;
    }
    
    private int validatePages(int pages) {
        if (pages < 0) {
            throw new IllegalArgumentException("Pages cannot be negative");
        }
        return pages;
    }
    
    @Override
    public String getCreator() {
        return author;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s by %s (%d, %s) - %d pages, $%.2f",
            title, author, year, 
            hardcover ? "Hardcover" : "Paperback",
            pages, price);
    }
    
    @Override
    public double getDiscountRate() {
        int currentYear = java.time.Year.now().getValue();
        if (year < currentYear - 2) {
            return 0.15;
        }
        return 0.0;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public int getPages() {
        return pages;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public boolean isHardcover() {
        return hardcover;
    }
    
    /**
     * Calculates estimated reading time based on average reading speed.
     * 
     * @param wordsPerMinute reading speed (typical: 200-250)
     * @return estimated hours to read
     */
    public double estimateReadingTime(int wordsPerMinute) {
        if (pages == 0 || wordsPerMinute <= 0) {
            return 0;
        }
        int avgWordsPerPage = 250;
        int totalWords = pages * avgWordsPerPage;
        return totalWords / (double)(wordsPerMinute * 60);
    }
    
    @Override
    public String toString() {
        return String.format("PrintedBook[ISBN=%s, Title='%s', Author='%s', Price=$%.2f, Year=%d, Pages=%d, %s]",
            isbn, title, author, price, year, pages,
            hardcover ? "Hardcover" : "Paperback");
    }
}