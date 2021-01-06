package band.mlgb.ghmasta.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import band.mlgb.ghmasta.data.model.Repository

@Dao
interface RepositoryDao {
    @Query("SELECT * FROM repository_table")
    fun allRepos(): LiveData<List<Repository>>

    @Query("SELECT * FROM repository_table WHERE id = :id")
    fun getRepository(id: String): LiveData<Repository>

    @Query("SELECT * FROM repository_table WHERE user_id = :ownerId")
    fun getRepositoriesOfUser(ownerId: Int): PagingSource<Int, Repository>

    @Query("SELECT * FROM repository_table WHERE login = :ownerLoginName ORDER BY repository_name ASC")
    fun getRepositoriesOfUser(ownerLoginName: String): PagingSource<Int, Repository>

    @Query("SELECT * FROM repository_table")
    fun getRepositories(): PagingSource<Int, Repository>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repo: Repository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repos: List<Repository>)

    @Delete
    fun deleteRepository(repo: Repository)

    @Query("DELETE FROM repository_table ")
    suspend fun clearRepositories()
}