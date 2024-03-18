package com.davidups.marvel.core.navigation

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.davidups.marvel.core.navigation.NavList.CHARACTER_DETAIL
import com.davidups.marvel.core.navigation.NavList.CHARACTER_LIST
import com.davidups.marvel.ui.features.character.models.CharacterDetailNavArgs
import com.davidups.marvel.ui.features.character.ui.detail.CharacterDetail
import com.davidups.marvel.ui.features.character.ui.list.CharactersList
import com.davidups.marvel.ui.features.character.viewmodels.CharactersViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Composable
fun MarvelNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CharacterList.route,
        modifier = modifier
    ) {
        composable(route = Screen.CharacterList.route) {
            CharactersList(viewModel = hiltViewModel<CharactersViewModel>())
        }
        composable(
            route = Screen.CharacterDetail.route,
            arguments = listOf(navArgument(NavArgs.ITEM_ID.key) { type = itemNavType })
        ) { backStackEntry ->
            val item =
                backStackEntry.arguments?.getParcelable<CharacterDetailNavArgs>(NavArgs.ITEM_ID.key)
            item?.let {
                CharacterDetail(it.characterView)
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object CharacterList : Screen(CHARACTER_LIST)
    data object CharacterDetail : Screen("$CHARACTER_DETAIL/{${NavArgs.ITEM_ID.key}}") {
        fun createRoute(item: CharacterDetailNavArgs): String =
            "$CHARACTER_DETAIL/${Uri.encode(Json.encodeToJsonElement(item).toString())}"
    }
}

enum class NavArgs(val key: String) {
    ITEM_ID("item")
}

val itemNavType = object : NavType<CharacterDetailNavArgs>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): CharacterDetailNavArgs? {
        return bundle.getParcelable(key) as CharacterDetailNavArgs?
    }

    override fun parseValue(value: String): CharacterDetailNavArgs {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: CharacterDetailNavArgs) {
        bundle.putParcelable(key, value)
    }
}

object NavList {
    const val CHARACTER_LIST = "characterList"
    const val CHARACTER_DETAIL = "characterDetail"
}