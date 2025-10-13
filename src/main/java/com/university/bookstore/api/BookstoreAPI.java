package com.university.bookstore.api;

import java.util.List;

import com.university.bookstore.model.Book;

/**
 * Defines the contract for bookstore operations.
 * 
 * <p>This interface provides a API for managing a book inventory,
 * including CRUD operations, search functionality, and analytics.</p>
 * 
 * <p>Implementations should ensure thread-safety if concurrent access is expected.</p>
 * 
 * @author Navid Mohaghegh
 * @version 1.0
 * @since 2024-09-15
 */
public interface BookstoreAPI {
    
    /**
     * Adds a book to the inventory.
     * Duplicate ISBNs are not allowed.
     * 
     * @param book the book to add (non-null)
     * @return true if the book was added, false if ISBN already exists or book is null
     */
    boolean add(Book book);
    
    /**
     * Removes a book from the inventory by ISBN.
     * 
     * @param isbn the ISBN of the book to remove
     * @return true if a book was removed, false if no book with that ISBN exists
     */
    boolean removeByIsbn(String isbn);
    
    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return the book if found, null otherwise
     */
    Book findByIsbn(String isbn);
    
    /**
     * Searches for books by title (case-insensitive, partial match).
     * 
     * @param titleQuery the title or partial title to search for
     * @return list of matching books (never null, may be empty)
     */
    List<Book> findByTitle(String titleQuery);
    
    /**
     * Searches for books by author (case-insensitive, partial match).
     * 
     * @param authorQuery the author name or partial name to search for
     * @return list of matching books (never null, may be empty)
     */
    List<Book> findByAuthor(String authorQuery);
    
    /**
     * Finds all books within a price range (inclusive).
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of books within the price range
     * @throws IllegalArgumentException if minPrice > maxPrice or prices are negative
     */
    List<Book> findByPriceRange(double minPrice, double maxPrice);
    
    /**
     * Finds all books published in a specific year.
     * 
     * @param year the publication year
     * @return list of books published in that year
     */
    List<Book> findByYear(int year);
    
    /**
     * Gets the number of books in the inventory.
     * 
     * @return the total number of books
     */
    int size();
    
    /**
     * Calculates the total value of all books in inventory.
     * 
     * @return sum of all book prices
     */
    double inventoryValue();
    
    /**
     * Finds the most expensive book in the inventory.
     * 
     * @return the book with highest price, null if inventory is empty
     */
    Book getMostExpensive();
    
    /**
     * Finds the most recently published book.
     * 
     * @return the book with the latest publication year, null if inventory is empty
     */
    Book getMostRecent();
    
    /**
     * Creates a defensive copy of the inventory as an array.
     * Changes to the returned array will not affect the inventory.
     * 
     * @return array containing all books in the inventory
     */
    Book[] snapshotArray();
    
    /**
     * Gets all books in the inventory.
     * The returned list is a defensive copy.
     * 
     * @return list of all books (never null, may be empty)
     */
    List<Book> getAllBooks();
}