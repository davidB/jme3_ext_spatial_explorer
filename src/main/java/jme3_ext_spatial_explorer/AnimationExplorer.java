package jme3_ext_spatial_explorer;

import javafx.scene.control.TreeItem;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Track;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

/**
 * L0 - rootSpatial
 * L1 - spatials with animation (can include rootSpatial)
 * L2 - animations of the parent spatial
 * L3 - tracks of parent animation
 *
 * @author David Bernard
 *
 */
public class AnimationExplorer extends Explorer0<Object, Spatial>{

	public void select(Animation value) {
		selectAction.select(value);
		selection.set(value);
	}

	@Override
	public void updateRoot(Spatial root) {
		rootItem.getChildren().clear();
		rootItem.setValue(root);
		if (root == null) return;
		exploreSpatial(root);
		root.breadthFirstTraversal(new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				exploreSpatial(spatial);
			}
		});
	}

	void exploreSpatial(Spatial s) {
		AnimControl ac = s.getControl(AnimControl.class);
		if (ac != null) {
			TreeItem<Object> itemL1 = new TreeItem<>();
			itemL1.setValue(s);
			rootItem.getChildren().add(itemL1);
			for(String aname : ac.getAnimationNames()) {
				TreeItem<Object> animItem = new TreeItem<>();
				Animation anim = ac.getAnim(aname);
				animItem.setValue(anim);
				itemL1.getChildren().add(animItem);
				for(Track t : anim.getTracks()) {
					TreeItem<Object> trackItem = new TreeItem<>();
					trackItem.setValue(t);
					animItem.getChildren().add(trackItem);
				}
			}
		}
		rootItem.setExpanded(true);
	}
}
