package pmoreira.application.batchprocessing.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArgsTest {

    @Test
    public void parse() {
        String[] argv = { "-log", "2", "-groups", "unit" };
        //Args args = Args::parse(argv);
        //assertEquals(2, args.getVerbose(), 0);
    }
}