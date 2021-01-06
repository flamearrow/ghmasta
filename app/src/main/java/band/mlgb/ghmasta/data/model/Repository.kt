package band.mlgb.ghmasta.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//{
//    "id": 71736912,
//    "node_id": "MDEwOlJlcG9zaXRvcnk3MTczNjkxMg==",
//    "name": "-CalculatorUI",
//    "full_name": "flamearrow/-CalculatorUI",
//    "private": false,
//    "owner": {
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
//    "site_admin": false
//    },
//    "html_url": "https://github.com/flamearrow/-CalculatorUI",
//    "description": "play with IOS UI constraints in different screen configurations",
//    "fork": false,
//    "url": "https://api.github.com/repos/flamearrow/-CalculatorUI",
//    "forks_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/forks",
//    "keys_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/keys{/key_id}",
//    "collaborators_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/collaborators{/collaborator}",
//    "teams_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/teams",
//    "hooks_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/hooks",
//    "issue_events_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/issues/events{/number}",
//    "events_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/events",
//    "assignees_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/assignees{/user}",
//    "branches_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/branches{/branch}",
//    "tags_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/tags",
//    "blobs_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/git/blobs{/sha}",
//    "git_tags_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/git/tags{/sha}",
//    "git_refs_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/git/refs{/sha}",
//    "trees_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/git/trees{/sha}",
//    "statuses_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/statuses/{sha}",
//    "languages_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/languages",
//    "stargazers_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/stargazers",
//    "contributors_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/contributors",
//    "subscribers_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/subscribers",
//    "subscription_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/subscription",
//    "commits_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/commits{/sha}",
//    "git_commits_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/git/commits{/sha}",
//    "comments_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/comments{/number}",
//    "issue_comment_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/issues/comments{/number}",
//    "contents_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/contents/{+path}",
//    "compare_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/compare/{base}...{head}",
//    "merges_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/merges",
//    "archive_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/{archive_format}{/ref}",
//    "downloads_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/downloads",
//    "issues_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/issues{/number}",
//    "pulls_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/pulls{/number}",
//    "milestones_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/milestones{/number}",
//    "notifications_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/notifications{?since,all,participating}",
//    "labels_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/labels{/name}",
//    "releases_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/releases{/id}",
//    "deployments_url": "https://api.github.com/repos/flamearrow/-CalculatorUI/deployments",
//    "created_at": "2016-10-23T23:55:39Z",
//    "updated_at": "2016-10-23T23:55:52Z",
//    "pushed_at": "2016-10-23T23:56:21Z",
//    "git_url": "git://github.com/flamearrow/-CalculatorUI.git",
//    "ssh_url": "git@github.com:flamearrow/-CalculatorUI.git",
//    "clone_url": "https://github.com/flamearrow/-CalculatorUI.git",
//    "svn_url": "https://github.com/flamearrow/-CalculatorUI",
//    "homepage": null,
//    "size": 10,
//    "stargazers_count": 0,
//    "watchers_count": 0,
//    "language": "Swift",
//    "has_issues": true,
//    "has_projects": true,
//    "has_downloads": true,
//    "has_wiki": true,
//    "has_pages": false,
//    "forks_count": 0,
//    "mirror_url": null,
//    "archived": false,
//    "disabled": false,
//    "open_issues_count": 0,
//    "license": null,
//    "forks": 0,
//    "open_issues": 0,
//    "watchers": 0,
//    "default_branch": "master"
//},

