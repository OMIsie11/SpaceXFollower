package io.github.omisie11.spacexfollower.ui.about.used_libraries

import com.google.gson.annotations.SerializedName

data class UsedLibrary(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("license")
    val license: String,
    @SerializedName("repositoryUrl")
    val repositoryUrl: String
)