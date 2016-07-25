package application;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class WriterTest {

    public Writer writer;

    @Before
    public void setUp() throws IOException{

        writer = getInstance();
    }

    public abstract Writer getInstance() throws IOException;

    @Test
    public void canary()
    {
        assertTrue( true );
    }

    @Test
    public void writeWritesAStringToAWriter() throws IOException
    {
        writer.write( "Hello" );
        assertEquals( "Hello", writer.getContent() );
    }

    @Test
    public void writeReturnsAnEmptyStringForAnEmptyInputString() throws IOException
    {
        writer.write("");

        assertEquals( "", writer.getContent() );
    }
}
