package jme3_ext_spatial_explorer;

import javafx.application.Platform;
import javafx.stage.Stage;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import com.jme3.scene.Node;

public class AppStateSpatialExplorer extends AbstractAppState {
	public final SpatialExplorer spatialExplorer = new SpatialExplorer();
	SimpleApplication app;
	Node exploreNode;

	public AppStateSpatialExplorer() { }
	
	public AppStateSpatialExplorer(Node exploreNode) {
		this.exploreNode = exploreNode;
	}
	
	@Override
	public void initialize(AppStateManager stateManager, com.jme3.app.Application app) {
		super.initialize(stateManager, app);
		this.app =(SimpleApplication) app;
		if(this.exploreNode == null) {
			this.exploreNode = this.app.getRootNode();
		}
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
					spatialExplorer.updateRoot(exploreNode);
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

