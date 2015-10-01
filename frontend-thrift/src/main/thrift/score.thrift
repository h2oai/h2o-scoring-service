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

enum ColumnType {
    NUMERIC = 1,
    CATEGORICAL = 2
}

/** Information about model pojo */
struct ModelInfo {
    1: string name
    2: string algorithm
    3: string modelKind
    4: string modelCategory
    5: list<string> columnNames
    6: list<ColumnType> columnTypes
}

struct ParserSetup {
    1: byte delimiter
    2: bool header
}

service ScoringService {

    list<ModelInfo> listModels()

    ModelInfo modelInfo(1: string modelId) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    Prediction predictMapRow(1: string modelId, 2: map<string, double> row) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    Prediction predictStringRow(1: string modelId, 2: string row, 3: ParserSetup parserSetup) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    Prediction predictDoubleRow(1: string modelId, 2: list<double> row) throws (1:ModelNotFoundException notFound, 2: PredictException predictException)

    ModelInfo deployPojoJar(1: binary modelJar)
}