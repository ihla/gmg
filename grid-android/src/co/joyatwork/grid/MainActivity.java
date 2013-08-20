package co.joyatwork.grid;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.ViewGroup;

import co.joyatwork.grid.tests.BasicShaderTest;

import com.android.debug.hv.ViewServer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AndroidApplication {
	private static final String TAG = "MainActivity";
	
	private MapView mapView;
    private GoogleMap map;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        cfg.useWakelock = true;
        // we need to change the default pixel format - since it does not include an alpha channel 
        // we need the alpha channel so the map will be seen behind the GL scene
        cfg.r = 8;
        cfg.g = 8;
        cfg.b = 8;
        cfg.a = 8;
        cfg.useGL20 = true;
        
        //TODO remove shader testing 
        //initialize(new GridGame(), cfg);
        initialize(new BasicShaderTest(), cfg);

        if (graphics.getView() instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) graphics.getView();
            // set transparent view
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            // put on the top of current window
            glView.setZOrderOnTop(true);
            
            //set mapView as the content view
            //set glView above mapView
            mapView = new MapView(this);
            mapView.onCreate(savedInstanceState);
            addContentView(mapView, 
            		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT ));
            glView.getParent().bringChildToFront(glView);
            
            setUpMapIfNeeded();
            }
        else {
            Gdx.app.error(TAG, "failed to add GLSurfaceView on the top of MapView");
		}
        
        //support for Hierarchy Viewer
        //TODO remember to disable ViewServer for production release!
        ViewServer.get(this).addWindow(this);
    }

    
    @Override
    protected void onResume() {
        mapView.onResume();
        //TODO remember to disable ViewServer for production release!
        ViewServer.get(this).setFocusedWindow(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        //TODO remember to disable ViewServer for production release!
        ViewServer.get(this).removeWindow(this);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = mapView.getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        
    }

}