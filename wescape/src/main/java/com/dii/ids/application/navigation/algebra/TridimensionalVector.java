package com.dii.ids.application.navigation.algebra;

import android.graphics.PointF;

public class TridimensionalVector {
    public final double x;
    public final double y;
    public final double z;

    /**
     * Costruzione del vettore da due punti complanari
     * @param begin
     * @param end
     */
    public TridimensionalVector(PointF begin, PointF end) {
        x = end.x - begin.x;
        y = end.y - end.y;
        z = 0;
    }

    /**
     * Costruzione del vettore dalle coordinate
     * @param x
     * @param y
     * @param z
     */
    public TridimensionalVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Prodotto scalare
     * @param otherVector
     * @return
     */
    public double dotProduct(TridimensionalVector otherVector) {
        return (x * otherVector.x) + (y * otherVector.y);
    }

    /**
     * Prodotto vettoriale
     * @param otherVector
     * @return
     */
    public TridimensionalVector vectorProduct(TridimensionalVector otherVector) {
        return new TridimensionalVector(
                (this.y * otherVector.z) - (otherVector.y * this.z),
                (this.x * otherVector.z) - (otherVector.x * this.z),
                (this.x * otherVector.y) - (otherVector.x * this.y)
        );
    }

    /**
     * Modulo
     * @return
     */
    public double module() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double cosine(TridimensionalVector vector) {
        return dotProduct(vector) / (module() * vector.module());
    }

    public double sine(TridimensionalVector vector) {
        TridimensionalVector vectorProduct = vectorProduct(vector);
        return vectorProduct.module() / (module() * vector.module());
    }

    public double getPlaneAngle(TridimensionalVector vector) {
        double sine, cosine, planeAngle;
        sine = sine(vector);
        cosine = cosine(vector);

        planeAngle = Math.acos(cosine);
        if (sine < 0) {
            planeAngle = (2 * Math.PI) - planeAngle;
        }

        return planeAngle;
    }
}
