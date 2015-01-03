package jme3_ext_spatial_explorer;

import java.util.List;

import javafx.scene.control.TreeItem;

import org.controlsfx.control.action.Action;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

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
		se.actions.add(new Action("Refresh", (evt) -> {
			TreeItem<Spatial> treeItem = (TreeItem<Spatial>)evt.getSource();
			se.update(treeItem.getValue(), treeItem);
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_ShowLocalAxis(SpatialExplorer se, SimpleApplication app) {
		//Spatial axis = app.getRootNode().getChild("axis");
		Spatial axis = makeCoordinateAxes(Vector3f.ZERO, app.getAssetManager());
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
		se.actions.add(new Action("Show Local Axis", (evt) -> {
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
}
