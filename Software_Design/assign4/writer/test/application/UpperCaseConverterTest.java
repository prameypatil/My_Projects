package application;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpperCaseConverterTest implements UpperCaseConverter {

    @Test
    public void convertToUpperCaseConvertsStingToLowerCase()
    {
        assertEquals( "HELLO", UpperCaseConverter.convertToUpperCase("hello") );
    }

    @Test
    public void convertToUpperCaseDoesNotChangeStringAlreadyInLowerCase()
    {
        assertEquals( "HELLO", UpperCaseConverter.convertToUpperCase("HELLO") );
    }

    @Test
    public void convertToUpperCaseConvertsAllUpperCaseCharactersToLowerCase()
    {
        assertEquals( "HELLO", UpperCaseConverter.convertToUpperCase("hello") );
    }

    @Test
    public void convertToUpperCaseDoesNotChangeSpecialCharactersInString()
    {
        assertEquals( "HELLO@!", UpperCaseConverter.convertToUpperCase("hello@!") );
    }
}
