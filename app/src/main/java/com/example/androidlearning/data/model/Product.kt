package com.example.androidlearning.data.model

data class Product(
    val name: String,
    val price: Price,
    val brand: String,
    val review: Review,
    val tags: List<String>,
    val location: String,
    val badge: Badge,
    val soldCountTotal: Int,
    val uspLabelsTags: List<String>,
    val images: List<String>
) {
    val id: String
        get() = name.lowercase()
            .replace(Regex("[^a-z0-9]"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
}
