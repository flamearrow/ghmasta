package band.mlgb.ghmasta.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repository_page_keys")
data class RepositoryPageKey(
    @PrimaryKey val repositoryId: Int,
    val currentPage: Int,
    val nextPage: Int,
    val previousPage: Int,
    val firstPage: Int,
    val lastPage: Int,
)
