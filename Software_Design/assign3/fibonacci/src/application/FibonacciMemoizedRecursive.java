package application;

import java.util.HashMap;
import java.util.Map;

public class FibonacciMemoizedRecursive extends FibonacciRecursive {

    private static Map< Integer, Integer > fibonacciSeries = new HashMap<>();

    public int fibonacciValueAtAPosition( int position )
    {

        return fibonacciSeries.computeIfAbsent( position, super::fibonacciValueAtAPosition );
    }
}
