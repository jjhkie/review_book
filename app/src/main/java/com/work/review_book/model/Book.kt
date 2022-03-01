package com.work.review_book.model

import com.google.gson.annotations.SerializedName

data class Book(
    //서버에서 가져올 값의 이름이 선언한 이름과 다를 경우
    //SerializedName을 사용하여 표시한다.
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String
)