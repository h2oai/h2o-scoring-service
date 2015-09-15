package water.scoring.server;

import water.genmodel.IGeneratedModel;

public class ModelPojoAdapter implements ModelPojo {
  private String name;
  private String algorithm;
  private String modelKind;

  private IGeneratedModel modelPojo;

  public ModelPojoAdapter(String name,
                          String algorithm,
                          String modelKind,
                          IGeneratedModel modelPojo) {
    this.name = name;
    this.algorithm = algorithm;
    this.modelKind = modelKind;
    this.modelPojo = modelPojo;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getAlgo() {
    return null;
  }

  @Override
  public String getKind() {
    return null;
  }

  @Override
  public IGeneratedModel getModel() {
    return modelPojo;
  }

  @Override
  public void release() {
    this.modelPojo = null;
  }
}