package water.scoring.server;

import hex.ModelCategory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import water.genmodel.IGeneratedModel;

/**
 * Interface to represent model POJO.
 */
public interface ModelPojo {
  String getName();
  String getAlgo();
  String getKind();
  ModelCategory getCategory();

  int getFeatureCount();
  String[] getColumNames();

  IGeneratedModel getModel();
  EasyPredictModelWrapper getPredictWrapper();
  void release();
}
