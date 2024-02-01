package com.example.spgunlp.repositories

import androidx.lifecycle.LiveData
import com.example.spgunlp.daos.VisitsDao
import com.example.spgunlp.model.AppVisit

class VisitsRepository(private val visitsDao: VisitsDao) {

    suspend fun addVisit(visit: AppVisit){
        visitsDao.addVisit(visit)
    }

    suspend fun insertVisits(visits: List<AppVisit>){
        visitsDao.insertVisits(visits)
    }

    suspend fun updateVisit(visit: AppVisit){
        visitsDao.updateVisit(visit)
    }

    suspend fun updateVisits(visits: List<AppVisit>){
        visitsDao.updateVisits(visits)
    }

    suspend fun clearVisits(){
        visitsDao.clearVisits()
    }

    fun getAllVisits(): LiveData<List<AppVisit>>{
        return visitsDao.getAllVisits()
    }

    fun getVisitById(id: Int): LiveData<AppVisit?>{
        return visitsDao.getVisitById(id)
    }
}