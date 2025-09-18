package org.squidfish.tuplenback.data.game.levels

import android.content.Context
import kotlinx.serialization.json.Json
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.SearchRepository
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.LevelDeserializationError
import org.squidfish.tuplenback.utils.Result

class LevelRepository(private val context: Context) : SearchRepository<GameSettings, LevelData> {
    private val levelCache = mutableMapOf<Game, GameLevelData>()

    override suspend fun get(key: LevelData): Result<GameSettings, Error> {
        levelCache[key.gameMode]?.let { return it.levels[key.level].asGameSettings }

        val levelConfigFileName = "config/${key.gameMode.name}.json"
        val levelsJson = context.assets.open(levelConfigFileName).bufferedReader().use {
            it.readText()
        }

        val levelData: GameLevelData = Json.decodeFromString(levelsJson)

        if (key.gameMode.name.lowercase() != levelData.gameType.lowercase()) {
            return Result.Error(LevelDeserializationError.InvalidGameType)
        }

        if (key.level != levelData.levels[key.level].levelId) {
            return Result.Error(LevelDeserializationError.LevelMismatch)
        }

        levelCache.put(key.gameMode, levelData)
        return levelData.levels[key.level].asGameSettings
    }

    override suspend fun insert(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }
}
