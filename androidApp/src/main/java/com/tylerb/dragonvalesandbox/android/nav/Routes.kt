package com.tylerb.dragonvalesandbox.android.nav

import com.tylerb.dragonvalesandbox.android.R

sealed class Routes(val name: String, val text: Int) {
    object Sandbox : Routes("Sandbox", R.string.sandbox)
    object ParentFinder : Routes("ParentFinder", R.string.parent_finder)
    object AllDragons : Routes("AllDragons", R.string.all_dragons)
}