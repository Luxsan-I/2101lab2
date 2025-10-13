package com.university.bookstore.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.university.bookstore.api.BookstoreAPI;
import com.university.bookstore.model.Book;

/**
 * ArrayList-based implementation of the BookstoreAPI.
 * 
 * <p>This implementation uses an ArrayList for storage and enforces
 * ISBN uniqueness. All collection returns are defensive copies to
 * maintain encapsulation.</p>
 * 
 * <p>Performance characteristics:</p>
 * <ul>
 *   <li>add: O(n) - due to uniqueness check</li>
 *   <li>removeByIsbn: O(n) - linear search required</li>
 *   <li>findByIsbn: O(n) - linear search required</li>
 *   <li>size: O(1) - ArrayList maintains size</li>
 * </ul>
 * 
 * <p>Note: This implementation is NOT thread-safe. For concurrent access,
 * consider using synchronization or ConcurrentHashMap.</p>
 * 
 * @author Navid Mohaghegh
 * @version 1.0
 * @since 2024-09-15
 */
public class BookstoreArrayList implements BookstoreAPI {
    
    private final List<Book> inventory;
    
    /**
     * Creates a new empty bookstore.
     */
    public BookstoreArrayList() {
        this.inventory = new ArrayList<>();
    }
    
    /**
     * Creates a bookstore with initial books.
     * 
     * @param initialBooks books to add initially (may be null or empty)
     */
    public BookstoreArrayList(Collection<Book> initialBooks) {
        this.inventory = new ArrayList<>();
        if (initialBooks != null) {
            for (Book book : initialBooks) {
                add(book);
            }
        }
    }
    
    @Override
    public boolean add(Book book) {
        if (book == null) {
            return false;
        }
        
        // Check for duplicate ISBN
        if (findByIsbn(book.getIsbn()) != null) {
            return false;
        }
        
        return inventory.add(book);
    }
    
    @Override
    public boolean removeByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        
        return inventory.removeIf(book -> book.getIsbn().equals(isbn));
    }
    
    @Override
    public Book findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        
        for (Book book : inventory) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }
    
    @Override
    public List<Book> findByTitle(String titleQuery) {
        if (titleQuery == null || titleQuery.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String query = titleQuery.toLowerCase().trim();
        return inventory.stream()
            .filter(book -> book.getTitle().toLowerCase().contains(query))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findByAuthor(String authorQuery) {
        if (authorQuery == null || authorQuery.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String query = authorQuery.toLowerCase().trim();
        return inventory.stream()
            .filter(book -> book.getAuthor().toLowerCase().contains(query))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException(
                "Minimum price cannot be greater than maximum price");
        }
        
        return inventory.stream()
            .filter(book -> book.getPrice() >= minPrice && book.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findByYear(int year) {
        return inventory.stream()
            .filter(book -> book.getYear() == year)
            .collect(Collectors.toList());
    }
    
    @Override
    public int size() {
        return inventory.size();
    }
    
    @Override
    public double inventoryValue() {
        return inventory.stream()
            .mapToDouble(Book::getPrice)
            .sum();
    }
    
    @Override
    public Book getMostExpensive() {
        return inventory.stream()
            .max(Comparator.comparingDouble(Book::getPrice))
            .orElse(null);
    }
    
    @Override
    public Book getMostRecent() {
        return inventory.stream()
            .max(Comparator.comparingInt(Book::getYear))
            .orElse(null);
    }
    
    @Override
    public Book[] snapshotArray() {
        return inventory.toArray(new Book[0]);
    }
    
    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(inventory);
    }
    
    /**
     * Clears all books from the inventory.
     * Useful for testing and bulk operations.
     */
    public void clear() {
        inventory.clear();
    }
    
    /**
     * Sorts the inventory by title (alphabetical order).
     */
    public void sortByTitle() {
        Collections.sort(inventory);
    }
    
    /**
     * Sorts the inventory by price (ascending).
     */
    public void sortByPrice() {
        inventory.sort(Comparator.comparingDouble(Book::getPrice));
    }
    
    /**
     * Sorts the inventory by year (ascending).
     */
    public void sortByYear() {
        inventory.sort(Comparator.comparingInt(Book::getYear));
    }
    
    /**
     * Gets statistics about the inventory.
     * 
     * @return map with statistics (size, total_value, avg_price, etc.)
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("size", size());
        stats.put("total_value", inventoryValue());
        stats.put("average_price", inventory.isEmpty() ? 0.0 : inventoryValue() / size());
        
        if (!inventory.isEmpty()) {
            stats.put("min_year", inventory.stream()
                .mapToInt(Book::getYear).min().orElse(0));
            stats.put("max_year", inventory.stream()
                .mapToInt(Book::getYear).max().orElse(0));
            stats.put("unique_authors", inventory.stream()
                .map(Book::getAuthor).distinct().count());
        }
        
        return stats;
    }
    
    @Override
    public String toString() {
        return String.format("BookstoreArrayList[size=%d, value=$%.2f]", 
            size(), inventoryValue());
    }
}