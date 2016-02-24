package jme3_ext_spatial_explorer;

import org.controlsfx.control.action.Action;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;

import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class Actions4Animation {
	public static void registerAllActions(SpatialExplorer se, SimpleApplication app) {
		registerAction_ShowSkeleton(se, app);
		registerAction_ExploreAnimation(se, app);
	}

	
	@SuppressWarnings("unchecked")
	public static void registerAction_ShowSkeleton(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Show Skeleton", (evt) -> {
			Spatial target = ((TreeItem<Spatial>)evt.getSource()).getValue();
			app.enqueue(() -> {
				target.breadthFirstTraversal(new SceneGraphVisitorAdapter(){
					public void visit(Node n) {
						String name = "skeletonDebugger.";
						int i = -1;
						Spatial child;
						do {
							i++;
							child = n.getChild(name + i);
						} while (child != null && !(child instanceof SkeletonDebugger));
						if (child != null) {
							n.detachChild(child);
						} else {
							Skeleton sk = null;
							SkeletonControl sc = n.getControl(SkeletonControl.class);
							if (sc != null) {
								sk = sc.getSkeleton();
							}
							AnimControl control = n.getControl(AnimControl.class);
							if (sk == null && control != null) {
								sk = control.getSkeleton();
							}
							if (sk != null) {
								final SkeletonDebugger skeletonDebug = new SkeletonDebugger(name + i, sk);
								final Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
								mat.setColor("Color", ColorRGBA.Green);
								mat.getAdditionalRenderState().setDepthTest(false);
								skeletonDebug.setMaterial(mat);
								n.attachChild(skeletonDebug);
							}
						}
					}
				});
				return null;
			});
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_ExploreAnimation(SpatialExplorer se, SimpleApplication app) {
		se.treeItemActions.add(new Action("Explore Animation", (evt) -> {
			TreeItem<Spatial> treeItem = ((TreeItem<Spatial>)evt.getSource());
			Spatial target = treeItem.getValue();
			AnimationExplorer exp = new AnimationExplorer();
			registerAction_PlayAnimation(exp, app);
			registerAction_StopAnimation(exp, app);
			registerAction_ResetSkeleton(exp, app);
			exp.start(new Stage(), "Animation Explorer");
			exp.updateRoot(target);
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_PlayAnimation(AnimationExplorer exp, SimpleApplication app) {
		exp.treeItemActions.add(new Action("play", (evt) -> {
			TreeItem<Object> treeItem = ((TreeItem<Object>)evt.getSource());
			Object target = treeItem.getValue();
			if (target instanceof Animation) {
				AnimControl ac = ((Spatial)treeItem.getParent().getValue()).getControl(AnimControl.class);
				ac.clearChannels();

				Animation ani = ((Animation)target);
				AnimChannel channel = ac.createChannel();
				channel.setAnim(ani.getName());
				channel.setLoopMode(LoopMode.DontLoop);
				channel.setSpeed(1f);
			}
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_StopAnimation(AnimationExplorer exp, SimpleApplication app) {
		exp.treeItemActions.add(new Action("stop", (evt) -> {
			TreeItem<Object> treeItem = ((TreeItem<Object>)evt.getSource());
			Object target = treeItem.getValue();
			if (target instanceof Animation) {
				AnimControl ac = ((Spatial)treeItem.getParent().getValue()).getControl(AnimControl.class);
				ac.clearChannels();
			}
		}));
	}

	@SuppressWarnings("unchecked")
	public static void registerAction_ResetSkeleton(AnimationExplorer exp, SimpleApplication app) {
		exp.treeItemActions.add(new Action("reset skeleton", (evt) -> {
			TreeItem<Object> treeItem = ((TreeItem<Object>)evt.getSource());
			Object target = treeItem.getValue();
			if (target instanceof Animation) {
				AnimControl ac = ((Spatial)treeItem.getParent().getValue()).getControl(AnimControl.class);
				ac.clearChannels();
				Skeleton sk = ac.getSkeleton();
				if (sk != null) {
					sk.resetAndUpdate();
				}
			}
		}));
	}
}
