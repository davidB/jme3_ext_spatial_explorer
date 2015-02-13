package jme3_ext_spatial_explorer;

import javafx.application.Platform;
import javafx.stage.Stage;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public class AppStateSpatialExplorer extends AbstractAppState {
	public final SpatialExplorer spatialExplorer = new SpatialExplorer();
	SimpleApplication app;

	@Override
	public void initialize(AppStateManager stateManager, com.jme3.app.Application app) {
		super.initialize(stateManager, app);
		this.app =(SimpleApplication) app;
		Helper.initJfx();
		setEnabled(true);
	}

	@Override
	public void cleanup() {
		setEnabled(false);
		app = null;
		super.cleanup();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled && app != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					spatialExplorer.start(new Stage(), "Spatial Explorer");
					spatialExplorer.updateRoot(app.getRootNode());
				}
			});
		} else {
			Platform.runLater(new Runnable() {
				public void run() {
					spatialExplorer.stop();
					spatialExplorer.updateRoot(null);
				}
			});
		}
	};
}

