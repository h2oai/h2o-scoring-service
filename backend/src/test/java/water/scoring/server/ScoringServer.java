package water.scoring.server;

import java.io.File;

/**
 * Interface exposed by scoring server.
 */
public interface ScoringServer {

  /** Start scoring server. */
  void start(int port, File[] pojoJars);

  /** Stop scoring server. */
  void stop();
}
