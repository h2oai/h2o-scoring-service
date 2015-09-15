package water.scoring.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import water.genmodel.IGeneratedModel;

public class ModelPojoManager {

  public static final ModelPojoManager INSTANCE = new ModelPojoManager();

  private static final Logger logger = LogManager.getLogger(ModelPojoManager.class);

  private Map<String, ModelPojo> registry = new HashMap<>();

  private ModelPojoManager() {
  }

  public ModelPojo loadModelPojo(URI pojoJarURI) {
    ModelPojo[] modelPojos = loadModelPojos(pojoJarURI);
    if (modelPojos.length == 0) {
      throw new IllegalArgumentException("Found no pojo in " + pojoJarURI);
    } else if (modelPojos.length > 1) {
      throw new IllegalArgumentException("Found multiple pojos (" + modelPojos.length +") in " + pojoJarURI + "!");
    }
    return modelPojos[0];
  }

  public ModelPojo[] loadModelPojos(URI pojoJarURI) {
    ModelPojo[] modelPojos = null;

    try {
      // Get URL of POJO jar
      URL pojoJarURL = pojoJarURI.toURL();
      // Create CL to load Pojo classes from given URL, delegates to parent CL
      URLClassLoader
          pojoCL =
          new URLClassLoader(new URL[]{pojoJarURL}, ModelPojoManager.class.getClassLoader());
      // Use reflections package to list POJOs in the jar
      Reflections g = new Reflections(pojoJarURL, pojoCL);
      Set<Class<?>>
          modelPojoKlazzes =
          g.getTypesAnnotatedWith(hex.genmodel.annotations.ModelPojo.class);
      // Load detected classes
      modelPojos = new ModelPojo[modelPojoKlazzes.size()];
      int cnt = 0;
      for (Class<?> modelPojoKlazz : modelPojoKlazzes) {
        hex.genmodel.annotations.ModelPojo anno = modelPojoKlazz.getAnnotation(hex.genmodel.annotations.ModelPojo.class);
        if (anno != null) {
          ModelPojo modelPojo = new ModelPojoAdapter(anno.name(),
                                                     anno.algorithm(),
                                                     "NA",
                                                     (IGeneratedModel) modelPojoKlazz.newInstance());
          modelPojos[cnt++] = modelPojo;
          // Put pojo into private registry of manager
          ModelPojo oldModelPojo = registry.put(anno.name(), modelPojo);
          if (oldModelPojo != null) {
            logger.warn(
                "Model POJO with name: " + anno.name() + "(" + oldModelPojo.getModel()
                    .getClass() + ") was replaced with new model POJO " + modelPojoKlazz);
            // Important: release holded reference to loaded POJO model class with dedicated CL
            oldModelPojo.release();
          }
        } else {
          logger.warn("Cannot register POJO " + modelPojoKlazz);
        }
      }
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Cannot instantiate model POJO!", e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Cannot instantiate model POJO!", e);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(
          "Cannot find location model model POJO classes! The location " + pojoJarURI
          + " seems to be wrong!", e);
    }

    return modelPojos;
  }

  public String[] listPojoNames() {
    return registry.keySet().toArray(new String[registry.size()]);
  }

  public ModelPojo getPojoByName(String name) {
    return registry.get(name);
  }

  public int getPojoCount() {
    return registry.size();
  }

  public ModelPojo[] getPojos() {
    return registry.values().toArray(new ModelPojo[registry.size()]);
  }

  public void clear() {
    for (ModelPojo mp : registry.values()) {
      mp.release();
    }
    registry.clear();
  }
}