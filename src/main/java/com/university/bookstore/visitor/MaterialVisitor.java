package com.university.bookstore.visitor;

import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.PrintedBook;
import com.university.bookstore.model.VideoMaterial;

/**
 * Visitor interface for implementing the Visitor pattern.
 * Allows adding new operations to the Material hierarchy without
 * modifying existing classes.
 * 
 * <p>This pattern is particularly useful for operations that need
 * to behave differently based on the concrete type of Material.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public interface MaterialVisitor {
    
    /**
     * Visits a PrintedBook.
     * 
     * @param book the printed book to visit
     */
    void visit(PrintedBook book);
    
    /**
     * Visits a Magazine.
     * 
     * @param magazine the magazine to visit
     */
    void visit(Magazine magazine);
    
    /**
     * Visits an AudioBook.
     * 
     * @param audioBook the audio book to visit
     */
    void visit(AudioBook audioBook);
    
    /**
     * Visits a VideoMaterial.
     * 
     * @param video the video material to visit
     */
    void visit(VideoMaterial video);
    
    /**
     * Visits an EBook.
     * 
     * @param ebook the e-book to visit
     */
    void visit(EBook ebook);
}
