package pmoreira.application.batchprocessing.models;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Args {
    @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
    @Getter
    private int verbose = 1;

    /**
     * Parse batch parameters
     * @return Return an Args instance
     */
    public static Args parse(String[] argv){
        Args parameters = new Args();

        JCommander.newBuilder()
                .addObject(parameters)
                .build()
                .parse(argv);

        return parameters;
    }
}
