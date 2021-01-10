package band.mlgb.ghmasta.data.model

import com.google.gson.annotations.SerializedName

data class RepositorySearchResponse(
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<Repository> = emptyList(),
)