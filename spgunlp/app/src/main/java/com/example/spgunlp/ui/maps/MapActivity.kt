package com.example.spgunlp.ui.maps
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.spgunlp.R

import android.graphics.Color
import android.graphics.Rect
import android.location.GpsStatus
import android.util.Log
import com.example.spgunlp.databinding.ActivityMapBinding

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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity(), MapListener, GpsStatus.Listener {





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
        mMap = binding.osmmap
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        //mMap.mapCenter
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

        var geoPoints = ArrayList<GeoPoint>();
        var polygon = Polygon();



        binding.drawBtn.setOnClickListener{
            binding.drawBtn.visibility = android.view.View.GONE
            binding.cancelBtn.visibility = android.view.View.VISIBLE
            binding.saveBtn.visibility = android.view.View.VISIBLE
            binding.crosshair.visibility = android.view.View.VISIBLE
            binding.markBtn.visibility = android.view.View.VISIBLE
            geoPoints = ArrayList<GeoPoint>();
            polygon = Polygon();
            polygon.setOnClickListener { polygon, mapView, eventPos ->
                if (binding.drawBtn.visibility==android.view.View.VISIBLE){
                    val builder= android.app.AlertDialog.Builder(this)
                    builder.setTitle("Eliminar Poligono")
                    builder.setMessage("Esta seguro que desea eliminar el poligono?")
                    builder.setPositiveButton("Si"){dialogInterface, which ->
                        mMap.overlays.remove(polygon)
                        mMap.invalidate()
                    }
                    builder.setNegativeButton("No"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    val alert=builder.create()
                    alert.show()
                }
                true
            }
            polygon.id= "polygon1"
            polygon.fillPaint.color = Color.parseColor("#1EFFE70E") //set fill color
            polygon.outlinePaint.color = Color.parseColor("#028A0F") //set outline color
        }

        binding.cancelBtn.setOnClickListener{
            binding.drawBtn.visibility = android.view.View.VISIBLE
            binding.cancelBtn.visibility = android.view.View.GONE
            binding.saveBtn.visibility = android.view.View.GONE
            binding.crosshair.visibility = android.view.View.GONE
            binding.markBtn.visibility = android.view.View.GONE
            mMap.overlays.remove(polygon)
        }

        binding.markBtn.setOnClickListener{
            mMap.overlays.remove(polygon)
            geoPoints.add(mMap.mapCenter as GeoPoint)
            polygon.points =geoPoints
            mMap.overlays.add(polygon)
            mMap.invalidate()
        }

        binding.saveBtn.setOnClickListener{
            binding.drawBtn.visibility = android.view.View.VISIBLE
            binding.cancelBtn.visibility = android.view.View.GONE
            binding.saveBtn.visibility = android.view.View.GONE
            binding.crosshair.visibility = android.view.View.GONE
            binding.markBtn.visibility = android.view.View.GONE
            // save map polygons to csv
            Log.i("points:",polygon.points.toString())
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


}