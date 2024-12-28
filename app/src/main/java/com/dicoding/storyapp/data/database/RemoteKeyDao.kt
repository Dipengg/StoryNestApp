package com.dicoding.storyapp.data.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Dao

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_key WHERE id = :id")
    suspend fun getRemoteKeyId(id: String): RemoteKey?

    @Query("DELETE FROM remote_key")
    suspend fun deleteRemoteKey()
}