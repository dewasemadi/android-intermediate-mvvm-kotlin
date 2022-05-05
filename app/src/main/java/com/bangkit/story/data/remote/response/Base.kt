package com.bangkit.story.data.remote.response

import com.google.gson.annotations.SerializedName

data class Base(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
