package jme3_ext_spatial_explorer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.PropertySheet.Mode;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.property.BeanProperty;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public class Explorer0<Entry, Root extends Entry> {
	TreeItem<Entry> rootItem = new TreeItem<>();
	Stage stage;
	PropertySheet details;
	ActionShowInPropertySheet<Entry> selectAction;
	public final ObjectProperty<Entry> selection = new SimpleObjectProperty<>();

	/** List of actions on TreeItem via ContextMenu. actions should be registered before start(). */
	public final List<Action> treeItemActions = new LinkedList<>();
	public final List<Action> barActions = new LinkedList<>();

	public Stage getStage() { return stage;	}
	public PropertySheet getDetails() { return details;	}

	MasterDetailPane makePane() {
		details = new PropertySheet();
		details.setMode(Mode.CATEGORY);
		details.setModeSwitcherVisible(true);
		details.setSearchBoxVisible(true);

		selectAction = new ActionShowInPropertySheet<Entry>("test", null, details);

		TreeView<Entry> tree = new TreeView<>(rootItem);
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)  -> {
			try {
				if (newValue == null) {
					select(null);
				} else {
					select(newValue.getValue());
				}
			} catch(Exception exc){
				exc.printStackTrace();
			}
		});

		tree.setCellFactory((treeview) -> new MyTreeCell());

		//StackPane root = new StackPane();
		MasterDetailPane pane = new MasterDetailPane();
		pane.setMasterNode(tree);
		pane.setDetailNode(details);
		pane.setDetailSide(Side.RIGHT);
		pane.setDividerPosition(0.5);
		pane.setShowDetailNode(true);
		return pane;
	}

	public ToolBar makeBar() {
		ToolBar b = new ToolBar();
		for(Action a : barActions) {
			javafx.scene.Node n = a.getGraphic();
			if (n == null) {
				n = ActionUtils.createButton(a);
			}
			b.getItems().add(n);
		}
		return b;
	}

	public void start(Stage primaryStage, String title) {
		stop();
		this.stage = primaryStage;
		primaryStage.setTitle(title);
		BorderPane root = new BorderPane();
		root.setTop(makeBar());
		root.setCenter(makePane());
		primaryStage.setScene(new Scene(root, 600, 500));
		primaryStage.show();
	}

	public void stop() {
		if (stage != null) {
			updateRoot(null);
			stage.hide();
			stage.close();
			stage = null;
		}
	}

	public void updateRoot(Root value) {
	}

	public void select(Entry value) {
		selectAction.select(value);
		selection.set(value);
	}

	class MyTreeCell extends TextFieldTreeCell<Entry> {
		private ContextMenu menu = new ContextMenu();

		public MyTreeCell() {
			editableProperty().set(false);
			setContextMenu(menu);
			for (Action a : treeItemActions) {
				MenuItem mi = ActionUtils.createMenuItem(a);
				//MenuItem mi = new MenuItem(a.getText());
				mi.setOnAction((evt) -> {
					try {
						a.handle(new ActionEvent(getTreeItem(), evt.getTarget()));
					} catch(Exception exc) {
						exc.printStackTrace();
					}
				});
				menu.getItems().add(mi);
			}
		}
	}
}

class ActionShowInPropertySheet<T> extends Action {

	T bean;
	final PropertySheet propertySheet;

	public ActionShowInPropertySheet(String title, T bean, PropertySheet propertySheet) {
		super(title);
		setEventHandler(this::handleAction);
		this.bean = bean;
		this.propertySheet = propertySheet;
	}

	public void select(T v) {
		bean = v;
		handle(null);
	}

	private void handleAction(ActionEvent ae) {

		// retrieving bean properties may take some time
		// so we have to put it on separate thread to keep UI responsive
		Service<?> service = new Service<ObservableList<Item>>() {

			@Override
			protected Task<ObservableList<Item>> createTask() {
				return new Task<ObservableList<Item>>() {
					@Override
					protected ObservableList<Item> call() throws Exception {
						return bean == null ? null : getProperties(bean);
					}
				};
			}

		};
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void handle(WorkerStateEvent e) {
				ObservableList<Item> items = (ObservableList<Item>) e.getSource().getValue();
				if (items != null) {
					propertySheet.getItems().setAll(items);
				} else {
					propertySheet.getItems().clear();
				}
			}
		});
		service.start();

	}

	/**
	 * Given a JavaBean, this method will return a list of {@link Item} intances,
	 * which may be directly placed inside a {@link PropertySheet} (via its
	 * {@link PropertySheet#getItems() items list}.
	 *
	 * @param bean The JavaBean that should be introspected and be editable via
	 *      a {@link PropertySheet}.
	 * @return A list of {@link Item} instances representing the properties of the
	 *      JavaBean.
	 */
	public static ObservableList<Item> getProperties(final Object bean) {
		ObservableList<Item> list = FXCollections.observableArrayList();

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
			for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
				if (isProperty(p) && ! p.isHidden() && p.getReadMethod() != null) {
					BeanProperty bp = new BeanProperty(bean, p);
					bp.setEditable(false);
					list.add(bp);
				}
			}
			if (bean instanceof Spatial) {
				Spatial sp = (Spatial)bean;
				for(String key : sp.getUserDataKeys()) {
					list.add(new BasicItem("UserData", key, sp.getUserData(key)));
				}
				for(int i = 0; i < sp.getNumControls(); i++){
					Control ctrl = sp.getControl(i);
					list.add(new BasicItem("Controls", ctrl.getClass().getSimpleName(), ctrl));
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		return list;
	}

	private static boolean isProperty(final PropertyDescriptor p) {
		//TODO  Add more filtering
		return p.getWriteMethod() != null && !p.getPropertyType().isAssignableFrom(EventHandler.class);
	}

	static class BasicItem implements PropertySheet.Item {
		public BasicItem(String category, String name, Object value) {
			super();
			this.category = category;
			this.name = name;
			this.value = value;
		}

		final String category;
		final String name;
		final Object value;

		@Override
		public void setValue(Object value) {
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Class<?> getType() {
			return getValue().getClass();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getCategory() {
			return category;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isEditable() {
			return false;
		}
	}
}