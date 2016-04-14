package jme3_ext_spatial_explorer;

import org.controlsfx.control.action.Action;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import javafx.scene.control.TreeItem;

public class Actions4Camera {

	public static void registerAllActions(SpatialExplorer se, SimpleApplication app) {
		registerAction_CameraLookAt(se, app);
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_CameraLookAt(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Look at", (evt) -> {
			Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
			app.enqueue(() -> {
				app.getCamera().lookAt(target.getWorldTranslation(), app.getCamera().getUp());
				return null;
			});
		}));
	}
	
	@SuppressWarnings("unchecked")
	public static void registerAction_CameraFit(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Fit view on object", (evt) -> {
			Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
			app.enqueue(() -> {
				cameraFit(target, app);
				return null;
			});
		}));
	}

	public static void cameraFit(Spatial target, SimpleApplication app) {
		Camera camera = app.getCamera();
		BoundingVolume bounds = target.getWorldBound();
		camera.lookAt(bounds.getCenter(), app.getCamera().getUp());
		BoundingBox bb = new BoundingBox();
		bb.clone(bounds);
		camera.contains(target.getWorldBound());
	}
}
