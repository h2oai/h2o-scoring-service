package water.scoring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import java.io.File;
import java.io.IOException;

import water.scoring.server.ModelPojo;
import water.scoring.server.ModelPojoManager;
import water.scoring.server.ScoringServer;

import static water.scoring.api.ScoringService.Processor;

/**
 * Scoring application entry point.
 *
 * This one is using thrift as a front-end API provider.
 */
public class ThriftScoringServer implements ScoringServer {

  private static final Logger logger = LogManager.getLogger(ThriftScoringServer.class);

  private final int port;
  private final File[] pojoJars;

  ThriftScoringServer(int port, File[] pojoJars) {
    this.port = port;
    this.pojoJars = pojoJars;
  }

  @Override
  public void start() throws IOException {
    ModelPojoManager mpm = ModelPojoManager.INSTANCE;
    for (File f : pojoJars) {
      try {
        ModelPojo[] pojos = mpm.loadModelPojos(f.toURI());
        logger.info("From " + f + " loaded " + pojos.length + " pojos:");
        for (ModelPojo mp : pojos) {
          logger.info(" - " + mp);
        }
      } catch (Exception e) {
        logger.error("Cannot load model POJOs from specified location: " + f);
      }
    }

    // Start service
    try {
      ModelPojoScoringService handler = new ModelPojoScoringService(mpm);
      Processor processor = new Processor(handler);
      TServerSocket serverTransport = new TServerSocket(port);
      TServer.Args serverArgs = new TServer.Args(serverTransport).processor(processor);
      TSimpleServer server = new TSimpleServer(serverArgs);

      // Run it
      logger.info(
          "Serving API on " + serverTransport.getServerSocket().getInetAddress().toString() + ":"
          + serverTransport.getServerSocket().getLocalPort());
      server.serve();
    } catch (TTransportException e) {
      throw new IOException(e);
    } finally {
      logger.info("Stopping serving API");
    }
  }

  @Override
  public void stop() {
  }
}
