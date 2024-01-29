package com.example.spgunlp.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.spgunlp.R

import android.graphics.Color
import android.graphics.Rect
import android.location.GpsStatus
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.spgunlp.databinding.ActivityMapBinding
import com.example.spgunlp.model.Poligono

import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polygon.OnClickListener
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity(), MapListener, GpsStatus.Listener {
    private lateinit var mPoligonoViewModel: PoligonoViewModel
    private lateinit var geoPoints: ArrayList<GeoPoint>
    private lateinit var polygon: Polygon


    lateinit var mMap: MapView
    lateinit var controller: IMapController;
    lateinit var mMyLocationOverlay: MyLocationNewOverlay;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        // init viewmodel
        mPoligonoViewModel = ViewModelProvider(this).get(PoligonoViewModel::class.java)

        val ID_VISIT = intent.getLongExtra("ID_VISIT", 0)

        // map config
        mMap = binding.osmmap
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())
        mMap.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)


        controller = mMap.controller

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                controller.setCenter(mMyLocationOverlay.myLocation);
                controller.animateTo(mMyLocationOverlay.myLocation)
            }
        }
        val mapPoint = GeoPoint(-34.9214500, -57.9545300)

        controller.setZoom(13.0)


        controller.animateTo(mapPoint)
        mMap.overlays.add(mMyLocationOverlay)

        mMap.addMapListener(this)

        // inicializar con datos de la base de datos
        mPoligonoViewModel.getPoliByIdVisit(ID_VISIT).observe(this, Observer { poligonos ->
            mMap.overlays.clear()
            poligonos.forEach {
                val geoPoints = it.coordenadas.replace("[", "").replace("]", "").split(",0.0,")
                val points = ArrayList<GeoPoint>()
                geoPoints.forEach {
                    val latlon = it.split(",")
                    points.add(GeoPoint(latlon[0].toDouble(), latlon[1].toDouble()))
                }
                polygon = Polygon()
                polygon.points = points
                polygon.fillPaint.color = Color.parseColor("#1EFFE70E") //set fill color
                polygon.outlinePaint.color = Color.parseColor("#028A0F") //set outline color
                polygon.setOnClickListener(onClickPolygon(it.id))
                mMap.overlays.add(polygon)
            }
        })


        binding.drawBtn.setOnClickListener {
            binding.drawBtn.visibility = android.view.View.GONE
            binding.cancelBtn.visibility = android.view.View.VISIBLE
            binding.saveBtn.visibility = android.view.View.VISIBLE
            binding.crosshair.visibility = android.view.View.VISIBLE
            binding.markBtn.visibility = android.view.View.VISIBLE
            geoPoints = ArrayList();
            polygon = Polygon();

            polygon.fillPaint.color = Color.parseColor("#1EFFE70E") //set fill color
            polygon.outlinePaint.color = Color.parseColor("#028A0F") //set outline color
        }

        binding.cancelBtn.setOnClickListener {
            binding.drawBtn.visibility = android.view.View.VISIBLE
            binding.cancelBtn.visibility = android.view.View.GONE
            binding.saveBtn.visibility = android.view.View.GONE
            binding.crosshair.visibility = android.view.View.GONE
            binding.markBtn.visibility = android.view.View.GONE
            mMap.overlays.remove(polygon)
            mMap.invalidate()
        }

        binding.markBtn.setOnClickListener {
            mMap.overlays.remove(polygon)
            geoPoints.add(mMap.mapCenter as GeoPoint)
            polygon.points = geoPoints
            mMap.overlays.add(polygon)
            mMap.invalidate()
        }

        binding.saveBtn.setOnClickListener {
            binding.drawBtn.visibility = android.view.View.VISIBLE
            binding.cancelBtn.visibility = android.view.View.GONE
            binding.saveBtn.visibility = android.view.View.GONE
            binding.crosshair.visibility = android.view.View.GONE
            binding.markBtn.visibility = android.view.View.GONE
            // save map polygons
            val poligono = Poligono(0, ID_VISIT, polygon.title, geoPoints.toString())
            val id = mPoligonoViewModel.addPoligono(poligono)
            id.observe(this, Observer {
                polygon.setOnClickListener(onClickPolygon(it))
            })

        }
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        // event?.source?.getMapCenter()
        //  Log.e("TAG", "onScroll   x: ${event?.x}  y: ${event?.y}", )
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        //  event?.zoomLevel?.let { controller.setZoom(it) }


        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false;
    }

    override fun onGpsStatusChanged(event: Int) {


        TODO("Not yet implemented")
    }

    private fun onClickPolygon(id: Long): OnClickListener{
        return OnClickListener { polygon, mapView, eventPos ->
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Eliminar Poligono")
            builder.setMessage("Esta seguro que desea eliminar el poligono?")
            builder.setPositiveButton("Si") { dialogInterface, which ->
                mPoligonoViewModel.deletePoliById(id)
                mMap.overlays.remove(polygon)
                mMap.invalidate()
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            val alert = builder.create()
            alert.show()
            true
        }
    }


}