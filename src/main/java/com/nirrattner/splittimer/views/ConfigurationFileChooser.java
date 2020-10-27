package com.nirrattner.splittimer.views;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.nirrattner.splittimer.controllers.StateController;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

@Singleton
public class ConfigurationFileChooser {

  private final FileChooser fileChooser;
  private final Stage stage;
  private final Provider<StateController> stateController;

  @Inject
  public ConfigurationFileChooser(
      FileChooser fileChooser,
      Stage stage,
      Provider<StateController> stateController) {
    this.fileChooser = fileChooser;
    this.stage = stage;
    this.stateController = stateController;

    this.fileChooser.setTitle("Open Configuration File");
    this.fileChooser.getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
  }

  public void choose() {
    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      stateController.get().load(file);
    }
  }
}
