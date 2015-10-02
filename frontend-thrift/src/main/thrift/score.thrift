/* Exposed public scoring API */

namespace java water.scoring.api

/** Exception to signal that POJO with given name does not exist. */
exception ModelNotFoundException {
    1: string modelName;
}

exception UnsupportedModelCategoryException {
    1: string modelCategory;
}

exception PredictException {
    1: string message;
}

/** Representation of binomial prediction. */
struct BinomialPrediction {
    1: string label
    2: list<double> distribution
}

/** Representation of multinomial prediction. */
struct MultinomialPrediction {
    1: string label
    2: list<double> distribution
}

struct RegressiongPrediction {
    1: double label
}

struct ClusteringPrediction {
    1: i32 label
}

/** Generic model prediction - it shapes depends on model type */
union PredictionHolder {
    1: BinomialPrediction binomialPrediction
    2: MultinomialPrediction multinomialPrediction
    3: RegressiongPrediction regressionPrediction
    4: ClusteringPrediction clusteringPrediction
}


struct Prediction {
    1: string modelCategory
    2: PredictionHolder prediction
}

enum FeatureType {
    NUMERIC = 1,
    CATEGORICAL = 2
    // NOTE POJO/H2O does not support string types at modelling level - so we do
    // not need to consider it here
}

/** Information about a model pojo. */
struct ModelInfo {
    1: string id
    2: string algorithm
    3: string modelKind
    4: string modelCategory
    5: i32 featureCount // Number of input features expected by model
    6: list<string> featureNames // Input feature names
    7: list<FeatureType> featureTypes // Input feature types
}

struct ParserSetup {
    1: byte delimiter
    2: bool header
}

/**
 * Holder to carry information about a feature (aka column).
 **/
struct FeatureInfo {
    1: string name
    2: FeatureType type
    3: optional list<string> categories
    4: optional i32 arity
}

service ScoringService {

    /** List all models provided by the service */
    list<ModelInfo> listModels()

    /** Returns model information about specified model */
    ModelInfo modelInfo(1: string modelId) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    /** Predict row given in form of map - { (feature name, feature value), ... } */
    Prediction predictMapRow(1: string modelId, 2: map<string, double> row) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    /** Predict row given as a simple string. */
    Prediction predictStringRow(1: string modelId, 2: string row, 3: ParserSetup parserSetup) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    /** Predict row given as array of doubles. */
    Prediction predictDoubleRow(1: string modelId, 2: list<double> row) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    /** Deploy jar given as a binary stream. */
    ModelInfo deployPojoJar(1: binary modelJar)

    /** Get feature information by feature name. */
    FeatureInfo featureInfoByName(1: string modelId, 2: string name, 3: optional i32 categoricalOff = 0, 4: optional i32 categoricalLen = 25)

    /** Get feature information by feature index. */
    FeatureInfo featureInfoByIdx(1: string modelId, 2: i32 idx, 3: optional i32 categoricalOff = 0, 4: optional i32 categoricalLen = 25)
}