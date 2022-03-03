package com.epa.mhealthservice.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChallengeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFinishedChallenge(challenge: Challenge)

    @Query("SELECT * FROM challenge WHERE date = :date")
    fun getDailyChallenges(date:String)
}