@Entity(tableName = "repository_table")
data class Repository(
//    @SerializedName("archive_url")
//    val archiveUrl: String,
//    @SerializedName("archived")
//    val archived: Boolean,
//    @SerializedName("assignees_url")
//    val assigneesUrl: String,
//    @SerializedName("blobs_url")
//    val blobsUrl: String,
//    @SerializedName("branches_url")
//    val branchesUrl: String,
//    @SerializedName("clone_url")
//    val cloneUrl: String,
//    @SerializedName("collaborators_url")
//    val collaboratorsUrl: String,
//    @SerializedName("comments_url")
//    val commentsUrl: String,
//    @SerializedName("commits_url")
//    val commitsUrl: String,
//    @SerializedName("compare_url")
//    val compareUrl: String,
//    @SerializedName("contents_url")
//    val contentsUrl: String,
//    @SerializedName("contributors_url")
//    val contributorsUrl: String,
//    @SerializedName("created_at")
//    val createdAt: String,
//    @SerializedName("default_branch")
//    val defaultBranch: String,
//    @SerializedName("deployments_url")
//    val deploymentsUrl: String,
    @SerializedName("description")
    val description: String?,
//    @SerializedName("disabled")
//    val disabled: Boolean,
//    @SerializedName("downloads_url")
//    val downloadsUrl: String,
//    @SerializedName("events_url")
//    val eventsUrl: String,
//    @SerializedName("fork")
//    val fork: Boolean,
//    @SerializedName("forks")
//    val forks: Int,
//    @SerializedName("forks_count")
//    val forksCount: Int,
//    @SerializedName("forks_url")
//    val forksUrl: String,
//    @SerializedName("full_name")
//    val fullName: String,
//    @SerializedName("git_commits_url")
//    val gitCommitsUrl: String,
//    @SerializedName("git_refs_url")
//    val gitRefsUrl: String,
//    @SerializedName("git_tags_url")
//    val gitTagsUrl: String,
//    @SerializedName("git_url")
//    val gitUrl: String,
//    @SerializedName("has_downloads")
//    val hasDownloads: Boolean,
//    @SerializedName("has_issues")
//    val hasIssues: Boolean,
//    @SerializedName("has_pages")
//    val hasPages: Boolean,
//    @SerializedName("has_projects")
//    val hasProjects: Boolean,
//    @SerializedName("has_wiki")
//    val hasWiki: Boolean,
//    @SerializedName("homepage")
//    val homepage: String,
//    @SerializedName("hooks_url")
//    val hooksUrl: String,
//    @SerializedName("html_url")
//    val htmlUrl: String,
    @SerializedName("id")
    @PrimaryKey
    val id: Int,
//    @SerializedName("issue_comment_url")
//    val issueCommentUrl: String,
//    @SerializedName("issue_events_url")
//    val issueEventsUrl: String,
//    @SerializedName("issues_url")
//    val issuesUrl: String,
//    @SerializedName("keys_url")
//    val keysUrl: String,
//    @SerializedName("labels_url")
//    val labelsUrl: String,
    @SerializedName("language")
    val language: String?,
//    @SerializedName("languages_url")
//    val languagesUrl: String,
//    @SerializedName("license")
//    val license: String,
//    @SerializedName("merges_url")
//    val mergesUrl: String,
//    @SerializedName("milestones_url")
//    val milestonesUrl: String,
//    @SerializedName("mirror_url")
//    val mirrorUrl: String?,
    @SerializedName("name")
    @ColumnInfo(name = "repository_name")
    val name: String?,
//    @SerializedName("node_id")
//    val nodeId: String,
//    @SerializedName("notifications_url")
//    val notificationsUrl: String,
//    @SerializedName("open_issues")
//    val openIssues: Int,
//    @SerializedName("open_issues_count")
//    val openIssuesCount: Int,
    @SerializedName("owner")
    @Embedded
    val owner: User,
//    @SerializedName("private")
//    val `private`: Boolean,
//    @SerializedName("pulls_url")
//    val pullsUrl: String,
//    @SerializedName("pushed_at")
//    val pushedAt: String,
//    @SerializedName("releases_url")
//    val releasesUrl: String,
//    @SerializedName("size")
//    val size: Int,
//    @SerializedName("ssh_url")
//    val sshUrl: String,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
//    @SerializedName("stargazers_url")
//    val stargazersUrl: String,
//    @SerializedName("statuses_url")
//    val statusesUrl: String,
//    @SerializedName("subscribers_url")
//    val subscribersUrl: String,
//    @SerializedName("subscription_url")
//    val subscriptionUrl: String,
//    @SerializedName("svn_url")
//    val svnUrl: String,
//    @SerializedName("tags_url")
//    val tagsUrl: String,
//    @SerializedName("teams_url")
//    val teamsUrl: String,
//    @SerializedName("trees_url")
//    val treesUrl: String,
//    @SerializedName("updated_at")
//    val updatedAt: String,
//    @SerializedName("url")
//    val url: String,
//    @SerializedName("watchers")
//    val watchers: Int,
//    @SerializedName("watchers_count")
//    val watchersCount: Int
)


