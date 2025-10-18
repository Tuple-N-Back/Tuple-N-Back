package org.squidfish.tuplenback.models

import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.Result

interface Repository<T> {
    suspend fun insert(data: T): Result<Unit, Error>
    suspend fun delete(data: T): Result<Unit, Error>
}

interface RecentRepository<T> : Repository<T> {
    suspend fun getRecent(): Result<T?, Error>
}

interface SearchRepository<T, SearchParam> : Repository<T> {
    suspend fun get(key: SearchParam): Result<T?, Error>
}
