package application;

import java.io.IOException;

public class StringWriterWithDuplicateRemoverTest extends WriterWithDuplicateRemoverTest {

    public StringWriter getInstance() throws IOException {

        return new StringWriter();
    }
}
