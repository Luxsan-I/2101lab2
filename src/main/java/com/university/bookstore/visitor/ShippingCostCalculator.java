package com.university.bookstore.visitor;

import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;
import com.university.bookstore.model.PrintedBook;
import com.university.bookstore.model.VideoMaterial;

/**
 * Visitor implementation for calculating shipping costs.
 * Demonstrates the Visitor pattern by providing different shipping
 * cost calculations based on material type.
 * 
 * <p>Shipping costs vary by material type:
 * - Physical items: $0.50 per 100g
 * - Digital items: $0 (instant download)
 * - Magazines: $2 flat rate</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class ShippingCostCalculator implements MaterialVisitor {
    
    private static final double PHYSICAL_ITEM_RATE = 0.50; // per 100g
    private static final double MAGAZINE_FLAT_RATE = 2.00;
    private static final double DIGITAL_ITEM_RATE = 0.00;
    
    private double totalShippingCost = 0.0;
    
    /**
     * Calculates shipping cost for a printed book based on weight.
     * Assumes average book weight of 500g.
     * 
     * @param book the printed book
     */
    @Override
    public void visit(PrintedBook book) {
        // Assume average book weight of 500g
        double weightInHundredGrams = 5.0;
        double cost = weightInHundredGrams * PHYSICAL_ITEM_RATE;
        totalShippingCost += cost;
    }
    
    /**
     * Calculates flat rate shipping cost for magazines.
     * 
     * @param magazine the magazine
     */
    @Override
    public void visit(Magazine magazine) {
        totalShippingCost += MAGAZINE_FLAT_RATE;
    }
    
    /**
     * Calculates shipping cost for audio books.
     * Physical CDs: weight-based, Digital: free
     * 
     * @param audioBook the audio book
     */
    @Override
    public void visit(AudioBook audioBook) {
        if (audioBook.getQuality() == Media.MediaQuality.PHYSICAL) {
            // Assume CD weight of 100g
            double weightInHundredGrams = 1.0;
            double cost = weightInHundredGrams * PHYSICAL_ITEM_RATE;
            totalShippingCost += cost;
        } else {
            // Digital download - no shipping cost
            totalShippingCost += DIGITAL_ITEM_RATE;
        }
    }
    
    /**
     * Calculates shipping cost for video materials.
     * Physical DVDs: weight-based, Digital: free
     * 
     * @param video the video material
     */
    @Override
    public void visit(VideoMaterial video) {
        if (video.getQuality() == Media.MediaQuality.PHYSICAL) {
            // Assume DVD weight of 150g
            double weightInHundredGrams = 1.5;
            double cost = weightInHundredGrams * PHYSICAL_ITEM_RATE;
            totalShippingCost += cost;
        } else {
            // Digital download - no shipping cost
            totalShippingCost += DIGITAL_ITEM_RATE;
        }
    }
    
    /**
     * Calculates shipping cost for e-books.
     * E-books are always digital downloads with no shipping cost.
     * 
     * @param ebook the e-book
     */
    @Override
    public void visit(EBook ebook) {
        // E-books are always digital - no shipping cost
        totalShippingCost += DIGITAL_ITEM_RATE;
    }
    
    /**
     * Gets the total shipping cost calculated so far.
     * 
     * @return the total shipping cost
     */
    public double getTotalShippingCost() {
        return totalShippingCost;
    }
    
    /**
     * Resets the shipping cost calculator for a new calculation.
     */
    public void reset() {
        totalShippingCost = 0.0;
    }
    
    /**
     * Calculates shipping cost for a single material.
     * 
     * @param material the material to calculate shipping for
     * @return the shipping cost for this material
     */
    public double calculateShippingCost(Material material) {
        reset();
        
        // Use pattern matching or instanceof to determine the correct visit method
        if (material instanceof PrintedBook) {
            visit((PrintedBook) material);
        } else if (material instanceof Magazine) {
            visit((Magazine) material);
        } else if (material instanceof AudioBook) {
            visit((AudioBook) material);
        } else if (material instanceof VideoMaterial) {
            visit((VideoMaterial) material);
        } else if (material instanceof EBook) {
            visit((EBook) material);
        } else {
            throw new IllegalArgumentException("Unknown material type: " + material.getClass().getSimpleName());
        }
        
        return totalShippingCost;
    }
}
