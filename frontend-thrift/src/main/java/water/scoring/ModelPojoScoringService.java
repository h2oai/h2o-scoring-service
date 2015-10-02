package water.scoring;

import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import hex.ModelCategory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.AbstractPredictException;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.ClusteringModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;
import water.genmodel.IGeneratedModel;
import water.scoring.api.BinomialPrediction;
import water.scoring.api.ClusteringPrediction;
import water.scoring.api.FeatureInfo;
import water.scoring.api.FeatureType;
import water.scoring.api.ModelInfo;
import water.scoring.api.ModelNotFoundException;
import water.scoring.api.MultinomialPrediction;
import water.scoring.api.ParserSetup;
import water.scoring.api.PredictException;
import water.scoring.api.Prediction;
import water.scoring.api.PredictionHolder;
import water.scoring.api.RegressiongPrediction;
import water.scoring.api.ScoringService;
import water.scoring.api.UnsupportedModelCategoryException;
import water.scoring.server.ModelPojo;
import water.scoring.server.ModelPojoManager;

/**
 * Scoring service for H2O model POJOs.
 */
public class ModelPojoScoringService implements ScoringService.Iface {

  /** Reference to pojo manager */
  private final ModelPojoManager mpm;

  public ModelPojoScoringService(ModelPojoManager mpm) {
    this.mpm = mpm;
  }

  @Override
  public List<ModelInfo> listModels() throws TException {
    ModelPojoManager mpm = getMpm();
    ModelPojo[] pojos = mpm.getPojos();
    List<ModelInfo> mpi = new ArrayList<>(pojos.length);
    for (ModelPojo pojo : pojos) {
      mpi.add(toModelInfo(pojo));
    }
    return mpi;
  }

  @Override
  public ModelInfo modelInfo(String modelId) throws ModelNotFoundException, TException {
    ModelPojoManager mpm = getMpm();
    ModelPojo modelPojo = getModelPojoOrThrows(mpm, modelId);
    return toModelInfo(modelPojo);
  }

  @Override
  public Prediction predictMapRow(String modelId, Map<String, Double> row)
      throws ModelNotFoundException, TException {
    ModelPojoManager mpm = getMpm();
    EasyPredictModelWrapper model = getModelWrapperOrThrows(mpm, modelId);
    // Prepare data
    RowData rowData = new RowData();
    for (Map.Entry<String, Double> e : row.entrySet()) {
      rowData.put(e.getKey(), e.getValue());
    }
    // Make prediction
    try {
      Prediction prediction = predict(model, rowData);
      return prediction;
    } catch (AbstractPredictException e) {
      throw new PredictException(e.getMessage());
    }
  }

  @Override
  public Prediction predictStringRow(String modelId, String row, ParserSetup parserSetup)
      throws ModelNotFoundException, TException {
    ModelPojoManager mpm = getMpm();
    return null;
  }

  @Override
  public Prediction predictDoubleRow(String modelId, List<Double> row)
      throws ModelNotFoundException, TException {
    ModelPojoManager mpm = getMpm();
    ModelPojo modelPojo = getModelPojoOrThrows(mpm, modelId);
    String[] names = modelPojo.getModel().getNames();
    int featuresCnt = modelPojo.getModel().getNumCols();
    if (featuresCnt != row.size()) {
      throw new PredictException("Number of columns does not match to the expected number!");
    }
    // Prepare data
    RowData rowData = new RowData();
    for (int i = 0; i < names.length; i++) {
      rowData.put(names[i], row.get(i));
    }

    // Make prediction
    try {
      Prediction prediction = predict(modelPojo.getPredictWrapper(), rowData);
      return prediction;
    } catch (AbstractPredictException e) {
      throw new PredictException(e.getMessage());
    }
  }

  @Override
  public ModelInfo deployPojoJar(ByteBuffer modelJar) throws TException {
    ModelPojoManager mpm = getMpm();
    ModelPojo modelPojo = mpm.loadModelPojo(modelJar);
    return toModelInfo(modelPojo);
  }

  @Override
  public FeatureInfo featureInfoByName(String modelId, String name, int categoricalOff, int categoricalLen)
      throws TException {
    ModelPojoManager mpm = getMpm();
    ModelPojo modelPojo = getModelPojoOrThrows(mpm, modelId);
    String[] values = modelPojo.getModel().getDomainValues(name);
    return getFeatureInfo(name, values, categoricalOff, categoricalLen);
  }

