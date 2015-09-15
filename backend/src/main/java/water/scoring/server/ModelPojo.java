package water.scoring.server;

import water.genmodel.IGeneratedModel;

/**
 * Created by michal on 9/12/15.
 */
public interface ModelPojo {
  String getName();
  String getAlgo();
  String getKind();

  IGeneratedModel getModel();
  void release();
}
