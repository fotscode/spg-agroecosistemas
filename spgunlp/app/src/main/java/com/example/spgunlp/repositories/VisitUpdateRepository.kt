package com.example.spgunlp.repositories

import androidx.lifecycle.LiveData
import com.example.spgunlp.daos.VisitUpdateDao
import com.example.spgunlp.daos.VisitsDao
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitUpdate
import com.example.spgunlp.model.VisitUpdate

class VisitUpdateRepository(private val visitUpdateDao: VisitUpdateDao) {

    suspend fun addVisit(visit: VisitUpdate){
        visitUpdateDao.insertVisit(visit)
        visitUpdateDao.updateVisit(visit)
    }

    fun getVisitsByEmail(email: String): List<AppVisitUpdate>? {
        return visitUpdateDao.getVisitsByEmail(email).value
    }
}