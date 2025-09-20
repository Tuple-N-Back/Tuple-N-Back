package org.squidfish.tuplenback.data.game.levels

import android.content.Context
import android.util.Log
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
import org.squidfish.tuplenback.models.CachedSearchRepository
import org.squidfish.tuplenback.presentation.navigation.LevelData
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.LevelDeserializationError
import org.squidfish.tuplenback.utils.Result
import org.squidfish.tuplenback.utils.ValidationError
import org.squidfish.tuplenback.utils.onError


class LevelRepository(private val context: Context) : CachedSearchRepository<GameSettings, LevelData> {
    private val TAG = "LevelRepository"
    private val levelCache = mutableMapOf<Game, GameLevelData>()

    /**
     * True if the level settings for all [Game]s have been loaded
     */
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _cacheInitState = MutableStateFlow<CacheInitializationState>(CacheInitializationState.Uninitialized)
    val cacheInitState : StateFlow<CacheInitializationState> = _cacheInitState.asStateFlow()

    init {
        // FIXME: isn't called?
        repositoryScope.launch {
            initializeCache()
        }
    }

    // The first level is level 0
    override suspend fun get(key: LevelData): Result<GameSettings, Error> {
        Log.d(TAG, "Retrieving level config for $key")
        levelCache[key.gameMode]?.let { return it.levels[key.level].asGameSettings }
        Log.d(TAG, "Config is not in cache")

        val levelConfigFileName = "levels/${key.gameMode.name}.json"
        val levelsJson = context.assets.open(levelConfigFileName).bufferedReader().use {
            it.readText()
        }

        Log.d(TAG, "Read json config file")
        val levelData: GameLevelData = Json.decodeFromString(levelsJson)
        Log.d(TAG, "Decoded to GameLevelData")

        if (key.gameMode.name.lowercase() != levelData.gameType.lowercase()) {
            Log.e(TAG, "Invalid file/gamemode name")
            return Result.Error(LevelDeserializationError.InvalidGameType)
        }

        if (key.level != levelData.levels[key.level].levelId) {
            Log.e(TAG, "Level mismatch")
            return Result.Error(LevelDeserializationError.LevelMismatch)
        }

        Log.d(TAG, "Putting in cache and returning")
        levelCache.put(key.gameMode, levelData)
        return levelData.levels[key.level].asGameSettings
    }

    /**
     * Fetch level settings from cache. [cacheInitState] must be [CacheInitializationState.Ready]
     *
     * @see[initializeCache]
     * @see[cacheInitState]
     */
    override fun getFromCache(key: LevelData): Result<GameSettings, Error> {
        if (cacheInitState.value != CacheInitializationState.Ready) {
            Log.e(TAG, "Cache not initialized, status: ${cacheInitState.value}")
            return Result.Error(LevelDeserializationError.IncompleteCache)
        }

        val gameLevelData = levelCache[key.gameMode] ?: return Result.Error(ValidationError.MissingLevel)

        if (gameLevelData.levels.size < key.level) {
            return Result.Error(ValidationError.MissingLevel)
        }

        return gameLevelData.levels[key.level].asGameSettings
    }

    /**
     * Cache all the level settings in memory.
     *
     * @see[cacheInitState]
     */
    override suspend fun initializeCache() {
        Log.d(TAG, "Start initializing cache")
        if (cacheInitState.value != CacheInitializationState.Uninitialized)
            Log.e(TAG, "Cache already initialized, status: ${cacheInitState.value}")
            return

        _cacheInitState.value = CacheInitializationState.Loading

        Game.entries.forEach {
            get(LevelData(it, 0)).onError {
                _cacheInitState.value = CacheInitializationState.Failed(it)
                Log.e(TAG, "Cache initialization error $it")
                return@onError
            }
        }

        Log.i(TAG, "Cache is initialized")
        _cacheInitState.value = CacheInitializationState.Ready
    }


    override suspend fun insert(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }
}

sealed class CacheInitializationState {
    object Uninitialized : CacheInitializationState()
    object Loading : CacheInitializationState()
    object Ready : CacheInitializationState()
    data class Failed(val error: Error): CacheInitializationState()
}
