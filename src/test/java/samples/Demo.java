package samples;

import java.util.logging.Level;
import java.util.logging.Logger;

import jme3_ext_spatial_explorer.AppStateSpatialExplorer;
import jme3_ext_spatial_explorer.Helper;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

public class Demo {

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.WARNING);

		AppSettings settings = new AppSettings(true);
		settings.setResolution(1280, 720);
		settings.setVSync(true);
		settings.setFullscreen(false);

		SimpleApplication app = new SimpleApplication(){
			@Override
			public void simpleInitApp() {
			}
		};

		app.setSettings(settings);
		app.setShowSettings(false);
		app.setDisplayStatView(true);
		app.setDisplayFps(true);
		// !!!! without .setPauseOnLostFocus(false)you should switch focus from javafx window to jme to see update
		app.setPauseOnLostFocus(false);
		app.start();

		//Setup Camera
		app.enqueue(() -> {
			app.getFlyByCamera().setEnabled(true);
			app.getFlyByCamera().setDragToRotate(true);
			//app.getStateManager().detach(app.getStateManager().getState(FlyCamAppState.class));
			app.getInputManager().setCursorVisible(true);
			return null;
		});
		//Setup a default scene (grid + axis)
		app.enqueue(() -> {
			app.getRootNode().attachChild(Helper.makeScene(app));
			app.getRootNode().attachChild(sampleCube(app));
			return null;
		});
		//Setup SpatialExplorer
		app.enqueue(() -> {
			AppStateSpatialExplorer se = new AppStateSpatialExplorer();
			Helper.registerAction_Refresh(se.spatialExplorer);
			Helper.registerAction_ShowLocalAxis(se.spatialExplorer, app);
			Helper.registerAction_SaveAsJ3O(se.spatialExplorer, app);
			Helper.registerAction_ShowSkeleton(se.spatialExplorer, app);
			Helper.registerAction_ShowWireframe(se.spatialExplorer, app);
			Helper.registerBarAction_PrintToto(se.spatialExplorer);
			Helper.registerBarAction_SceneInWireframe(se.spatialExplorer, app);
			Helper.registerBarAction_SceneInDebugPhysic(se.spatialExplorer, app);
//			se.spatialExplorer.selection.addListener((observable, oldValue, newValue) -> {
//				app.enqueue(()->{
//					if (oldValue != null) {
//						oldValue.removeControl(axisSync);
//					}
//					if (newValue != null) {
//						newValue.addControl(axisSync);
//					}
//					return null;
//				});
//			});
			app.getStateManager().attach(se);
			return null;
		});
	}

	public static Spatial sampleCube(SimpleApplication app) {
		Geometry cube = Helper.makeShape("cube", new Box(0.5f, 0.5f, 0.5f), ColorRGBA.Brown, app.getAssetManager(), false);
		cube.setUserData("sample String", "string");
		cube.setUserData("sample int", 42);
		cube.setUserData("sample float", 42.0f);
		cube.setUserData("sample vector3f", new Vector3f(-2.0f, 3.0f, 4.0f));
		return cube;
	}

}
