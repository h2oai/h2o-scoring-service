/* Exposed public scoring API */

namespace java water.scoring.api

struct Prediction {
    1: string label
    2: list<double> distribution
}

struct ModelPojoInfo {
    1: string name
    2: string algorithm
    3: string modelKind
}

struct ParserSetup {
    1: byte delimiter
}

service ScoringService {

    list<ModelPojoInfo> listModelPojos()

    Prediction predictMapRow(1: string pojoName, 2: map<string, double> row)

    Prediction predictStringRow(1: string pojoName, 2: string row, 3: ParserSetup parserSetup)

    Prediction predictDoubleRow(1: string pojoName, 2: list<double> row)
}