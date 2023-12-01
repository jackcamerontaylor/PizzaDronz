package uk.ac.ed.inf.pathfinding;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Objects;

/**
 * The Cell class represents a cell in a grid and is used to record the status of the grid.
 * It implements Comparable to enable comparison based on 'f' scores for priority queue management.
 */
public class Cell implements Comparable<Cell> {
    double lng, lat;   // cell position
    double f,g,h;
    Cell parent;    // parent record: come from
    LngLat cellLngLat;    // cells LngLat position so we can use LngLatHandler
    double fromAngle;

    /**
     * Constructs a Cell with the specified longitude and latitude.
     *
     * @param lng the longitude of the cell.
     * @param lat the latitude of the cell.
     */
    public Cell(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
        cellLngLat = new LngLat(lng, lat);
        parent = null;
        f = 0;
        g = 0;
        h = 0;
        fromAngle = 0;
    }

    /**
     * Generates a hash code for this Cell based on its longitude and latitude.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode(){
        return Objects.hash(lng, lat);
    }

    /**
     * Checks if this Cell is equal to another object.
     *
     * @param obj the object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        Cell other = (Cell)obj;
        return other.lng == lng && other.lat == lat;
    }

    /**
     * Compares this Cell to another based on 'f' values for priority queue management.
     *
     * @param other the Cell to compare with.
     * @return a negative integer, zero, or a positive integer as this Cell is less than, equal to, or greater than the specified Cell.
     */
    @Override
    public int compareTo(Cell other) {
        // Implement your comparison logic based on 'f' values
        return Double.compare(this.f, other.f);
    }

    /**
     * Gets the longitude of the cell.
     *
     * @return the longitude.
     */
    public double lng() {
        return this.lng;
    }

    /**
     * Gets the latitude of the cell.
     *
     * @return the latitude.
     */
    public double lat() {
        return this.lat;
    }

    /**
     * Gets the angle of movement from the parent cell.
     *
     * @return the angle.
     */
    public double angle() {
        return this.fromAngle;
    }

    /**
     * Gets the current LngLat position of the cell.
     *
     * @return the current LngLat position.
     */
    public LngLat currentLngLat() {
        return this.cellLngLat;
    }

    /**
     * Gets the LngLat position of the parent cell.
     *
     * @return the parent LngLat position.
     */
    public LngLat parentLngLat() {
        return this.parent.cellLngLat;
    }
}
