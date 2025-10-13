package com.university.bookstore.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.university.bookstore.model.Book;

/**
 * Utility class for array-based operations on Book objects.
 * 
 * <p>This class provides static methods for manipulating and analyzing
 * arrays of books, demonstrating array operations without using ArrayList.</p>
 * 
 * <p>All methods handle null arrays and null elements gracefully.</p>
 * 
 * @author Navid Mohaghegh
 * @version 1.0
 * @since 2024-09-15
 */
public final class BookArrayUtils {
    
    /**
     * Private constructor to prevent instantiation.
     */
    private BookArrayUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Counts books published before a given year.
     * 
     * @param books array of books (may be null or contain nulls)
     * @param yearCutoff the cutoff year (exclusive)
     * @return count of books published before the cutoff year
     */
    public static int countBeforeYear(Book[] books, int yearCutoff) {
        if (books == null) {
            return 0;
        }
        
        int count = 0;
        for (Book book : books) {
            if (book != null && book.getYear() < yearCutoff) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Counts books by a specific author (case-insensitive, exact match).
     * 
     * @param books array of books (may be null or contain nulls)
     * @param author the author name to search for
     * @return count of books by the specified author
     */
    public static int countByAuthor(Book[] books, String author) {
        if (books == null || author == null) {
            return 0;
        }
        
        int count = 0;
        for (Book book : books) {
            if (book != null && book.getAuthor().equalsIgnoreCase(author)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Filters books with price at most the specified maximum.
     * Returns a compact array (no nulls, exact size).
     * 
     * @param books array of books (may be null or contain nulls)
     * @param maxPrice maximum price (inclusive)
     * @return compact array of books with price less than or equal to maxPrice
     * @throws IllegalArgumentException if maxPrice is negative
     */
    public static Book[] filterPriceAtMost(Book[] books, double maxPrice) {
        if (maxPrice < 0) {
            throw new IllegalArgumentException("Max price cannot be negative");
        }
        
        if (books == null) {
            return new Book[0];
        }
        
        // Count matching books
        int count = 0;
        for (Book book : books) {
            if (book != null && book.getPrice() <= maxPrice) {
                count++;
            }
        }
        
        // Create compact array
        Book[] result = new Book[count];
        int index = 0;
        for (Book book : books) {
            if (book != null && book.getPrice() <= maxPrice) {
                result[index++] = book;
            }
        }
        
        return result;
    }
    
    /**
     * Filters books published in a specific decade.
     * For example, decade 1990 includes years 1990-1999.
     * 
     * @param books array of books (may be null or contain nulls)
     * @param decade the decade start year (e.g., 1990, 2000)
     * @return compact array of books from that decade
     */
    public static Book[] filterByDecade(Book[] books, int decade) {
        if (books == null) {
            return new Book[0];
        }
        
        int decadeEnd = decade + 9;
        
        // Count matching books
        int count = 0;
        for (Book book : books) {
            if (book != null && book.getYear() >= decade && book.getYear() <= decadeEnd) {
                count++;
            }
        }
        
        // Create compact array
        Book[] result = new Book[count];
        int index = 0;
        for (Book book : books) {
            if (book != null && book.getYear() >= decade && book.getYear() <= decadeEnd) {
                result[index++] = book;
            }
        }
        
        return result;
    }
    
    /**
     * Sorts books by price in ascending order (in-place).
     * Nulls are moved to the end.
     * 
     * @param books array to sort (modified in-place)
     */
    public static void sortByPrice(Book[] books) {
        if (books == null || books.length <= 1) {
            return;
        }
        
        Arrays.sort(books, (a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            return Double.compare(a.getPrice(), b.getPrice());
        });
    }
    
    /**
     * Sorts books by year in ascending order (in-place).
     * Nulls are moved to the end.
     * 
     * @param books array to sort (modified in-place)
     */
    public static void sortByYear(Book[] books) {
        if (books == null || books.length <= 1) {
            return;
        }
        
        Arrays.sort(books, (a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            return Integer.compare(a.getYear(), b.getYear());
        });
    }
    
    /**
     * Calculates the average price of books in the array.
     * 
     * @param books array of books (may be null or contain nulls)
     * @return average price, or 0.0 if array is null or empty
     */
    public static double averagePrice(Book[] books) {
        if (books == null) {
            return 0.0;
        }
        
        double sum = 0.0;
        int count = 0;
        
        for (Book book : books) {
            if (book != null) {
                sum += book.getPrice();
                count++;
            }
        }
        
        return count == 0 ? 0.0 : sum / count;
    }
    
    /**
     * Finds the oldest book (earliest publication year).
     * 
     * @param books array of books (may be null or contain nulls)
     * @return the oldest book, or null if array is null/empty
     */
    public static Book findOldest(Book[] books) {
        if (books == null) {
            return null;
        }
        
        Book oldest = null;
        for (Book book : books) {
            if (book != null) {
                if (oldest == null || book.getYear() < oldest.getYear()) {
                    oldest = book;
                }
            }
        }
        
        return oldest;
    }
    
    /**
     * Merges two book arrays into one, preserving all elements.
     * 
     * @param arr1 first array (may be null)
     * @param arr2 second array (may be null)
     * @return merged array containing all books from both arrays
     */
    public static Book[] merge(Book[] arr1, Book[] arr2) {
        int len1 = (arr1 == null) ? 0 : arr1.length;
        int len2 = (arr2 == null) ? 0 : arr2.length;
        
        Book[] result = new Book[len1 + len2];
        
        if (arr1 != null) {
            System.arraycopy(arr1, 0, result, 0, len1);
        }
        if (arr2 != null) {
            System.arraycopy(arr2, 0, result, len1, len2);
        }
        
        return result;
    }
    
    /**
     * Removes duplicate books based on ISBN.
     * Returns a compact array with unique books only.
     * 
     * @param books array of books (may be null or contain nulls)
     * @return compact array with duplicates removed
     */
    public static Book[] removeDuplicates(Book[] books) {
        if (books == null) {
            return new Book[0];
        }
        
        Set<String> seenIsbns = new HashSet<>();
        List<Book> unique = new ArrayList<>();
        
        for (Book book : books) {
            if (book != null && seenIsbns.add(book.getIsbn())) {
                unique.add(book);
            }
        }
        
        return unique.toArray(new Book[0]);
    }
    
    /**
     * Finds books within a year range (inclusive).
     * 
     * @param books array of books
     * @param startYear start year (inclusive)
     * @param endYear end year (inclusive)
     * @return compact array of books within the year range
     */
    public static Book[] filterByYearRange(Book[] books, int startYear, int endYear) {
        if (books == null || startYear > endYear) {
            return new Book[0];
        }
        
        return Stream.of(books)
            .filter(book -> book != null && 
                    book.getYear() >= startYear && 
                    book.getYear() <= endYear)
            .toArray(Book[]::new);
    }
    
    /**
     * Groups books by decade and returns a summary.
     * 
     * @param books array of books
     * @return map with decade as key and count as value
     */
    public static Map<Integer, Integer> countByDecade(Book[] books) {
        Map<Integer, Integer> decadeCounts = new TreeMap<>();
        
        if (books != null) {
            for (Book book : books) {
                if (book != null) {
                    int decade = (book.getYear() / 10) * 10;
                    decadeCounts.merge(decade, 1, Integer::sum);
                }
            }
        }
        
        return decadeCounts;
    }
    
    /**
     * Finds the book with the longest title.
     * 
     * @param books array of books
     * @return book with longest title, null if array is null/empty
     */
    public static Book findLongestTitle(Book[] books) {
        if (books == null) {
            return null;
        }
        
        Book longest = null;
        int maxLength = 0;
        
        for (Book book : books) {
            if (book != null && book.getTitle().length() > maxLength) {
                maxLength = book.getTitle().length();
                longest = book;
            }
        }
        
        return longest;
    }
}