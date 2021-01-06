package band.mlgb.ghmasta.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import band.mlgb.ghmasta.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun allUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE user_id = :id")
    fun getUserLiveData(id: String): LiveData<User>

    @Query("SELECT * FROM user_table WHERE user_id = :id")
    fun getUser(id: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Delete
    fun deleteUser(user: User)

    @Query("DELETE FROM user_table ")
    suspend fun clearUsers()
}