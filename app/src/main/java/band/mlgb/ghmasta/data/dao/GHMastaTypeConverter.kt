package band.mlgb.ghmasta.data.dao

import androidx.room.TypeConverter
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GHMastaTypeConverter {
//    @TypeConverter
//    fun stringToUsers(data: String): MutableList<User> {
//        return Gson().fromJson(data, object : TypeToken<List<User>>() {}.type)
//    }
//
//    @TypeConverter
//    fun usersToString(users: List<User>): String {
//        return Gson().toJson(users)
//    }
//
//    @TypeConverter
//    fun stringToRepositories(data: String): MutableList<Repository> {
//        return Gson().fromJson(data, object : TypeToken<List<Repository>>() {}.type)
//    }
//
//    @TypeConverter
//    fun repositoriesToString(repos: List<Repository>): String {
//        return Gson().toJson(repos)
//    }

    @TypeConverter
    fun stringToUser(data: String): User {
        return Gson().fromJson(data, object : TypeToken<User>() {}.type)
    }

    @TypeConverter
    fun userToString(user: User): String {
        return Gson().toJson(user)
    }

}