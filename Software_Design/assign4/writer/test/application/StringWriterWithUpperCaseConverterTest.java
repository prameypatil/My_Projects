package application;

import java.io.IOException;

public class StringWriterWithUpperCaseConverterTest extends WriterWithUpperCaseConverterTest {

    public StringWriter getInstance() throws IOException {

        return new StringWriter();
    }
}