package com.inrotate.exast.domain.prediction

sealed interface PredictionFailure {
    data class Validation(val message: String) : PredictionFailure
    data class Backend(val statusCode: Int, val message: String) : PredictionFailure
    data object ModelNotFound : PredictionFailure
    data object Network : PredictionFailure
    data class Unknown(val message: String) : PredictionFailure
}

sealed interface PredictionOutcome {
    data class Success(val result: PredictionResult) : PredictionOutcome
    data class Error(val failure: PredictionFailure) : PredictionOutcome
}
