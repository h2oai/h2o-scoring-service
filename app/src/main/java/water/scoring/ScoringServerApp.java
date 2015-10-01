package water.scoring;

import com.beust.jcommander.JCommander;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import water.scoring.server.ScoringServer;

/**
 * Scoring application entry point.
 *
 * The exposed front-end depends on implementation on ServerProvider.
 * By default it returns Thrift based API server.
 */
public class ScoringServerApp {

  private static final Logger logger = LogManager.getLogger(ScoringServerApp.class);

  public static void main(String[] args) {
    ScoringServerOpts opts = new ScoringServerOpts();
    // Parse
    new JCommander(opts, args);

    ScoringServer ssa = null;
    try {
      // Create a scoring server.
      // It uses a simple link approach - at assembly time we can provide different implementations
      // for ServerProvider.
      ssa = new ServerProvider().createScoringServer(opts);
      // Start the server
      ssa.start();
    } catch (Exception e) {
      logger.error("Problem detected during start of scoring server!", e);
    } finally {
      if (ssa != null) {
        ssa.stop();
      }
    }

  }
}
