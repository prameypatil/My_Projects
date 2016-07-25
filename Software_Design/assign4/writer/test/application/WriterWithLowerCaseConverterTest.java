package application;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public abstract class WriterWithLowerCaseConverterTest implements LowerCaseConverter {

    public Writer writer;

    @Before
    public void setUp() throws IOException{

        writer = getInstance();
    }

    public abstract Writer getInstance() throws IOException;

    @Test
    public void writeAStringAndConvertItToLowercase()throws IOException
    {

        writer.setOperators(LowerCaseConverter::convertToLowerCase);

        writer.write( "HELLO" );

        assertEquals( "hello", writer.getContent() );
    }
}
