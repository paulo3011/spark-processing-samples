package pmoreira.application.batchprocessing.models;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.Getter;


/**
 * Batch processing args
 */
public class Args {
    @Parameter(names = { "-positions" }, description = "Path to directory that holds positions files or file path. For example, you can use textFile(\"/my/directory\"), textFile(\"/my/directory/*.txt\"), and textFile(\"/my/directory/*.gz\")")
    @Getter
    private String sourcePosition = "C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\posicoes.csv";

    @Parameter(names = { "-poi" }, description = "Path to directory that holds point of interest files or file path. For example, you can use textFile(\"/my/directory\"), textFile(\"/my/directory/*.txt\"), and textFile(\"/my/directory/*.gz\")")
    @Getter
    private String sourcePoi = "C:\\projetos\\paulo3011\\spark-processing-samples\\latlong-processing\\src\\Data\\base_pois_def.csv";

    @Parameter(names = { "-o", "-output" }, description = "Path to output directory. Results files will be save here.")
    @Getter
    private String outputDir = "C:\\tmp\\positions\\";

    public Args(){}
    public Args(String[] argv){
        this.parse(argv);
    }

    /**
     * Parse batch parameters
     * @return Return an Args instance
     */
    public Args parse(String[] argv){
        Args parameters = new Args();

        JCommander.newBuilder()
                .addObject(this)
                .build()
                .parse(argv);

        return parameters;
    }
}
