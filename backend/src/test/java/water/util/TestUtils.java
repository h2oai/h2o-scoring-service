package water.util;

import java.io.File;

/**
 * Various test utilities.
 */
public class TestUtils {

  public static File findFile(String path) {
    File cd = new File(".").getAbsoluteFile();
    File f = null;
    while (cd != null && !(f = new File(cd, path)).exists()) {
      cd = cd.getParentFile();
    }
    return cd != null ? f : null;
  }
}
