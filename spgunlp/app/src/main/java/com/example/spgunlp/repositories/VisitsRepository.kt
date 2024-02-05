package com.example.spgunlp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.spgunlp.daos.VisitsDao
import com.example.spgunlp.model.AppImage
import com.example.spgunlp.model.AppUser
import com.example.spgunlp.model.AppVisit
import com.example.spgunlp.model.AppVisitParameters
import com.example.spgunlp.model.VisitUserJoin
import com.example.spgunlp.model.VisitWithImagesMembersAndParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VisitsRepository(private val visitsDao: VisitsDao) {

    suspend fun insertVisit(visit: AppVisit){
        visitsDao.insertVisit(visit)
        visit.imagenes?.let { images ->
            images.forEach {
               it.visitId = visit.id
            }
            visitsDao.insertImagesList(images)
        }
        visit.integrantes?.let { members ->
            visitsDao.insertUsersList(members)
            members.forEach {
                visitsDao.insertVisitUserJoin(VisitUserJoin(visit.id!!, it.id!!))
            }
        }
        visit.visitaParametrosResponse?.let { parameters ->
            parameters.forEach {
                it.visitId = visit.id
            }
            visitsDao.insertParametersList(parameters)
        }
    }

    suspend fun insertVisits(visits: List<AppVisit>){
        visitsDao.insertVisits(visits)
        visits.forEach {visit ->
            visit.imagenes?.let { images ->
                images.forEach {
                    it.visitId = visit.id
                }
                visitsDao.insertImagesList(images)
            }
            visit.integrantes?.let { members ->
                visitsDao.insertUsersList(members)
                members.forEach {
                    visitsDao.insertVisitUserJoin(VisitUserJoin(visit.id!!, it.id!!))
                }
            }
            visit.visitaParametrosResponse?.let { parameters ->
                parameters.forEach {
                    it.visitId = visit.id
                }
                visitsDao.insertParametersList(parameters)
            }
        }
    }

    suspend fun updateVisit(visit: AppVisit){
        try {
            visitsDao.updateVisit(visit)
            visit.imagenes?.let { images ->
                images.forEach {
                    it.visitId = visit.id
                }
                visitsDao.updateImagesList(images)
            }
            visit.integrantes?.let { members ->
                visitsDao.updateUsersList(members)
                members.forEach {
                    visitsDao.updateVisitUserJoin(VisitUserJoin(visit.id!!, it.id!!))
                }
            }
            visit.visitaParametrosResponse?.let { parameters ->
                parameters.forEach {
                    it.visitId = visit.id
                }
                visitsDao.updateParametersList(parameters)
            }
        } catch (e: Exception) {
            Log.e("SPGUNLP_INFO", e.message.toString())
            Log.e("SPGUNLP_INFO", e.cause.toString())
        }
    }

    suspend fun updateVisits(visits: List<AppVisit>){
        visitsDao.updateVisits(visits)
        /*
        visits.forEach { visit ->
            updateVisit(visit)
        }
         */
    }

    suspend fun clearVisits(){
        visitsDao.clearVisits()
        visitsDao.clearImagesList()
        visitsDao.clearUsersList()
        visitsDao.clearVisitUserJoin()
        visitsDao.clearParametersList()
    }

    fun getAllVisits(): List<AppVisit>{
        val visits = visitsDao.getAllFullVisits()
        val appVisitList = mutableListOf<AppVisit>()
        visits.forEach { visit ->
            appVisitList.add(
                AppVisit(
                    visit.visit.id,
                    visit.visit.comentarioImagenes,
                    visit.visit.estadoVisita,
                    visit.visit.fechaActualizacion,
                    visit.visit.fechaCreacion,
                    visit.visit.fechaVisita,
                    visit.imagenes,
                    visit.integrantes,
                    visit.visit.quintaResponse,
                    visit.visit.usuarioOperacion,
                    visit.parameters
                )
            )
        }
        return appVisitList
    }
    fun getVisitById(visitId: Int): LiveData<VisitWithImagesMembersAndParameters> {
        return visitsDao.getFullVisitById(visitId)
    }

}