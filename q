[1mdiff --git a/src/org/ssh/ui/components/overlay/LoggerConsole.java b/src/org/ssh/ui/components/overlay/LoggerConsole.java[m
[1mindex 9a0f379..1e22ae8 100644[m
[1m--- a/src/org/ssh/ui/components/overlay/LoggerConsole.java[m
[1m+++ b/src/org/ssh/ui/components/overlay/LoggerConsole.java[m
[36m@@ -3,10 +3,7 @@[m [mpackage org.ssh.ui.components.overlay;[m
 import javafx.collections.transformation.FilteredList;[m
 import javafx.collections.transformation.SortedList;[m
 import javafx.fxml.FXML;[m
[31m-import javafx.scene.control.ChoiceBox;[m
[31m-import javafx.scene.control.Tab;[m
[31m-import javafx.scene.control.TabPane;[m
[31m-import javafx.scene.control.TableView;[m
[32m+[m[32mimport javafx.scene.control.*;[m[41m[m
 import javafx.scene.layout.GridPane;[m
 import javafx.scene.layout.Pane;[m
 import org.ssh.managers.manager.UI;[m
[36m@@ -73,7 +70,6 @@[m [mpublic class LoggerConsole extends UIComponent {[m
         }[m
 [m
         tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);[m
[31m-[m
         // Add a tab to see all logging[m
         tabPane.getTabs().add(new Tab("all", new LoggingTab("all", Logger.getLogger("org.ssh")).getComponent()));[m
         // Add a tab for all packages in the list tabNames. These are all packages in org.ssh where[m