  @Override
  public FeatureInfo featureInfoByIdx(String modelId, int idx, int categoricalOff, int categoricalLen)
      throws TException {
    ModelPojoManager mpm = getMpm();
    ModelPojo modelPojo = getModelPojoOrThrows(mpm, modelId);
    String[] values = modelPojo.getModel().getDomainValues(idx);
    String name = modelPojo.getColumNames()[idx];
    return getFeatureInfo(name, values, categoricalOff, categoricalLen);
  }

  private ModelPojoManager getMpm() {
    return this.mpm; //ModelPojoManager.INSTANCE;
  }

  private Prediction predict(EasyPredictModelWrapper model, RowData rowData) throws
                                                                             AbstractPredictException,
                                                                             UnsupportedModelCategoryException {
    ModelCategory cat = model.getModelCategory();
    switch (cat) {
      case Binomial: {
        BinomialModelPrediction p = model.predictBinomial(rowData);
        PredictionHolder
            ph =
            PredictionHolder
                .binomialPrediction(new BinomialPrediction(p.label, toList(p.classProbabilities)));
        return new Prediction(cat.name(), ph);
      }

      case Multinomial: {
        MultinomialModelPrediction p = model.predictMultinomial(rowData);
        PredictionHolder ph = PredictionHolder.multinomialPrediction(
                new MultinomialPrediction(p.label, toList(p.classProbabilities)));
        return new Prediction(cat.name(), ph);
      }

      case Regression: {
        RegressionModelPrediction p = model.predictRegression(rowData);
        PredictionHolder ph = PredictionHolder.regressionPrediction(
            new RegressiongPrediction(p.value));
        return new Prediction(cat.name(), ph);
      }

      case Clustering: {
        ClusteringModelPrediction p = model.predictClustering(rowData);
        PredictionHolder ph = PredictionHolder.clusteringPrediction(
            new ClusteringPrediction(p.cluster));
        return new Prediction(cat.name(), ph);
      }
        
      default: throw new UnsupportedModelCategoryException(model.getModelCategory().name());
    }
  }

  private ModelPojo getModelPojoOrThrows(ModelPojoManager mpm, String modelId) throws ModelNotFoundException {
    ModelPojo modelPojo = mpm.getPojoByName(modelId);
    if (modelPojo != null) {
      return modelPojo;
    } else {
      throw new ModelNotFoundException(modelId);
    }
  }

  private EasyPredictModelWrapper getModelWrapperOrThrows(ModelPojoManager mpm, String modelId) throws ModelNotFoundException {
    ModelPojo modelPojo = getModelPojoOrThrows(mpm, modelId);
    return modelPojo.getPredictWrapper();
  }

  private ModelInfo toModelInfo(ModelPojo modelPojo) {
    int nfeatures = modelPojo.getFeatureCount();
    ArrayList<FeatureType> types = new ArrayList(nfeatures);
    IGeneratedModel genModel = modelPojo.getModel();
    for (int i = 0; i < nfeatures; i++) {
      String[] dom = genModel.getDomainValues(i);
      types.add(dom != null ? FeatureType.CATEGORICAL : FeatureType.NUMERIC);
    }
    return new ModelInfo(modelPojo.getName(),
                         modelPojo.getAlgo(),
                         modelPojo.getKind(),
                         modelPojo.getCategory().name(),
                         nfeatures,
                         Arrays.asList(modelPojo.getColumNames()),
                         types);
  }

  private List<Double> toList(double[] l) {
    ArrayList<Double> list = new ArrayList<>(l.length);
    for (double n : l) list.add(n);
    return list;
  }

  private FeatureInfo getFeatureInfo(String name, String[] dom, int categoricalOff, int categoricalLen) throws TException {
    if (dom != null) {
      if (categoricalOff < 0 || categoricalOff > dom.length || categoricalOff + categoricalLen > dom.length) {
        throw new TException("Wrong values for categoricalOff or categoricalLen, should fit into the interval (0, " + dom.length +")");
      }
      String[] domPreview = new String[categoricalLen];
      System.arraycopy(dom, categoricalOff, domPreview, 0, categoricalLen);
      return new FeatureInfo(name, FeatureType.CATEGORICAL)
          .setArity(dom.length)
          .setCategories(Arrays.asList(domPreview));
    } else {
      return new FeatureInfo(name, FeatureType.NUMERIC);
    }
  }
}
