package org.squidfish.tuplenback.presentation.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

sealed interface ScreenDestination {
    @Serializable
    data object MainScreen: ScreenDestination

    @Serializable
    data object GameSelectionScreen: ScreenDestination

    // FIXME: game is temporarily nullable until passing data between screens is reworked
    @Serializable
    data class GameScreen(val level: LevelData?): ScreenDestination

    @Serializable
    data class SummaryScreen(val level: LevelData): ScreenDestination
}

object AppNavTypes {
    val typeMap = mapOf(
        typeOf<LevelData>() to parcelableType<LevelData>(isNullableAllowed = true),
        typeOf<LevelData?>() to parcelableType<LevelData>(isNullableAllowed = true),
    )
}

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(
    isNullableAllowed = isNullableAllowed,
) {
    override fun get(bundle: Bundle, key: String): T? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            return bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): T = json.decodeFromString<T>(value)

    override fun serializeAsValue(value: T): String = Uri.encode(json.encodeToString(value))

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putParcelable(key, value)
    }
}
