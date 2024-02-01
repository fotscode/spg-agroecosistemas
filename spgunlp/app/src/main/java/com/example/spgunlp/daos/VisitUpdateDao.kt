package com.example.spgunlp.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.VisitUpdate
import com.example.spgunlp.repositories.VisitsRepository

@Dao
interface VisitUpdateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVisit(visit: VisitUpdate)

    @Update
    suspend fun updateVisit(visit: VisitUpdate)

    @Query("SELECT visit FROM changes_table WHERE email = :email")
    fun getVisitsByEmail(email: String): LiveData<List<AppVisitUpdate>?>
}