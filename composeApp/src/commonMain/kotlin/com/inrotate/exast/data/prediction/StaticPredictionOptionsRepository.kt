package com.inrotate.exast.data.prediction

import com.inrotate.exast.domain.prediction.OrganizationOption
import com.inrotate.exast.domain.prediction.PredictionOption
import com.inrotate.exast.domain.prediction.PredictionOptionsRepository

class StaticPredictionOptionsRepository : PredictionOptionsRepository {
    override suspend fun getEventTypes(): List<PredictionOption> =
        listOf(
            PredictionOption("cultural_creative", "Культурно-творческое"),
            PredictionOption("scientific", "Научное"),
            PredictionOption("sports", "Спортивное"),
            PredictionOption("educational", "Образовательное"),
            PredictionOption("patriotic", "Патриотическое"),
            PredictionOption("volunteer", "Волонтерское"),
            PredictionOption("career", "Карьерное"),
        )

    override suspend fun getOrganizations(): List<OrganizationOption> =
        listOf(
            OrganizationOption(67, "Организация 67"),
        )
}
