package org.squidfish.tuplenback.data.game.levels

import android.content.Context
import android.content.res.Resources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.SearchRepository
import org.squidfish.tuplenback.presentation.navigation.LevelData
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.LevelDeserializationError
import org.squidfish.tuplenback.utils.Result
import org.squidfish.tuplenback.utils.onError

/**
 * Get level configuration for [Game]s.
 *
 * @param[context] Application context
 * @param[autoInitCache] When true the content of all config files is automatically cached. See
 * [initializeCache]
 */
class LevelRepository(private val context: Context, private val autoInitCache: Boolean = false) :
    SearchRepository<GameSettings, LevelData> {

    private val levelCache = mutableMapOf<Game, GameLevelData>()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _cacheInitState = MutableStateFlow<CacheInitializationState>(CacheInitializationState.Uninitialized)
    val cacheInitState : StateFlow<CacheInitializationState> = _cacheInitState.asStateFlow()

    init {
        if (autoInitCache) {
            repositoryScope.launch {
                initializeCache()
            }
        }
    }

    /**
     * Get the [GameSettings] corresponding to the correct [LevelData]. An error-free get results in the [GameSettings]
     * of all levels being cached. The first level of a game should be passed as level 0.
     */
    override suspend fun get(key: LevelData): Result<GameSettings, Error> {
        if (key.level < 0) {
            return Result.Error(LevelDeserializationError.NonexistentLevel)
        }

        levelCache[key.gameMode]?.let {
            return if (it.levels.size <= key.level) {
                Result.Error(LevelDeserializationError.NonexistentLevel)
            } else {
                it.levels[key.level].asGameSettings
            }
        }

        val levelsJson: String = when (val res = readLevelData(key.gameMode)) {
            is Result.Success -> res.data
            is Result.Error -> return res
        }

        val levelData: GameLevelData = runCatching {
            Json.decodeFromString<GameLevelData>(levelsJson)
        }.getOrElse {
            return Result.Error(LevelDeserializationError.InvalidSyntax)
        }

        if (levelData.levels.size <= key.level) {
            return Result.Error(LevelDeserializationError.NonexistentLevel)
        }

        // check that each level has the correct level id
        for (levelId in 0..<levelData.levels.size) {
            if (levelId != levelData.levels[levelId].levelId) {
                return Result.Error(LevelDeserializationError.LevelMismatch)
            }
        }

        levelCache.put(key.gameMode, levelData)
        return levelData.levels[key.level].asGameSettings
    }

    /**
     * Cache all the level settings in memory. If a configuration error is detected, change [cacheInitState] to
     * [CacheInitializationState.Failed]. Missing game configuration files are ignored i.e. are not considered errors
     *
     * @see[cacheInitState]
     */
    suspend fun initializeCache() {
        if (cacheInitState.value != CacheInitializationState.Uninitialized) {
            return
        }

        _cacheInitState.value = CacheInitializationState.Loading

        Game.entries.forEach {
            get(LevelData(it, 0)).onError {
                if (it != LevelDeserializationError.ConfigNotFound) {
                    _cacheInitState.value = CacheInitializationState.Failed(it)
                    return@onError
                }
            }
        }

        _cacheInitState.value = CacheInitializationState.Ready
    }

    fun readLevelData(game: Game): Result<String, Error> = try {
        Result.Success(
            context.resources.openRawResource(game.resourceId).bufferedReader().use {
                it.readText()
            },
        )
    } catch (_: Resources.NotFoundException) {
        Result.Error(LevelDeserializationError.ConfigNotFound)
    }

    override suspend fun insert(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }
}

sealed interface CacheInitializationState {
    object Uninitialized : CacheInitializationState
    object Loading : CacheInitializationState
    object Ready : CacheInitializationState
    data class Failed(val error: Error): CacheInitializationState
}
