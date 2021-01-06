package band.mlgb.ghmasta.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//{
//    "login": "flamearrow",
//    "id": 4720570,
//    "node_id": "MDQ6VXNlcjQ3MjA1NzA=",
//    "avatar_url": "https://avatars1.githubusercontent.com/u/4720570?v=4",
//    "gravatar_id": "",
//    "url": "https://api.github.com/users/flamearrow",
//    "html_url": "https://github.com/flamearrow",
//    "followers_url": "https://api.github.com/users/flamearrow/followers",
//    "following_url": "https://api.github.com/users/flamearrow/following{/other_user}",
//    "gists_url": "https://api.github.com/users/flamearrow/gists{/gist_id}",
//    "starred_url": "https://api.github.com/users/flamearrow/starred{/owner}{/repo}",
//    "subscriptions_url": "https://api.github.com/users/flamearrow/subscriptions",
//    "organizations_url": "https://api.github.com/users/flamearrow/orgs",
//    "repos_url": "https://api.github.com/users/flamearrow/repos",
//    "events_url": "https://api.github.com/users/flamearrow/events{/privacy}",
//    "received_events_url": "https://api.github.com/users/flamearrow/received_events",
//    "type": "User",
//    "site_admin": false,
//    "name": "Chen Cen",
//    "company": "@google",
//    "blog": "",
//    "location": "Santa Clara",
//    "email": null,
//    "hireable": null,
//    "bio": "Mobile/ML engineer",
//    "twitter_username": "arrowcen",
//    "public_repos": 44,
//    "public_gists": 1,
//    "followers": 15,
//    "following": 14,
//    "created_at": "2013-06-17T17:30:27Z",
//    "updated_at": "2021-01-03T01:59:14Z"
//}

@Entity(tableName = "user_table")
data class User(
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("blog")
    val blog: String?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("events_url")
    val eventsUrl: String?,
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("followers_url")
    val followersUrl: String?,
    @SerializedName("following")
    val following: Int,
    @SerializedName("following_url")
    val followingUrl: String?,
    @SerializedName("gists_url")
    val gistsUrl: String?,
    @SerializedName("gravatar_id")
    val gravatarId: String?,
    @SerializedName("hireable")
    val hireable: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("id")
    @ColumnInfo(name = "user_id")
    @PrimaryKey
    val id: Int,
    @SerializedName("location")
    val location: String?,
    @SerializedName("login")
    val login: String?,
    @SerializedName("name")
    @ColumnInfo(name = "user_name")
    val name: String?,
    @SerializedName("node_id")
    val nodeId: String?,
    @SerializedName("organizations_url")
    val organizationsUrl: String?,
    @SerializedName("public_gists")
    val publicGists: Int,
    @SerializedName("public_repos")
    val publicRepos: Int,
    @SerializedName("received_events_url")
    val receivedEventsUrl: String?,
    @SerializedName("repos_url")
    val reposUrl: String?,
    @SerializedName("site_admin")
    val siteAdmin: Boolean,
    @SerializedName("starred_url")
    val starredUrl: String?,
    @SerializedName("subscriptions_url")
    val subscriptionsUrl: String?,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("url")
    val url: String?
)