//@Entity(tableName = "repository_table")
//data class Repository(
//    @SerializedName("archive_url")
//    val archiveUrl: String,
//    @SerializedName("archived")
//    val archived: Boolean,
//    @SerializedName("assignees_url")
//    val assigneesUrl: String,
//    @SerializedName("blobs_url")
//    val blobsUrl: String,
//    @SerializedName("branches_url")
//    val branchesUrl: String,
//    @SerializedName("clone_url")
//    val cloneUrl: String,
//    @SerializedName("collaborators_url")
//    val collaboratorsUrl: String,
//    @SerializedName("comments_url")
//    val commentsUrl: String,
//    @SerializedName("commits_url")
//    val commitsUrl: String,
//    @SerializedName("compare_url")
//    val compareUrl: String,
//    @SerializedName("contents_url")
//    val contentsUrl: String,
//    @SerializedName("contributors_url")
//    val contributorsUrl: String,
//    @SerializedName("created_at")
//    val createdAt: String,
//    @SerializedName("default_branch")
//    val defaultBranch: String,
//    @SerializedName("deployments_url")
//    val deploymentsUrl: String,
//    @SerializedName("description")
//    val description: String,
//    @SerializedName("disabled")
//    val disabled: Boolean,
//    @SerializedName("downloads_url")
//    val downloadsUrl: String,
//    @SerializedName("events_url")
//    val eventsUrl: String,
//    @SerializedName("fork")
//    val fork: Boolean,
//    @SerializedName("forks")
//    val forks: Int,
//    @SerializedName("forks_count")
//    val forksCount: Int,
//    @SerializedName("forks_url")
//    val forksUrl: String,
//    @SerializedName("full_name")
//    val fullName: String,
//    @SerializedName("git_commits_url")
//    val gitCommitsUrl: String,
//    @SerializedName("git_refs_url")
//    val gitRefsUrl: String,
//    @SerializedName("git_tags_url")
//    val gitTagsUrl: String,
//    @SerializedName("git_url")
//    val gitUrl: String,
//    @SerializedName("has_downloads")
//    val hasDownloads: Boolean,
//    @SerializedName("has_issues")
//    val hasIssues: Boolean,
//    @SerializedName("has_pages")
//    val hasPages: Boolean,
//    @SerializedName("has_projects")
//    val hasProjects: Boolean,
//    @SerializedName("has_wiki")
//    val hasWiki: Boolean,
//    @SerializedName("homepage")
//    val homepage: String,
//    @SerializedName("hooks_url")
//    val hooksUrl: String,
//    @SerializedName("html_url")
//    val htmlUrl: String,
//    @SerializedName("id")
//    @PrimaryKey
//    val id: Int,
//    @SerializedName("issue_comment_url")
//    val issueCommentUrl: String,
//    @SerializedName("issue_events_url")
//    val issueEventsUrl: String,
//    @SerializedName("issues_url")
//    val issuesUrl: String,
//    @SerializedName("keys_url")
//    val keysUrl: String,
//    @SerializedName("labels_url")
//    val labelsUrl: String,
//    @SerializedName("language")
//    val language: String,
//    @SerializedName("languages_url")
//    val languagesUrl: String,
//    @SerializedName("license")
//    val license: String,
//    @SerializedName("merges_url")
//    val mergesUrl: String,
//    @SerializedName("milestones_url")
//    val milestonesUrl: String,
//    @SerializedName("mirror_url")
//    val mirrorUrl: String?,
//    @SerializedName("name")
//    val name: String,
//    @SerializedName("node_id")
//    val nodeId: String,
//    @SerializedName("notifications_url")
//    val notificationsUrl: String,
//    @SerializedName("open_issues")
//    val openIssues: Int,
//    @SerializedName("open_issues_count")
//    val openIssuesCount: Int,
//    @SerializedName("owner")
//    val owner: User,
//    @SerializedName("private")
//    val `private`: Boolean,
//    @SerializedName("pulls_url")
//    val pullsUrl: String,
//    @SerializedName("pushed_at")
//    val pushedAt: String,
//    @SerializedName("releases_url")
//    val releasesUrl: String,
//    @SerializedName("size")
//    val size: Int,
//    @SerializedName("ssh_url")
//    val sshUrl: String,
//    @SerializedName("stargazers_count")
//    val stargazersCount: Int,
//    @SerializedName("stargazers_url")
//    val stargazersUrl: String,
//    @SerializedName("statuses_url")
//    val statusesUrl: String,
//    @SerializedName("subscribers_url")
//    val subscribersUrl: String,
//    @SerializedName("subscription_url")
//    val subscriptionUrl: String,
//    @SerializedName("svn_url")
//    val svnUrl: String,
//    @SerializedName("tags_url")
//    val tagsUrl: String,
//    @SerializedName("teams_url")
//    val teamsUrl: String,
//    @SerializedName("trees_url")
//    val treesUrl: String,
//    @SerializedName("updated_at")
//    val updatedAt: String,
//    @SerializedName("url")
//    val url: String,
//    @SerializedName("watchers")
//    val watchers: Int,
//    @SerializedName("watchers_count")
//    val watchersCount: Int
//)
