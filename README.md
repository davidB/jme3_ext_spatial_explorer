A simple (extensible) GUI window to display the content (Spatials) of a jmonkeyengine scene. It can be used at runtime, debug/test time.

Requirements: javafx 8 (java 8u20+)

# Install

## build.gradle

```
repositories {
	//...
	maven { url "http://updates.jmonkeyengine.org/maven/"}
	maven { url "http://dl.bintray.com/jmonkeyengine/contrib" }
}

dependencies {
	//...
	testCompile 'net.alchim31.jme3:jme3_ext_spatial_explorer:0+'
}
```

## into java code

```
		SimpleApplication app = ...

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
			// register my custom Action and BarAction
			registerBarAction_PrintToto(se.spatialExplorer);
			return null;
		});
```

see [Demo.java](src/test/java/samples/Demo.java] for a complete sample.

# Customization

## Add Context Menu action
```
	@SuppressWarnings("unchecked")
	public static void registerAction_MyVerb(SpatialExplorer se) {
		se.treeItemActions.add(new Action("My Verb Label", (evt) -> {
			TreeItem<Spatial> treeItem = (TreeItem<Spatial>)evt.getSource();
			Spatial spatial = treeItem.getValue();
			// do my stuff
		}));
	}
```

see registerAction_Refresh or registerAction_ShowLocalAxis in [Helper.java](src/main/java/jme3_ext_spatial_explorer/Helper.java] for a complete sample.

## Add action on selection change

```
	//oldValue and newValue are Spatial (previous selected, a newly selected)
	se.spatialExplorer.selection.addListener((observable, oldValue, newValue) -> {
		app.enqueue(()->{
			if (oldValue != null) {
				// do job on oldValue
			}
			if (newValue != null) {
				// do job on newValue
			}
			return null;
		});
	});

```

## Add Top ToolBar action
```
	@SuppressWarnings("unchecked")
	public static void registerAction_MyVerb(SpatialExplorer se) {
		se.barActions.add(new Action("My Verb Label", (evt) -> {
			// do my stuff
		}));
	}
```

see registerBarAction_Xxx in [Helper.java](src/main/java/jme3_ext_spatial_explorer/Helper.java] for samples.

