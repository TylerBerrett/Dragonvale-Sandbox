package com.tylerb.dragonvalesandbox.android.view


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tylerb.dragonvalesandbox.android.R
import com.tylerb.dragonvalesandbox.android.nav.Routes
import com.tylerb.dragonvalesandbox.android.view.sandbox.SandboxScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        Routes.Sandbox,
        Routes.ParentFinder,
        Routes.AllDragons
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { route ->
                    val selected = currentDestination?.hierarchy?.any { it.route == route.name } == true
                    NavigationDrawerItem(
                        label = { Text(stringResource(id = route.text)) },
                        selected = selected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (!selected) {
                                navController.navigate(route.name)
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

            }
        }
    ) {

        NavHost(navController = navController, startDestination = Routes.Sandbox.name) {
            composable(Routes.Sandbox.name) {
                SandboxScreen(
                    modifier = modifier,
                    navController = navController,
                    drawerState = drawerState
                )
            }
            composable(Routes.ParentFinder.name) { Text(text = "Parent Finder") }
            composable(Routes.AllDragons.name) { Text(text = "All Dragons") }
        }

    }
    





}