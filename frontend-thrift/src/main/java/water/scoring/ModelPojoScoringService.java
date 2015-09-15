package water.scoring;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import water.scoring.api.ModelPojoInfo;
import water.scoring.api.ParserSetup;
import water.scoring.api.Prediction;
import water.scoring.api.ScoringService;
import water.scoring.server.ModelPojo;
import water.scoring.server.ModelPojoManager;

/**
 * Scoring service for H2O model pojos.
 */
public class ModelPojoScoringService implements ScoringService.Iface {

  @Override
  public List<ModelPojoInfo> listModelPojos() throws TException {
    ModelPojoManager mpm = ModelPojoManager.INSTANCE;
    ModelPojo[] pojos = mpm.getPojos();
    List<ModelPojoInfo> mpi = new ArrayList<>(pojos.length);
    for (ModelPojo pojo : pojos) {
      mpi.add(new ModelPojoInfo(pojo.getName(), pojo.getAlgo(), pojo.getKind()));
    }
    return mpi;
  }

  @Override
  public Prediction predictMapRow(String pojoName, Map<String, Double> row)
      throws TException {
    return null;
  }

  @Override
  public Prediction predictStringRow(String pojoName, String row, ParserSetup parserSetup)
      throws TException {
    return null;
  }

  @Override
  public Prediction predictDoubleRow(String pojoName, List<Double> row) throws TException {
    return null;
  }
}
