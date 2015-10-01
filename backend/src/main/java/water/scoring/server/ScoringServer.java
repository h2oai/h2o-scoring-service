package water.scoring.server;

import java.io.IOException;

/**
 * Interface exposed by scoring server.
 */
public interface ScoringServer {

  /** Start scoring server. */
  void start() throws IOException;

  /** Stop scoring server. */
  void stop();
}
