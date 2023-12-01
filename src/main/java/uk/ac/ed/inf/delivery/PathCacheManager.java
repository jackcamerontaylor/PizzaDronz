package uk.ac.ed.inf.delivery;
import uk.ac.ed.inf.pathfinding.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The PathCacheManager class manages the caching of visited paths to improve pathfinding efficiency.
 * It uses a map to store paths based on start and end cells along with a flag indicating whether it's a way back path.
 * The caching mechanism helps avoid recalculating paths for previously visited cell combinations.
 */
public class PathCacheManager {

    private Map<PathCacheKey, List<Cell>> visitedPathsCache = new HashMap<>();

    /**
     * The PathCacheKey class represents a key for the path cache, consisting of start and end cells,
     * and a boolean flag indicating whether it's a way back path.
     */
    private static class PathCacheKey {
        private Cell start;
        private Cell end;
        private boolean isWayBack;

        /**
         * Constructs a PathCacheKey with the specified start and end cells and a way back flag.
         *
         * @param start      the starting cell of the path.
         * @param end        the ending cell of the path.
         * @param isWayBack  a boolean flag indicating whether it's a way back path.
         */
        public PathCacheKey(Cell start, Cell end, Boolean isWayBack) {
            this.start = start;
            this.end = end;
            this.isWayBack = isWayBack;
        }

        /**
         * Checks if this PathCacheKey is equal to another object.
         *
         * @param o the object to compare with.
         * @return true if the objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PathCacheKey that = (PathCacheKey) o;

            return Objects.equals(start, that.start) && Objects.equals(end, that.end);
        }

        /**
         * Generates a hash code for this PathCacheKey.
         *
         * @return the hash code.
         */
        @Override
        public int hashCode() {
            int result = Objects.hash(start, end);

            // XOR the hash code with a constant value based on direction
            result ^= isWayBack ? 1231 : 1237;

            return result;
        }
    }

    /**
     * Retrieves a visited path from the cache based on the start and end cells and the way back flag.
     *
     * @param start     the starting cell of the path.
     * @param end       the ending cell of the path.
     * @param isWayBack a boolean flag indicating whether it's a way back path.
     * @return the visited path if found in the cache, otherwise null.
     */
    public List<Cell> getVisitedPathFromCache(Cell start, Cell end, Boolean isWayBack) {
        PathCacheKey key = new PathCacheKey(start, end, isWayBack);
        return visitedPathsCache.get(key);
    }

    /**
     * Adds a visited path to the cache based on the start and end cells and the way back flag.
     *
     * @param start     the starting cell of the path.
     * @param end       the ending cell of the path.
     * @param path      the visited path to be cached.
     * @param isWayBack a boolean flag indicating whether it's a way back path.
     */
    public void addVisitedPathToCache(Cell start, Cell end, List<Cell> path, Boolean isWayBack) {
        PathCacheKey key = new PathCacheKey(start, end, isWayBack);
        visitedPathsCache.put(key, path);
    }
}

