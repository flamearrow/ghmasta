package band.mlgb.ghmasta.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import band.mlgb.ghmasta.data.model.RepositoryPageKey

@Dao
interface RepositoryPageKeyDao {
    @Query("SELECT * FROM repository_page_keys WHERE repositoryId = :repositoryId")
    suspend fun getRepositoryPageKey(repositoryId: Int): RepositoryPageKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositoryPageKey(pageKey: RepositoryPageKey)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositoryPageKeys(pageKeys: List<RepositoryPageKey>)

    @Query("DELETE FROM repository_page_keys ")
    suspend fun clearRepositoryPageKeys()
}