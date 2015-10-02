package water.scoring;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Defalt command line options for scoring server
 */
public class ScoringServerOpts {

  @Parameter(names = {"-p", "--port"}, description = "Exposed server port with API.")
  public Integer port = 9090;

  @Parameter(names = { "-j", "-modelJar"}, description = "The model jar location (can be specified multiple times)")
  public List<String> pojoJars = new ArrayList<>();

  @Parameter(names = {"-h", "--help"}, help = true)
  public boolean help;
}
