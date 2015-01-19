package jme3_ext_spatial_explorer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.action.Action;

import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.SkeletonDebugger;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.css.StyleManager;

public class Helper {

	public static void dump(Node node, String prefix) {
		List<Spatial> children = node.getChildren();
		System.out.printf("%s %s (%d)\n", prefix, node.getName(), children.size());
		prefix = (prefix.length() == 0)? " +--":  ("\t"+ prefix);
		for (Spatial sp : children) {
			if (sp instanceof Node) dump((Node) sp, prefix);
			else System.out.printf("%s %s [%s]\n", prefix, sp.getName(), sp.getClass().getName());
		}
	}

	public static void initJfx() {
		//new JFXPanel();
		// Note that calling PlatformImpl.startup more than once is OK
		PlatformImpl.startup(() -> {
			Helper.initJfxStyle();
		});
	}

	//HACK: workaround see https://bitbucket.org/controlsfx/controlsfx/issue/370/using-controlsfx-causes-css-errors-and
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void initJfxStyle() {
		//use reflection because sun.util.logging.PlatformLogger.Level is not always available
		try {
			//com.sun.javafx.Logging.getCSSLogger().setLevel(sun.util.logging.PlatformLogger.Level.SEVERE);
			Class<Enum> e = (Class<Enum>)Class.forName("sun.util.logging.PlatformLogger$Level");
			Object o = Class.forName("com.sun.javafx.Logging").getMethod("getCSSLogger").invoke(null);
			o.getClass().getMethod("setLevel", e).invoke(o, Enum.valueOf(e, "SEVERE"));
		} catch(Exception exc) {
			exc.printStackTrace();
		}
//		StyleManager.getInstance().addUserAgentStylesheet(Thread.currentThread().getContextClassLoader().getResource( "com/sun/javafx/scene/control/skin/modena/modena.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(GlyphFont.class.getResource("glyphfont.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(CommandLinksDialog.class.getResource("commandlink.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(Dialogs.class.getResource("dialogs.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(Wizard.class.getResource("wizard.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(CustomTextField.class.getResource("customtextfield.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(CustomTextField.class.getResource("autocompletion.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(SpreadsheetView.class.getResource("spreadsheet.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("breadcrumbbar.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("gridview.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("info-overlay.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("listselectionview.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("masterdetailpane.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("notificationpane.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("notificationpopup.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("plusminusslider.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("popover.bss").toExternalForm());
		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("propertysheet.css").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("rangeslider.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("rating.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("segmentedbutton.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("snapshot-view.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("statusbar.bss").toExternalForm());
//		StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("taskprogressview.bss").toExternalForm());
	}

	public static Node makeScene(SimpleApplication app) {
		Node scene = new Node("demo");
		AssetManager assetManager = app.getAssetManager();
		scene.attachChild(makeGrid(Vector3f.ZERO, 10, ColorRGBA.Blue, assetManager));
		scene.attachChild(makeCoordinateAxes(Vector3f.ZERO, assetManager));
		return scene;
	}

	public static Spatial makeGrid(Vector3f pos, int size, ColorRGBA color, AssetManager assetManager){
		Geometry g = makeShape("wireframe grid", new Grid(size, size, 1.0f), color, assetManager, true);
		g.center().move(pos);
		return g;
	}

	public static Spatial makeCoordinateAxes(Vector3f pos, AssetManager assetManager){
		Node b = new Node("axis");
		b.setLocalTranslation(pos);

		Arrow arrow = new Arrow(Vector3f.UNIT_X);
		arrow.setLineWidth(4); // make arrow thicker
		b.attachChild(makeShape("x", arrow, ColorRGBA.Red, assetManager, true));

		arrow = new Arrow(Vector3f.UNIT_Y);
		arrow.setLineWidth(4); // make arrow thicker
		b.attachChild(makeShape("y", arrow, ColorRGBA.Green, assetManager, true));

		arrow = new Arrow(Vector3f.UNIT_Z);
		arrow.setLineWidth(4); // make arrow thicker
		b.attachChild(makeShape("z", arrow, ColorRGBA.Blue, assetManager, true));
		return b;
	}

	public static Geometry makeShape(String name, Mesh shape, ColorRGBA color, AssetManager assetManager, boolean wireframe){
		Geometry g = new Geometry(name, shape);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(wireframe);
		mat.setColor("Color", color);
		g.setMaterial(mat);
		return g;
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_Refresh(SpatialExplorer se) {
		se.treeItemActions.add(new Action("Refresh", (evt) -> {
			TreeItem<Spatial> treeItem = (TreeItem<Spatial>)evt.getSource();
			se.update(treeItem.getValue(), treeItem);
		}));
	}

	public static void registerBarAction_PrintToto(SpatialExplorer se) {
		se.barActions.add(new Action("Toto", (evt) -> {
			System.out.println("toto");
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_ShowLocalAxis(SpatialExplorer se, SimpleApplication app) {
		//Spatial axis = app.getRootNode().getChild("axis");
		Spatial axis = makeCoordinateAxes(Vector3f.ZERO, app.getAssetManager());
		axis.setQueueBucket(Bucket.Translucent);
		for(Spatial s : ((Node)axis).getChildren()) {
			((Geometry)s).getMaterial().getAdditionalRenderState().setDepthTest(false);
		}
		axis.setName("localAxis");
		AbstractControl axisSync = new AbstractControl() {
			@Override
			protected void controlUpdate(float tpf) {
				if (axis.getParent() == null) {
					app.getRootNode().attachChild(axis);
				}
				if (axis != getSpatial() && axis != getSpatial().getParent()) {
					axis.setLocalTransform(getSpatial().getWorldTransform());
				}
			}

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}
		};
		se.treeItemActions.add(new Action("Show Local Axis", (evt) -> {
			app.enqueue(()->{
				Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
				if (target != axisSync.getSpatial()) {
					if (axisSync.getSpatial() != null) {
						axisSync.getSpatial().removeControl(axisSync);
					}
					target.addControl(axisSync);
				}
				return null;
			});
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_SaveAsJ3O(SpatialExplorer se, SimpleApplication app) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("jMonkeyEngine Object (*.j3o)", "*.j3o")
		);
		se.treeItemActions.add(new Action("Save as .j3o", (evt) -> {
			System.out.println("target : " + evt.getTarget());
			File f0 = fileChooser.showSaveDialog(
					null
//					((MenuItem)evt.getTarget()).getGraphic().getScene().getWindow()
//					((MenuItem)evt.getTarget()).getParentPopup()
			);
			if (f0 != null) {
				File f = (f0.getName().endsWith(".j3o"))? f0 : new File(f0.getParentFile(), f0.getName() + ".j3o");
				app.enqueue(()->{
					Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
					new BinaryExporter().save(target, f);
					return null;
				});
			}
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_ShowSkeleton(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Show Skeleton", (evt) -> {
			Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
			app.enqueue(() -> {
				if (!(target instanceof Node)) {
					System.out.println("can't Show Skeleton on non-Node");
					return null;
				}
				Node n = (Node)target;
				String name = "skeletonDebugger";
				int i = -1;
				Spatial child;
				do {
					i++;
					child = n.getChild(name + i);
				} while (child != null && !(child instanceof SkeletonDebugger));
				if (child != null) {
					n.detachChild(child);
				} else {
					AnimControl control = n.getControl(AnimControl.class);
					if (control != null) {
						final SkeletonDebugger skeletonDebug = new SkeletonDebugger(name + i, control.getSkeleton());
						final Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
						mat.setColor("Color", ColorRGBA.Green);
						mat.getAdditionalRenderState().setDepthTest(false);
						skeletonDebug.setMaterial(mat);
						n.attachChild(skeletonDebug);
					}
				}
				return null;
			});
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_ShowWireframe(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Show Wireframe", (evt) -> {
			Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
			app.enqueue(() -> {
				if (!(target instanceof Geometry)) {
					System.out.println("can't Show Wireframe on non-Geometry");
					return null;
				}
				RenderState r = ((Geometry)target).getMaterial().getAdditionalRenderState();
				boolean wireframe = false;
				try {
					Field f = r.getClass().getDeclaredField("wireframe");
					f.setAccessible(true);
					wireframe = (Boolean) f.get(r);
				} catch(Exception exc) {
					exc.printStackTrace();
				}
				r.setWireframe(!wireframe);
				return null;
			});
		}));
	}

	public static void registerBarAction_SceneInWireframe(SpatialExplorer se, SimpleApplication app) {
		WireProcessor p = new WireProcessor(app.getAssetManager());
		se.barActions.add(new Action("Scene In Wireframe", (evt) -> {
			if (app.getViewPort().getProcessors().contains(p)) {
				app.getViewPort().removeProcessor(p);
			} else {
				app.getViewPort().addProcessor(p);
			}
		}));
	}

	public static void registerBarAction_SceneInDebugPhysic(SpatialExplorer se, SimpleApplication app) {
		se.barActions.add(new Action("Debug Physic", (evt) -> {
			BulletAppState s = app.getStateManager().getState(BulletAppState.class);
			if (s != null) {
				s.setDebugEnabled(!s.isDebugEnabled());
			}
			//physicsSpace.enableDebug(assetManager);
		}));
	}

	public static void registerBarAction_ShowStats(SpatialExplorer se, SimpleApplication app) {
		se.barActions.add(new Action("Show Stats", (evt) -> {
			StatsAppState s = app.getStateManager().getState(StatsAppState.class);
			if (s == null) {
				s = new StatsAppState();
				app.getStateManager().attach(s);
				s.setDisplayStatView(true);
			} else {
				boolean v = true;
				try {
					Field f = s.getClass().getDeclaredField("showStats");
					f.setAccessible(true);
					v = (Boolean) f.get(s);
				} catch(Exception exc) {
					exc.printStackTrace();
				}
				s.setDisplayStatView(!v);
			}
		}));
	}

	public static void registerBarAction_ShowFps(SpatialExplorer se, SimpleApplication app) {
		se.barActions.add(new Action("Show FPS", (evt) -> {
			StatsAppState s = app.getStateManager().getState(StatsAppState.class);
			if (s == null) {
				s = new StatsAppState();
				app.getStateManager().attach(s);
				s.setDisplayFps(true);
			} else {
				boolean v = true;
				try {
					Field f = s.getClass().getDeclaredField("showFps");
					f.setAccessible(true);
					v = (Boolean) f.get(s);
				} catch(Exception exc) {
					exc.printStackTrace();
				}
				s.setDisplayFps(!v);
			}
		}));
	}

}
