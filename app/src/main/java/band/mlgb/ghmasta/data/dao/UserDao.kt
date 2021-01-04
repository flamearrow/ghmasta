package band.mlgb.ghmasta.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun allUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getUser(id: String): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepository(repo: Repository)

    @Delete
    fun deleteRepository(repo: Repository)
}