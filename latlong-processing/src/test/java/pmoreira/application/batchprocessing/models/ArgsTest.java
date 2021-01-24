package pmoreira.application.batchprocessing.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArgsTest {

    @Test
    public void parse() {
        String path = "~/tmp/position_processing";
        String[] argv = { "-o", path};
        Args args = new Args(argv);
        assertEquals("expected: " + path, path, args.getOutputDir());
    }
}