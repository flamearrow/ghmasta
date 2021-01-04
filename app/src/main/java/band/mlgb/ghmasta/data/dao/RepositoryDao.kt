package band.mlgb.ghmasta.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.User

@Dao
interface RepositoryDao {
    @Query("SELECT * FROM repository_table")
    fun allRepos(): LiveData<List<Repository>>

    @Query("SELECT * FROM repository_table WHERE id = :id")
    fun getRepository(id: String): LiveData<Repository>

    @Insert(onConflict = REPLACE)
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)
}