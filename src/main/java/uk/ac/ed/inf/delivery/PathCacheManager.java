package uk.ac.ed.inf.delivery;
import uk.ac.ed.inf.pathfinding.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PathCacheManager {

    private Map<PathCacheKey, List<Cell>> visitedPathsCache = new HashMap<>();

    private static class PathCacheKey {
        private Cell start;
        private Cell end;
        private boolean isWayBack;

        public PathCacheKey(Cell start, Cell end, Boolean isWayBack) {
            this.start = start;
            this.end = end;
            this.isWayBack = isWayBack;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PathCacheKey that = (PathCacheKey) o;

            return Objects.equals(start, that.start) && Objects.equals(end, that.end);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(start, end);

            // XOR the hash code with a constant value based on direction
            result ^= isWayBack ? 1231 : 1237;

            return result;
        }
    }

    public List<Cell> getVisitedPathFromCache(Cell start, Cell end, Boolean isWayBack) {
        PathCacheKey key = new PathCacheKey(start, end, isWayBack);
        return visitedPathsCache.get(key);
    }

    public void addVisitedPathToCache(Cell start, Cell end, List<Cell> path, Boolean isWayBack) {
        PathCacheKey key = new PathCacheKey(start, end, isWayBack);
        visitedPathsCache.put(key, path);
    }
}

