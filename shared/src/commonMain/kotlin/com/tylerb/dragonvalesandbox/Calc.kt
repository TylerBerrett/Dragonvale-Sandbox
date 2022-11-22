package com.tylerb.dragonvalesandbox

import com.tylerb.dragonvalesandbox.model.DragonData
import kotlin.math.roundToInt

// TODO: this would probably be easier to maintain if I host it instead


internal object Calc {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // configuration

    private enum class WeightByClone(val weight: Double) {
        Both(27.81),
        Single(2.45);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // is element

    private val elementList = listOf(
        "plant", "fire", "earth", "cold", "lightning",
        "water", "air", "metal", "light", "dark"
    )

    private val epicList = listOf(
        "apocalypse", "aura", "chrysalis", "dream", "galaxy", "hidden",
        "melody", "monolith", "moon", "olympus", "ornamental", "rainbow",
        "seasonal", "snowflake", "sun", "surface", "treasure", "zodiac"
    )

    private val riftList = listOf("rift")
    private val gemList = listOf("gemstone", "crystalline")

    private val breedList = listOf(elementList, epicList, riftList).flatten()
    private val concatList = listOf(breedList, gemList).flatten()

    private fun isBaseElement(tag: String): Boolean {
        return elementList.indexOf(tag) > -1
    }

    private fun isBreedElement(tag: String): Boolean {
        return breedList.indexOf(tag) > -1
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // is opposite

    private val opposite = mutableMapOf(
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

    private val Any?.notNull: Boolean
        get() = this != null

    private fun isOpposite(e1: String?, e2: String?): Boolean {
        return defAndEq(opposite[e1], e2)
    }

    private fun defAndEq(a: String?, b: String?): Boolean {
        return (a.notNull && b.notNull) && (a == b)
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // breed two dragons

    fun breedCalc(
        allDragons: List<DragonData>,
        d1: DragonData,
        d2: DragonData,
        beb: Boolean,
        fast: Boolean
    ): List<DragonData>? {
        val query = breedQuery(d1, d2, beb)
        var spawn: MutableList<DragonData> = mutableListOf()

        when {
            oppositePrimary(query) -> {
                // opposite primaries cannot be bred directly
            }
            samePrimary(query) -> {
                spawn = primaryDragons(query.elements).map { dragonName ->
                    allDragons.find { it.name == dragonName }!!
                }.toMutableList()
            }
            else -> {
                if (oppositeElements(query)) {
                    spawn = primaryDragons(query.elements).map { dragonName ->
                        allDragons.find { it.name == dragonName }!!
                    }.toMutableList()
                }
                allDragons.forEach { dragon ->
                    if (breedable(dragon, query)) {
                        spawn.add(dragon)
                    }
                }
            }
        }

        spawn = weightCalc(d1, d2, spawn).toMutableList()
        spawn.forEach { it.dhms = fmtBreedTime(it, fast) }

        return if (spawn.size > 0) spawn.sortedBy { it.time } else null


    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // breed query

    private data class Query(
        val d1: DragonData,
        val d2: DragonData,
        val beb: Boolean,
        val tags: Map<String, Int>,
        val elements: List<String> = emptyList(),
    )

    private const val ANY_KEY = "any"
    private const val DREAM_KEY = "dream"
    private fun breedQuery(d1: DragonData, d2: DragonData, beb: Boolean): Query {

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
                tags["d${i + 1}.${it.key}"] = 1
            }
        }

        val list = breedElements(query)
        val elements = list[ANY_KEY]!!.keys
        val n = elements.size
        val d = list[DREAM_KEY]!!.keys.size

        query = query.copy(elements = elements.toList())

        if (n >= 4) {
            tags["four elements"] = 1
        }
        if (d >= 2) {
            tags["dream elements"] = 1
        }

        query = query.copy(tags = tags)


        return query
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // dragon tags
    /** @see DragonData.tags  **/

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // compile breed elements

    private fun breedElements(query: Query): Map<String, Map<String, String>> {


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

    private fun oppositePrimary(query: Query): Boolean {
        return defAndEq(query.d1.primary, query.d2.opposite)
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // same primary rule

    private fun samePrimary(query: Query): Boolean {
        return defAndEq(query.d1.primary, query.d2.primary)
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // opposite elements rule

    private fun oppositeElements(query: Query): Boolean {
        val list = query.elements.filter { elem -> isBaseElement(elem) }
        return list.size == 2 && isOpposite(list[0], list[1])
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // list primary dragons by element

    private fun primaryDragons(elements: List<String>): List<String> {
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

    private fun breedable(dragonData: DragonData, query: Query): Boolean {
        if (checkAvailable(dragonData, query)) {
            val reqs = dragonData.reqsCompiled
            var yn = false

            reqs.forEach { req ->
                if (!yn) {
                    val need = req.keys
                    val have = query.tags
                    var miss = false

                    need.forEach { tag ->
                        if (have[tag] == null) {
                            miss = true
                        }
                    }

                    if (!miss) {
                        yn = true
                    }

                }
            }

            return yn
        }
        return false
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // check available

    private fun checkAvailable(dragonData: DragonData, query: Query): Boolean {
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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // weighted breed list

    private fun weightCalc(d1: DragonData, d2: DragonData, spawn: List<DragonData>): List<DragonData> {
        var total = 0.0
        val weighted = mutableMapOf<DragonData, Double>()

        // TODO: handle rift breeding
        spawn.forEach { dragon ->
            val weight = if (dragon == d1 && dragon == d2) {
                dragon.weight?.cloneBoth ?: WeightByClone.Both.weight
            } else if (dragon == d1 || dragon == d2) {
                dragon.weight?.cloneSingle ?: WeightByClone.Single.weight
            } else {
                dragon.weight?.breed ?: dragon.weightByType
            }

            weighted[dragon] = weight

            total += weight


        }

        spawn.forEach {
            val percent = (((weighted[it]!! / total) * 100) * 10)
            if (!percent.isNaN()) {
                it.percent = percent.roundToInt() / 10.0
            }
        }

        return spawn.filter { it.percent > 0.0 }

    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // format breed time

    private fun fmtBreedTime(dragon: DragonData, fast: Boolean): String {
        return fmtDhms(dragon.time * if (fast) 0.8 else 1.0)
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // format day:hour:min:sec

    fun fmtDhms(time: Double): String {

        fun Int.format(): String = if (this in 0..9) "0$this" else "$this"

        var t = time
        val days = if (t > 86400) {
            val d = (t / 86400).toInt()
            t %= 86400
            d
        } else null

        val hours = (t / 3600).toInt()
        t %= 3600

        val minutes = (t / 60).toInt()
        t %= 60

        val seconds = t.toInt()

        val dayString = if (days.notNull) "$days:" else ""
        val hourString =
            if (days.notNull) "${hours.format()}:" else if (hours > 0) "$hours:" else ""

        return "$dayString$hourString${minutes.format()}:${seconds.format()}"

    }


}

