package com.university.bookstore.model;

import java.time.Year;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents an immutable book in the bookstore inventory.
 * Books are uniquely identified by their ISBN.
 * 
 * <p>This class is immutable and thread-safe. All fields are validated
 * during construction to ensure data integrity.</p>
 * 
 * @author Navid Mohaghegh
 * @version 1.0
 * @since 2024-09-15
 */
public final class Book implements Comparable<Book> {
    
    private static final Pattern ISBN_13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern ISBN_10_PATTERN = Pattern.compile("^\\d{9}[\\dX]$");
    private static final int MIN_YEAR = 1450; // Invention of printing press
    
    private final String isbn;
    private final String title;
    private final String author;
    private final double price;
    private final int year;
    
    /**
     * Creates a new Book with validation.
     * 
     * @param isbn the International Standard Book Number (10 or 13 digits)
     * @param title the book title (non-null, non-blank)
     * @param author the primary author (non-null, non-blank)
     * @param price the price in dollars (non-negative)
     * @param year the publication year (1450 to current year + 1)
     * @throws IllegalArgumentException if any parameter is invalid
     * @throws NullPointerException if any string parameter is null
     */
    public Book(String isbn, String title, String author, double price, int year) {
        this.isbn = validateIsbn(isbn);
        this.title = validateStringField(title, "Title");
        this.author = validateStringField(author, "Author");
        this.price = validatePrice(price);
        this.year = validateYear(year);
    }
    
    private String validateIsbn(String isbn) {
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
    
    private String validateStringField(String value, String fieldName) {
        if (value == null) {
            throw new NullPointerException(fieldName + " cannot be null");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
    
    private double validatePrice(double price) {
        if (price < 0.0) {
            throw new IllegalArgumentException(
                "Price cannot be negative. Provided: " + price);
        }
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new IllegalArgumentException(
                "Price must be a valid number. Provided: " + price);
        }
        return price;
    }
    
    private int validateYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < MIN_YEAR || year > currentYear + 1) {
            throw new IllegalArgumentException(
                String.format("Year must be between %d and %d. Provided: %d",
                    MIN_YEAR, currentYear + 1, year));
        }
        return year;
    }
    
    /**
     * Gets the ISBN of this book.
     * @return the ISBN (10 or 13 digits)
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * Gets the title of this book.
     * @return the book title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets the author of this book.
     * @return the author name
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Gets the price of this book.
     * @return the price in dollars
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets the publication year of this book.
     * @return the publication year
     */
    public int getYear() {
        return year;
    }
    
    /**
     * Compares this book with another book based on title (alphabetical order).
     * 
     * @param other the book to compare with
     * @return negative if this book comes before, positive if after, 0 if equal
     */
    @Override
    public int compareTo(Book other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare to null Book");
        }
        return this.title.compareToIgnoreCase(other.title);
    }
    
    /**
     * Checks if this book is equal to another object.
     * Books are considered equal if they have the same ISBN.
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal (same ISBN), false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Book)) return false;
        Book other = (Book) obj;
        return isbn.equals(other.isbn);
    }
    
    /**
     * Generates hash code based on ISBN.
     * 
     * @return hash code of the ISBN
     */
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
    
    /**
     * Returns a human-readable string representation of this book.
     * 
     * @return formatted string with book details
     */
    @Override
    public String toString() {
        return String.format("Book[ISBN=%s, Title='%s', Author='%s', Price=$%.2f, Year=%d]",
            isbn, title, author, price, year);
    }
}