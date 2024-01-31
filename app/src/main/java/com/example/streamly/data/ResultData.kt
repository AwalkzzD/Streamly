package com.example.streamly.data

data class ResultData(
    val `data`: List<Data>,
    val next: String,
    val total: Int
)