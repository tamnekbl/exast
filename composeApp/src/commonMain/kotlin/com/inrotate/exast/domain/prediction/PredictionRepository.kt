package com.inrotate.exast.domain.prediction

interface PredictionRepository {
    suspend fun predictEventScale(request: CreatePredictionRequest): PredictionOutcome
}
