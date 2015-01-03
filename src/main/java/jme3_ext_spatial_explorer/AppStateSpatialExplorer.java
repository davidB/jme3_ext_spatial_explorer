package jme3_ext_spatial_explorer;

import javafx.application.Platform;
import javafx.stage.Stage;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.sun.javafx.application.PlatformImpl;

public class AppStateSpatialExplorer extends AbstractAppState {
	public final SpatialExplorer spatialExplorer = new SpatialExplorer();
	SimpleApplication app;

	@Override
	public void initialize(AppStateManager stateManager, com.jme3.app.Application app) {
		super.initialize(stateManager, app);
		this.app =(SimpleApplication) app;
		initJFX();
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
					spatialExplorer.start(new Stage());
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
	public void initJFX() {
		//new JFXPanel();
		// Note that calling PlatformImpl.startup more than once is OK
		PlatformImpl.startup(new Runnable() {
			@Override public void run() {
				// No need to do anything here
			}
		});
	}
}

