package band.mlgb.ghmasta.inject

import android.content.Context
import androidx.room.Room
import band.mlgb.ghmasta.data.dao.GHMastaDB
import band.mlgb.ghmasta.data.dao.RepositoryDao
import band.mlgb.ghmasta.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DBModule {
    // add shit

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): GHMastaDB {
        return Room.databaseBuilder(context, GHMastaDB::class.java, GHMastaDB.DB_NAME).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(ghMastaDB: GHMastaDB): UserDao {
        return ghMastaDB.getUserDao()
    }

    @Provides
    @Singleton
    fun provideRepositoryDao(ghMastaDB: GHMastaDB): RepositoryDao {
        return ghMastaDB.getRepositoryDao()
    }
}