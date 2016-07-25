package application;

import java.util.stream.*;

public class FibonacciNumber implements Fibonacci {

    public int fibonacciValueAtAPosition( int input )
    {

        if ( input < 0 )
            return 0;

        if ( input < 2 )
            return 1;

        return Stream.iterate( new int[]{1, 1}, e -> new int[]{e[1], e[1] + e[0]} )
                .limit( input )
                .map( e -> e[1] )
                .collect( Collectors.toList() )
                .get( input - 1 );
    }
}