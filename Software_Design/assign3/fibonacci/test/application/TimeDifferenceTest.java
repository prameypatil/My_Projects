package application;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TimeDifferenceTest {

    public long calculateResponseTime( Fibonacci fibonacci )
    {
        long beginTime = System.nanoTime();
        fibonacci.fibonacciValueAtAPosition(40);
        long endTime = System.nanoTime();

        return endTime - beginTime;
    }

    @Test
    public void testForMemoizedVersionIsFasterThanRecursiveVersion()
    {

        FibonacciRecursive fibonacciRecursive = new FibonacciRecursive();
        FibonacciMemoizedRecursive fibonacciMemoizedRecursive = new FibonacciMemoizedRecursive();

        long recursiveTime = calculateResponseTime( fibonacciRecursive );
        long memoizedTime = calculateResponseTime( fibonacciMemoizedRecursive );

        assertTrue( recursiveTime / memoizedTime >= 10 );
    }
}
