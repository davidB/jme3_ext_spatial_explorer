package samples;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.action.Action;

import jme3_ext_spatial_explorer.AppStateSpatialExplorer;
import jme3_ext_spatial_explorer.Helper;
import jme3_ext_spatial_explorer.SpatialExplorer;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
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
			app.getRootNode().attachChild(sampleShapes(app));
			return null;
		});
		//Setup SpatialExplorer
		Helper.setupSpatialExplorerWithAll(app);
//		app.enqueue(() -> {
//			AppStateSpatialExplorer se = new AppStateSpatialExplorer();
//			Helper.registerAction_Refresh(se.spatialExplorer);
//			Helper.registerAction_ShowLocalAxis(se.spatialExplorer, app);
//			Helper.registerAction_SaveAsJ3O(se.spatialExplorer, app);
//			Helper.registerAction_ShowSkeleton(se.spatialExplorer, app);
//			Helper.registerAction_ShowWireframe(se.spatialExplorer, app);
//			Helper.registerBarAction_ShowFps(se.spatialExplorer, app);
//			Helper.registerBarAction_ShowStats(se.spatialExplorer, app);
//			Helper.registerBarAction_SceneInWireframe(se.spatialExplorer, app);
//			Helper.registerBarAction_SceneInDebugPhysic(se.spatialExplorer, app);
//			app.getStateManager().attach(se);
//			return null;
//		});
		app.enqueue(() -> {
			AppStateSpatialExplorer se = app.getStateManager().getState(AppStateSpatialExplorer.class);
			registerBarAction_PrintToto(se.spatialExplorer);
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

	public static Spatial sampleShapes(SimpleApplication app) {
		Node anchor = new Node("anchor");
		float l = 0;
		for (float x = -5; x <6; x += 2.0, l++) {
			Geometry cube = Helper.makeShape("cube_"+x, new Box(0.5f, 0.5f, 0.5f), ColorRGBA.Yellow, app.getAssetManager(), false);
			cube.setLocalTranslation(new Vector3f(x, x*0.5f, 5));
			cube.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI * (1.0f/3f) * l, Vector3f.UNIT_Y));
			anchor.attachChild(cube);

			Geometry sphere = Helper.makeShape("sphere_"+x, new Sphere(8, 8, 0.5f), ColorRGBA.Cyan, app.getAssetManager(), false);
			sphere.setLocalTranslation(new Vector3f(x, x*0.5f, -5));
			anchor.attachChild(sphere);
		}
		return anchor;
	}

	public static void registerBarAction_PrintToto(SpatialExplorer se) {
		se.barActions.add(new Action("Toto", (evt) -> {
			System.out.println("toto");
		}));
	}
}
