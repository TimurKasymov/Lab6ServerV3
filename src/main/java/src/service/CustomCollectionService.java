package src.service;

import java.util.List;

public class CustomCollectionService {

    public static <T extends Comparable<T>> WayOfOrder determineWayOfOrder(List<T> elements) {
        boolean up = true, down = true;
        for (var i = 1; i < elements.size(); i++) {
            if (elements.get(i - 1).compareTo(elements.get(i)) > 0)
                down = false;
            else if (elements.get(i - 1).compareTo(elements.get(i)) < 0)
                up = false;
            if (!(down || up))
                return WayOfOrder.NON;
        }
        if (up) {
            return WayOfOrder.ASCENDING;
        }
        return WayOfOrder.DESCENDING;
    }
}
