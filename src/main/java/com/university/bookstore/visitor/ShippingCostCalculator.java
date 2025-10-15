package com.university.bookstore.visitor;

import com.university.bookstore.model.*;

/**
 * Calculates shipping expenses for various material types using
 * the Visitor design pattern.
 *
 * Each concrete material type defines a unique shipping behavior:
 * - Physical books and discs: weight-based rate
 * - Magazines: fixed cost
 * - Digital items (eBooks, downloads): no cost
 *
 * This implementation separates pricing logic from the material classes.
 *
 * Luxsan Indran
 * 221298286
 * luxsan@my.yorku.ca
 */
public class ShippingCostCalculator implements MaterialVisitor {

    private static final double RATE_PER_100G = 0.50;
    private static final double MAGAZINE_RATE = 2.00;
    private static final double DIGITAL_RATE = 0.00;

    private double totalCost;

    /**
     * Visit a printed book and add its shipping charge.
     * Approximate weight: 500g.
     */
    @Override
    public void visit(PrintedBook book) {
        double weightUnits = 5.0; // 500g / 100g
        totalCost += weightUnits * RATE_PER_100G;
    }

    /**
     * Visit a magazine — always flat rate.
     */
    @Override
    public void visit(Magazine magazine) {
        totalCost += MAGAZINE_RATE;
    }

    /**
     * Visit an audiobook and add cost depending on format.
     * CDs incur cost; digital copies do not.
     */
    @Override
    public void visit(AudioBook audioBook) {
        if (audioBook.getQuality() == Media.MediaQuality.PHYSICAL) {
            totalCost += 1.0 * RATE_PER_100G; // 100g assumed
        } else {
            totalCost += DIGITAL_RATE;
        }
    }

    /**
     * Visit a video material (DVD vs digital download).
     */
    @Override
    public void visit(VideoMaterial video) {
        if (video.getQuality() == Media.MediaQuality.PHYSICAL) {
            totalCost += 1.5 * RATE_PER_100G; // ~150g
        } else {
            totalCost += DIGITAL_RATE;
        }
    }

    /**
     * Visit an eBook — shipping not applicable.
     */
    @Override
    public void visit(EBook ebook) {
        totalCost += DIGITAL_RATE;
    }

    /**
     * Return the total accumulated shipping fee.
     */
    public double getTotalShippingCost() {
        return totalCost;
    }

    /**
     * Reset calculator to handle a fresh computation.
     */
    public void reset() {
        totalCost = 0.0;
    }

    /**
     * Utility method for computing the shipping cost of a single material.
     *
     * @param material material whose shipping is to be calculated
     * @return cost associated with this material
     */
    public double calculateShippingCost(Material material) {
        reset();

        if (material instanceof PrintedBook pb) {
            visit(pb);
        } else if (material instanceof Magazine mag) {
            visit(mag);
        } else if (material instanceof AudioBook ab) {
            visit(ab);
        } else if (material instanceof VideoMaterial vm) {
            visit(vm);
        } else if (material instanceof EBook eb) {
            visit(eb);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported material type: " + material.getClass().getSimpleName()
            );
        }

        return totalCost;
    }
}
