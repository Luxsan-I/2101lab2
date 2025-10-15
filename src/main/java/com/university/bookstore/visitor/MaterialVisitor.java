package com.university.bookstore.visitor;

import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.PrintedBook;
import com.university.bookstore.model.VideoMaterial;

/**
 * Represents a visitor for performing specific operations across
 * the different subclasses of Material without modifying them.
 *
 * This interface enables extension of behavior while keeping the
 * Material hierarchy unchanged — a key idea behind the Visitor Pattern.
 * Luxsan Indran
 * 221298286
 * luxsan@my.yorku.ca
 */
public interface MaterialVisitor {

    /**
     * Handle operation for printed books.
     * @param book printed book instance
     */
    void visit(PrintedBook book);

    /**
     * Handle operation for magazines.
     * @param magazine magazine instance
     */
    void visit(Magazine magazine);

    /**
     * Handle operation for audio books.
     * @param audioBook audio book instance
     */
    void visit(AudioBook audioBook);

    /**
     * Handle operation for video-based materials.
     * @param video video material instance
     */
    void visit(VideoMaterial video);

    /**
     * Handle operation for digital eBooks.
     * @param ebook eBook instance
     */
    void visit(EBook ebook);
}
