package uk.ac.ed.inf.pathfinding;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Objects;

// Cell class to help record status of grid
// Since we are using priority queue and hashset, implement some functions, e.g., hashCode, equals are needed
public class Cell implements Comparable<Cell> {
    double lng, lat;   // cell position
    double f,g,h;
    Cell parent;    // parent record: come from
    LngLat cellLngLat;    // cells LngLat position so we can use LngLatHandler

    public Cell(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
        cellLngLat = new LngLat(lng, lat);
        parent = null;
        f = 0;
        g = 0;
        h = 0;
    }

    @Override
    public int hashCode(){
        return Objects.hash(lng, lat);
    }

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

    @Override
    public int compareTo(Cell other) {
        // Implement your comparison logic based on 'f' values
        return Double.compare(this.f, other.f);
    }

}
