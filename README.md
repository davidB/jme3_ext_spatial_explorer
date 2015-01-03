A simple (extensible) GUI window to display the content (Spatials) of a jmonkeyengine scene. It can be used at runtime, debug/test time.

Requirements: javafx 8

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
	app.enqueue(() -> {
		AppStateSpatialExplorer se = new AppStateSpatialExplorer();
		Helper.registerAction_Refresh(se.spatialExplorer);
		Helper.registerAction_ShowLocalAxis(se.spatialExplorer, app);
		app.getStateManager().attach(se);
		return null;
	});
```

see [Demo.java](src/test/java/samples/Demo.java] for a complete sample.

# Customisation

## Add Context Menu action
```
	@SuppressWarnings("unchecked")
	public static void registerAction_MyVerb(SpatialExplorer se) {
		se.actions.add(new Action("My Verb Label", (evt) -> {
			TreeItem<Spatial> treeItem = (TreeItem<Spatial>)evt.getSource();
			Spatial spatial = treeItem.getValue();
			// do my stuff
		}));
	}
```

see registerAction_Refresh or registerAction_ShowLocalAxis int [Helper.java](src/main/java/jme3_ext_spatial_explorer/Helper.java] for a complete sample.

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