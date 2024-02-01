package com.example.spgunlp.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.repositories.VisitsRepository

@Dao
interface VisitsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addVisit(visit: AppVisit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVisits(visits: List<AppVisit>)

    @Update
    suspend fun updateVisits(visits: List<AppVisit>)

    @Update
    suspend fun updateVisit(visit: AppVisit)

    @Query("DELETE FROM visits_table")
    suspend fun clearVisits()

    @Query("SELECT * FROM visits_table WHERE id = :id")
    fun getVisitById(id: Int): LiveData<AppVisit?>

    @Query("SELECT * FROM visits_table")
    fun getAllVisits(): LiveData<List<AppVisit>>
}