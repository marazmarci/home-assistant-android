package io.homeassistant.companion.android.data

data class SimplifiedEntity(
    val entityId: String,
    val friendlyName: String = entityId,
    val icon: String = ""
) {
    constructor(entityString: String) : this(
        entityString.split(",").first(),
        entityString.split(",").run {
            subList(1, lastIndex - 1)
        }.joinToString(","),
        entityString.split(",").last()
    )

    val domain: String
        get() = entityId.split(".")[0]

    val entityString: String
        get() = "$entityId,$friendlyName,$icon"
}
