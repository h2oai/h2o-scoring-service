package water.scoring.server;

import hex.ModelCategory;
import hex.genmodel.GenModel;
import hex.genmodel.IGenModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import water.genmodel.IGeneratedModel;

public class ModelPojoAdapter implements ModelPojo {

  private final String name;
  private final String algorithm;
  private final String modelKind;
  private final ModelCategory modelCategory;

  private IGeneratedModel modelPojo;
  private EasyPredictModelWrapper modelWrapper;

  public ModelPojoAdapter(String name,
                          String algorithm,
                          String modelKind,
                          IGeneratedModel modelPojo) {
    this.name = name;
    this.algorithm = algorithm;
    this.modelKind = modelKind;
    this.modelCategory = ((IGenModel) modelPojo).getModelCategory();
    this.modelPojo = modelPojo;
    this.modelWrapper = new EasyPredictModelWrapper(asGenModel(modelPojo));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAlgo() {
    return algorithm;
  }

  @Override
  public String getKind() {
    return modelKind;
  }

  @Override
  public ModelCategory getCategory() {
    return modelCategory;
  }

  @Override
  public int getFeatureCount() {
    return getModel().getNumCols();
  }

  @Override
  public String[] getColumNames() {
    return modelPojo.getNames();
  }

  @Override
  public IGeneratedModel getModel() {
    return modelPojo;
  }

  @Override
  public EasyPredictModelWrapper getPredictWrapper() {
    return modelWrapper;
  }

  @Override
  public void release() {
    this.modelWrapper = null;
    this.modelPojo = null;
  }

  private static GenModel asGenModel(IGeneratedModel modelPojo) {
    return (GenModel) modelPojo;
  }

  @Override
  public String toString() {
    return "ModelPojo{" +
           "name='" + name + '\'' +
           ", algorithm='" + algorithm + '\'' +
           ", modelKind='" + modelKind + '\'' +
           ", modelCategory=" + modelCategory +
           '}';
  }
}