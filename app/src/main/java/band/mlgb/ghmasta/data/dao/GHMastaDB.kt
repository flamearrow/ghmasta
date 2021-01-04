package band.mlgb.ghmasta.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.User

@Database(entities = [User::class, Repository::class], version = 1)
@TypeConverters(GHMastaTypeConverter::class)
abstract class GHMastaDB : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getRepositoryDao(): RepositoryDao

    companion object {
        const val DB_NAME = "GHMastaDB"
    }
}