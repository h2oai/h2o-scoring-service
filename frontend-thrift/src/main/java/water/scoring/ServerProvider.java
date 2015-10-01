package water.scoring;

import java.io.File;

import water.scoring.server.ScoringServer;
import water.scoring.server.ScoringServerProvider;

/**
 * Thrift API server provider
 */
public class ServerProvider implements ScoringServerProvider {

  @Override
  public ScoringServer createScoringServer(ScoringServerOpts opts) {
    File[] pojoJars = new File[opts.pojoJars.size()];
    int i = 0;
    for (String pj : opts.pojoJars) {
      pojoJars[i++] = new File(pj);
    }
    return new ThriftScoringServer(opts.port, pojoJars);
  }
}
