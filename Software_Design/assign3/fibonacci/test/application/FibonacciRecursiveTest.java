package application;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FibonacciRecursiveTest  extends FibonacciTest {

    public FibonacciRecursive getInstance()
    {

        return new FibonacciRecursive();
    }

    @Test
    public void fibonacciRecursiveIsRecursive()
    {
        FibonacciRecursive fibonacciRecursive = Mockito.spy( new FibonacciRecursive() );

        fibonacciRecursive.fibonacciValueAtAPosition(3);

        verify( fibonacciRecursive, times(5) ).fibonacciValueAtAPosition( any( Integer.class ) );
    }
}