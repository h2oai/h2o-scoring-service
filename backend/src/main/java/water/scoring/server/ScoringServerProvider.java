package water.scoring.server;

import water.scoring.ScoringServerOpts;

/**
 * A ScoringServer provider.
 *
 * Motivation behind this interface is that we would like
 * to separate strategy of server creation from actual application.
 *
 */
public interface ScoringServerProvider {

  /** Create an instance of scoring server for given parameters. */
  ScoringServer createScoringServer(ScoringServerOpts opts);

}
