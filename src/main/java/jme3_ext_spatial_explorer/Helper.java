package jme3_ext_spatial_explorer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.shadow.ShadowUtil;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.css.StyleManager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;

// TODO add helper to displayDebug and displayFrustum of AbstractShadowRenderer (a SceneProcessor)
// TODO add helper to list SceneProcessor and display Properties
public class Helper {
	private static AtomicBoolean initialized = new AtomicBoolean(false);
	private static String FontAwesome4 = "FontAwesome";

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
		//if (!initialized.get()) {
			PlatformImpl.startup(() -> {
				Helper.initJfxStyle();
			});
		//}
	}

	//HACK: workaround see https://bitbucket.org/controlsfx/controlsfx/issue/370/using-controlsfx-causes-css-errors-and
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void initJfxStyle() {
		//use reflection because sun.util.logging.PlatformLogger.Level is not always available
		if (initialized.getAndSet(true)){
			// already initialized
			return;
		}
		try {
			//com.sun.javafx.Logging.getCSSLogger().setLevel(sun.util.logging.PlatformLogger.Level.SEVERE);
			Class<Enum> e = (Class<Enum>)Class.forName("sun.util.logging.PlatformLogger$Level");
			Object o = Class.forName("com.sun.javafx.Logging").getMethod("getCSSLogger").invoke(null);
			o.getClass().getMethod("setLevel", e).invoke(o, Enum.valueOf(e, "SEVERE"));
		} catch(ClassNotFoundException exc) {
			// ignore
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		GlyphFont f = new FontAwesome(Helper.class.getResourceAsStream("/Interface/Fonts/fontawesome-webfont-4.4.0.ttf"));
		GlyphFontRegistry.register(f);


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
		Node scene = new Node("_referential");
		AssetManager assetManager = app.getAssetManager();
		scene.attachChild(makeGrid(Vector3f.ZERO, 10, ColorRGBA.Blue, assetManager));
		scene.attachChild(makeCoordinateAxes(Vector3f.ZERO, assetManager));
		return scene;
	}

	public static Spatial makeGrid(Vector3f pos, int size, ColorRGBA color, AssetManager assetManager){
		Geometry g = makeShape("_grid", new Grid(size, size, 1.0f), color, assetManager, true);
		g.center().move(pos);
		return g;
	}

	public static Spatial makeCoordinateAxes(Vector3f pos, AssetManager assetManager){
		Node b = new Node("_axis");
		b.setLocalTranslation(pos);

		Geometry arrow = makeShape("x", new Arrow(Vector3f.UNIT_X), ColorRGBA.Red, assetManager, true);
		arrow.getMaterial().getAdditionalRenderState().setLineWidth(4); // make arrow thicker
		b.attachChild(arrow);

		arrow = makeShape("y", new Arrow(Vector3f.UNIT_Y), ColorRGBA.Green, assetManager, true);
		arrow.getMaterial().getAdditionalRenderState().setLineWidth(4); // make arrow thicker
		b.attachChild(arrow);

		arrow = makeShape("z", new Arrow(Vector3f.UNIT_Z), ColorRGBA.Blue, assetManager, true);
		arrow.getMaterial().getAdditionalRenderState().setLineWidth(4); // make arrow thicker
		b.attachChild(arrow);
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

	public static Action makeAction(String tooltip, FontAwesome.Glyph g, Consumer<ActionEvent> eventHandler) {
		initJfx();
		Action action = new Action("", eventHandler);
		GlyphFont fontAwesome = GlyphFontRegistry.font(FontAwesome4);
		action.setGraphic(fontAwesome.create(g).size(14));
		action.setLongText(tooltip);
		return action;
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_Refresh(SpatialExplorer se) {
		se.treeItemActions.add(new Action("Refresh", (evt) -> {
			TreeItem<Spatial> treeItem = (TreeItem<Spatial>)evt.getSource();
			se.update(treeItem.getValue(), treeItem);
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
	public static void registerAction_ShowWireframe(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Show Wireframe", (evt) -> {
			Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
			app.enqueue(() -> {
				target.breadthFirstTraversal(new SceneGraphVisitorAdapter(){
					public void visit(Geometry geom) {
						RenderState r = geom.getMaterial().getAdditionalRenderState();
						boolean wireframe = false;
						try {
							Field f = r.getClass().getDeclaredField("wireframe");
							f.setAccessible(true);
							wireframe = (Boolean) f.get(r);
						} catch(Exception exc) {
							exc.printStackTrace();
						}
						r.setWireframe(!wireframe);
					}
				});
				return null;
			});
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_ShowBound(SpatialExplorer se, SimpleApplication app) {
		Material matModel = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		matModel.setColor("Color", ColorRGBA.Orange);
		Material matWorld = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		matWorld.setColor("Color", ColorRGBA.Red);

		se.treeItemActions.add(new Action("Show Bounds", (evt) -> {
			Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
			app.enqueue(() -> {
				target.breadthFirstTraversal(new SceneGraphVisitorAdapter(){
					public void visit(Geometry geom) {
						ShowBoundsControl ctrl = geom.getControl(ShowBoundsControl.class);
						if (ctrl != null) {
							geom.removeControl(ctrl);
						} else {
							geom.addControl(new ShowBoundsControl(matModel, matWorld));
						}
					}
				});
				return null;
			});
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_Remove(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Remove", (evt) -> {
			TreeItem<Spatial> treeItem = ((TreeItem<Spatial>)evt.getSource());
			Spatial target = treeItem.getValue();
			app.enqueue(() -> {
				if (target.getParent() != null) {
					target.removeFromParent();
					Platform.runLater(() ->{
						se.update(treeItem.getParent().getValue(), treeItem.getParent());
					});
				}
				return null;
			});
		}));
	}

	public static void registerBarAction_SceneInWireframe(SpatialExplorer se, SimpleApplication app) {
		WireProcessor p = new WireProcessor(app.getAssetManager());
		se.barActions.add(makeAction("Scene In Wireframe", FontAwesome.Glyph.DIAMOND, (evt) -> {
			app.enqueue(() -> {
				if (app.getViewPort().getProcessors().contains(p)) {
					app.getViewPort().removeProcessor(p);
				} else {
					app.getViewPort().addProcessor(p);
				}
				return null;
			});
		}));
	}

	public static void registerBarAction_ShowStats(SpatialExplorer se, SimpleApplication app) {
		se.barActions.add(makeAction("Show Stats", FontAwesome.Glyph.ALIGN_LEFT, (evt) -> {
			app.enqueue(() -> {
				StatsAppState s = app.getStateManager().getState(StatsAppState.class);
				if (s == null) {
					s = new StatsAppState();
					app.getStateManager().attach(s);
					s.setDisplayStatView(true);
					s.setDisplayFps(false);
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
				return null;
			});
		}));
	}

	public static void registerBarAction_ShowFps(SpatialExplorer se, SimpleApplication app) {
		se.barActions.add(makeAction("Show FPS", FontAwesome.Glyph.DASHBOARD, (evt) -> {
			app.enqueue(() -> {
				StatsAppState s = app.getStateManager().getState(StatsAppState.class);
				if (s == null) {
					s = new StatsAppState();
					app.getStateManager().attach(s);
					s.setDisplayStatView(false);
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
				return null;
			});
		}));
	}


	public static void registerBarAction_ShowFrustums(SpatialExplorer se, SimpleApplication app) {
		se.barActions.add(makeAction("Show Frustums", FontAwesome.Glyph.VIDEO_CAMERA, (evt) -> {
			app.enqueue(() -> {
				Node grp = (Node) app.getRootNode().getChild("_frustums");
				if (grp != null) {
					grp.removeFromParent();
				} else {
					grp = new Node("_frustums");
					app.getRootNode().attachChild(grp);
					Set<Camera> cameras = new HashSet<>();
					for(ViewPort vp : app.getRenderManager().getMainViews()) {
						Camera cam = vp.getCamera();
						if (!cameras.contains(cam)) {
							grp.attachChild(createFrustum(cam, app.getAssetManager()));
							cameras.add(cam);
						}
					}
				}
				return null;
			});
		}));
	}

	public static Geometry createFrustum(Camera cam, AssetManager assetManager){
		Vector3f[] pts = new Vector3f[8];
		for(int i = 0; i < pts.length; i++) pts[i] = new Vector3f();
		ShadowUtil.updateFrustumPoints2(cam, pts);
		WireFrustum frustum = new WireFrustum(pts);
		Geometry frustumMdl = new Geometry("_frustum."+cam.getName(), frustum);
		frustumMdl.setCullHint(Spatial.CullHint.Never);
		frustumMdl.setShadowMode(ShadowMode.Off);
		frustumMdl.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
		frustumMdl.getMaterial().setColor("Color", ColorRGBA.Brown);
		frustumMdl.addControl(new AbstractControl() {
			@Override
			protected void controlUpdate(float tpf) {
				ShadowUtil.updateFrustumPoints2(cam, pts);
				frustum.update(pts);
			}

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}
		});
		return frustumMdl;
	}

	public static void setupSpatialExplorerWithAll(SimpleApplication app) {
		setupSpatialExplorerWithAll(app, app.getRootNode());
	}
	
	public static void setupSpatialExplorerWithAll(SimpleApplication app, final Node exploreNode) {
		app.enqueue(() -> {
			AppStateSpatialExplorer ase = new AppStateSpatialExplorer(exploreNode);
			SpatialExplorer se = ase.spatialExplorer;
			Helper.registerAction_Refresh(se);
			Helper.registerAction_ShowLocalAxis(se, app);
			Helper.registerAction_ShowWireframe(se, app);
			Helper.registerAction_ShowBound(se, app);
			Helper.registerAction_SaveAsJ3O(se, app);
			Helper.registerAction_Remove(se, app);
			Helper.registerBarAction_ShowFps(se, app);
			Helper.registerBarAction_ShowStats(se, app);
			Helper.registerBarAction_SceneInWireframe(se, app);
			Helper.registerBarAction_ShowFrustums(se, app);
			Actions4Animation.registerAllActions(se, app);
			Actions4Bullet.registerAllActions(se, app);
			app.getStateManager().attach(ase);
			return null;
		});
	}

	public static class ShowBoundsControl extends AbstractControl {
		final WireBox mwbx = new WireBox();
		final Geometry mgeo = new Geometry("", mwbx);
		final Transform mt = new Transform();
		final WireBox wwbx = new WireBox();
		final Geometry wgeo = new Geometry("", mwbx);
		final Transform wt = new Transform();

		ShowBoundsControl(Material matModel, Material matWorld) {
			mgeo.setMaterial(matModel);
			mgeo.setCullHint(Spatial.CullHint.Never);
			mgeo.setShadowMode(ShadowMode.Off);
			wgeo.setMaterial(matWorld);
			wgeo.setCullHint(Spatial.CullHint.Never);
			wgeo.setShadowMode(ShadowMode.Off);
		}

		@Override
		public void setSpatial(Spatial s) {
			super.setSpatial(s);
			if (s != null) {
				Node root = s.getParent();
				for(;root.getParent() != null; root = root.getParent());
				mgeo.setName("_bounds.model." + s.getName());
				root.attachChild(mgeo);
				wgeo.setName("_bounds.world." + s.getName());
				root.attachChild(wgeo);
			} else {
				wgeo.removeFromParent();
				mgeo.removeFromParent();
			}
		}

		@Override
		protected void controlUpdate(float tpf) {
			Geometry geom = (Geometry) getSpatial();
			if (geom != null) {
				// world
				BoundingBox wbb = (BoundingBox) geom.getWorldBound();
				//TODO use WireBox.makeGeometry ?
				//wwbx.fromBoundingBox(wbb);
				wwbx.updatePositions(wbb.getXExtent(), wbb.getYExtent(), wbb.getZExtent());
				wt.loadIdentity();
				wt.setTranslation(wbb.getCenter());
				wgeo.setLocalTransform(wt);
				// model
				BoundingBox mbb = (BoundingBox) geom.getModelBound();
				//TODO use WireBox.makeGeometry ?
				mwbx.updatePositions(mbb.getXExtent(), mbb.getYExtent(), mbb.getZExtent());
				mt.loadIdentity();
				mt.setTranslation(mbb.getCenter());
				mt.combineWithParent(geom.getWorldTransform());
				mgeo.setLocalTransform(mt);
			}
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}
	}
}
