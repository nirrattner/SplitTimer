package com.nirrattner.splittimer.views;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.nirrattner.splittimer.controllers.StateController;
import com.nirrattner.splittimer.controllers.listeners.FileStateListener;
import com.nirrattner.splittimer.models.FileState;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

@Singleton
public class SplitTimerMenuBar extends MenuBar implements FileStateListener {

  private final ConfigurationFileChooser configurationFileChooser;
  private final Provider<StateController> stateController;
  private final MenuItem openMenuItem;
  private final MenuItem saveMenuItem;

  @Inject
  public SplitTimerMenuBar(
      ConfigurationFileChooser configurationFileChooser,
      Provider<StateController> stateController) {
    this.configurationFileChooser = configurationFileChooser;
    this.stateController = stateController;

    this.openMenuItem = new MenuItem("Open...");
    KeyCombination openKeyCombination = new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN);
    this.openMenuItem.setAccelerator(openKeyCombination);
    this.openMenuItem.setOnAction(event -> configurationFileChooser.choose());

    this.saveMenuItem = new MenuItem("Save...");
    KeyCombination saveKeyCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN);
    this.saveMenuItem.setAccelerator(saveKeyCombination);
    this.saveMenuItem.setOnAction(event -> stateController.get().save());

    Menu menu = new Menu("File");
    menu.getItems().addAll(openMenuItem, saveMenuItem);

    useSystemMenuBarProperty().setValue(true);
    getMenus().add(menu);
  }

  @Override
  public void setState(FileState state) {
    this.saveMenuItem.disableProperty()
        .setValue(state.getFile().isEmpty());
  }
}
