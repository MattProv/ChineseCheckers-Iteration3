package org.example.game_logic;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a 2D coordinate with integer values for x and y.
 * The class is immutable and provides methods for accessing the coordinate's components,
 * as well as overriding the equals and hashCode methods for comparison and storage in collections.
 */
public class Coordinate implements Serializable {

    private final int x;
    private final int y;

    /**
     * Constructs a new Coordinate with the specified x and y values.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of this Coordinate.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate of this Coordinate.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Compares this Coordinate to the specified object for equality.
     * Two Coordinates are considered equal if their x and y values are the same.
     *
     * @param o the object to compare with
     * @return true if the object is a Coordinate and has the same x and y values, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Coordinate that = (Coordinate) o;
            return this.x == that.x && this.y == that.y;
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code for this Coordinate.
     * The hash code is computed based on the x and y values.
     *
     * @return the hash code for this Coordinate
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}
