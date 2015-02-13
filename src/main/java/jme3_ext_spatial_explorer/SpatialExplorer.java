package jme3_ext_spatial_explorer;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class SpatialExplorer extends Explorer0<Spatial, Node>{

	@Override
	public void updateRoot(Node value) {
		update(value, rootItem);
		rootItem.setExpanded(value != null);
	}

	void update(Spatial value, TreeItem<Spatial> item) {
		item.setValue(value);
		if (value == null) {
			item.getChildren().clear();
			return;
		}

		if (value instanceof Node) {
			// resync list without clearing to keep state of existing TreeItem (expends,...)
			List<Spatial> spatials = ((Node)value).getChildren();
			ObservableList<TreeItem<Spatial>> items = item.getChildren();
			int i = 0;
			for (i=0; i < spatials.size(); i++) {
				Spatial child = spatials.get(i);
				TreeItem<Spatial> childItem = null;
				while (items.size() > i) {
					TreeItem<Spatial> c = items.get(i);
					if (c.getValue().equals(child)) {
						childItem = c;
						break;
					}
					items.remove(i);
				}
				if (childItem == null) {
					childItem = new TreeItem<Spatial>();
					items.add(childItem);
				}
				update(child, childItem);
			}
			while (i < items.size()) {
				items.remove(i);
			}
		} else {
			item.getChildren().clear();
		}
	}
}
