package jme3_ext_spatial_explorer;

import org.controlsfx.glyphfont.FontAwesome;
import com.jme3.bullet.BulletAppState;

import com.jme3.app.SimpleApplication;

public class Actions4Bullet {
	public static void registerAllActions(SpatialExplorer se, SimpleApplication app) {
		try {
			Class.forName("com.jme3.bullet.BulletAppState");
			registerBarAction_SceneInDebugPhysic(se, app);
		} catch(ClassNotFoundException exc) {
			System.err.println("Actions4Bullet not registered : "  +  exc.getMessage());
			//exc.printStackTrace();
		}
	}

	public static void registerBarAction_SceneInDebugPhysic(SpatialExplorer se, SimpleApplication app) {
		se.barActions.add(Helper.makeAction("Debug Physic", FontAwesome.Glyph.GEAR, (evt) -> {
			app.enqueue(() -> {
				BulletAppState s = app.getStateManager().getState(BulletAppState.class);
				if (s != null) {
					s.setDebugEnabled(!s.isDebugEnabled());
				}
				//physicsSpace.enableDebug(assetManager);
				return null;
			});
		}));
	}
}
