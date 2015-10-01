package water.scoring;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michal on 10/1/15.
 */
public class ScoringServerOpts {

  @Parameter(names = {"-p", "--port"}, description = "Server port to expose API.")
  public Integer port = 9090;

  @Parameter(names = { "-j", "-pojoJar"}, description = "The model pojo jar location (can be specified multiple times)")
  public List<String> pojoJars = new ArrayList<>();

  @Parameter(names = {"-h", "--help"}, help = true)
  public boolean help;
}
