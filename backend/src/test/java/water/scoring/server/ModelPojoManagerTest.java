package water.scoring.server;

import org.testng.annotations.Test;

import java.net.URI;
import java.util.Arrays;

import water.util.TestUtils;

/**
 * Test model pojo manager API.
 */
public class ModelPojoManagerTest {

  @Test
  public void testLoadPojo() {
    ModelPojoManager mpm = ModelPojoManager.INSTANCE;
    URI pojoJarUri = TestUtils.findFile("backend/build/resources/test/pojos/gbm_02c92461_25ea_45d2_9bf9_af2fa92758d2.jar").toURI();
    ModelPojo mp = mpm.loadModelPojo(pojoJarUri);
    System.err.println(Arrays.deepToString(mp.getModel().getDomainValues()));
  }

}