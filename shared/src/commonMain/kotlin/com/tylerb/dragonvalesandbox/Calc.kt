package com.tylerb.dragonvalesandbox

import com.tylerb.dragonvalesandbox.model.DragonData

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// configuration

val weight = mapOf(
    "hybrid" to 10,
    "primary" to 2,
    "default" to 1
)

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// is element

val elementList = listOf(
    "plant", "fire", "earth", "cold", "lightning",
    "water", "air", "metal", "light", "dark"
)

val epicList = listOf(
    "apocalypse", "aura", "chrysalis", "dream", "galaxy", "hidden",
    "melody", "monolith", "moon", "olympus", "ornamental", "rainbow",
    "seasonal", "snowflake", "sun", "surface", "treasure", "zodiac"
)

val riftList = listOf("rift")
val gemList = listOf("gemstone", "crystalline")

val breedList = listOf(elementList, epicList, riftList).flatten()
val concatList = listOf(breedList, gemList).flatten()

fun isBaseElement(tag: String): Boolean {
    return elementList.indexOf(tag) > -1
}

fun isBreedElement(tag: String): Boolean {
    return breedList.indexOf(tag) > -1
}

fun isElement(tag: String): Boolean {
    return concatList.indexOf(tag) > -1
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// is opposite

val opposite = mutableMapOf(
    "plant" to "metal",
    "fire" to "cold",
    "earth" to "air",
    "lightning" to "water",
    "light" to "dark"
).apply {
    // add reverse to map
    val keys = keys.toList()
    keys.forEach { key ->
        this[this[key]!!] = key
    }
}

val Any?.notNull: Boolean
    get() = this != null

fun isOpposite(e1: String?, e2: String?): Boolean {
    return defAndEq(opposite[e1], e2)
}

fun defAndEq(a: String?, b: String?): Boolean {
    return (a.notNull && b.notNull) && (a == b)
}



// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// breed two dragons

fun List<DragonData>.breedCalc(d1: DragonData, d2: DragonData, beb: Boolean): List<DragonData>? {
    val allDragons = this
    val query = breedQuery(d1, d2, beb)
    var list: MutableList<DragonData> = mutableListOf()

    when {
        oppositePrimary(query) -> {
            // opposite primaries cannot be bred directly
        }
        samePrimary(query) -> {
            list = primaryDragons(query.elements).map { dragonName ->
                allDragons.find { it.name == dragonName }!!
            }.toMutableList()
        }
        else -> {
            if (oppositeElements(query)) {
                list = primaryDragons(query.elements).map { dragonName ->
                    allDragons.find { it.name == dragonName }!!
                }.toMutableList()
            }
            allDragons.forEach { dragon ->
                if (breedable(dragon, query)) {
                    list.add(dragon)
                }
            }
        }
    }

    return if (list.size > 0) list else null


}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// breed query

data class Query(
    val d1: DragonData,
    val d2: DragonData,
    val beb: Boolean,
    val tags: Map<String, Int>,
    val elements: List<String> = emptyList(),
) {
    val nElements
        get() = elements.size
}

private const val ANY_KEY = "any"
private const val DREAM_KEY = "dream"
fun breedQuery(d1: DragonData, d2: DragonData, beb: Boolean): Query {

    var query = Query(
        d1,
        d2,
        beb,
        mapOf("any dragons" to 1)
    )

    val tags = query.tags.toMutableMap()
    listOf(d1, d2).forEachIndexed { i, dragon ->
        dragon.tags.forEach {
            tags[it.key] = 1
            tags["d${i+1}.${it.key}"] = 1
        }
    }

    val list = breedElements(query)
    val elements = list[ANY_KEY]!!.keys
    val n = elements.size
    val d = list[DREAM_KEY]!!.keys.size

    query = query.copy(elements = elements.toList())

    if (n >= 4) { tags["four elements"] = 1 }
    if (d >= 2) { tags["dream elements"] = 1 }

    query = query.copy(tags = tags)


    return query
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// dragon tags
/** @see DragonData.tags  **/

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// compile breed elements

fun breedElements(query: Query): Map<String, Map<String, String>> {


    val list = mapOf<String, MutableMap<String, String>>(
        ANY_KEY to mutableMapOf(),
        DREAM_KEY to mutableMapOf()
    )

    listOf(query.d1, query.d2).forEach { dragon ->
        dragon.tags.forEach { tag ->
            if (isBreedElement(tag.key)) {
                list[ANY_KEY]!![tag.key] = tag.key

                if (tag.key != "light" && tag.key != "dark") {
                    list[DREAM_KEY]!![tag.key] = tag.key
                }
            }
        }
    }

    return list
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// opposite primary rule

fun oppositePrimary(query: Query): Boolean {
    return defAndEq(query.d1.primary, query.d2.opposite)
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// same primary rule

fun samePrimary(query: Query): Boolean {
    return defAndEq(query.d1.primary, query.d2.primary)
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// opposite elements rule

fun oppositeElements(query: Query): Boolean {
    val list = query.elements.filter { elem -> isBaseElement(elem) }
    return list.size == 2 && isOpposite(list[0], list[1])
}


// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// list primary dragons by element

fun primaryDragons(elements: List<String>): List<String> {
    val list = ArrayList<String>()

    elements.forEach { element ->
        val elementLower = element.lowercase()
        if (elementList.contains(elementLower)) {
            val elementName = elementLower.replaceFirstChar { it.uppercase() }
            list.add(elementName)
        }
    }

    return list
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// check breedable

fun breedable(dragonData: DragonData, query: Query): Boolean {
    if (checkAvailable(dragonData, query)) {
        val reqs = dragonData.reqsCompiled
        var yn = false

        reqs.forEach { req ->
            if(!yn) {
                val need = req.keys
                val have = query.tags
                var miss = false

                need.forEach { tag ->
                    if (have[tag] == null) {
                        miss = true
                    }
                }

                if (!miss) { yn = true }

            }
        }

        return yn
    }
    return false
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// check available

fun checkAvailable(dragonData: DragonData, query: Query): Boolean {
    return when {
        dragonData.available == "never" -> false
        query.beb -> true
        dragonData.available == "permanent" -> true
        dragonData.available.startsWith("yes") -> true
        dragonData.name == query.d1.name -> true
        dragonData.name == query.d2.name -> true
        else -> false
    }
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// compile requirements
/** @see DragonData.reqsCompiled  **/





