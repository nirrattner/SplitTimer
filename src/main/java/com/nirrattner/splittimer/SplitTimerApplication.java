package com.nirrattner.splittimer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nirrattner.splittimer.controllers.HotkeyInputController;
import com.nirrattner.splittimer.controllers.InputController;
import com.nirrattner.splittimer.views.SplitTimerScene;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.KeyStroke;

public class SplitTimerApplication extends Application {

  private static final String TITLE = "Split Timer";
  private static final Provider PROVIDER = Provider.getCurrentProvider(false);

  @Override
  public void start(Stage stage) {
    Injector injector = Guice.createInjector(new SplitTimerModule(stage));
    Scene scene = injector.getInstance(SplitTimerScene.class);
    InputController inputController = injector.getInstance(InputController.class);
    HotkeyInputController hotkeyInputController = injector.getInstance(HotkeyInputController.class);

    scene.setOnKeyPressed(inputController);
    PROVIDER.register(KeyStroke.getKeyStroke("SPACE"), hotkeyInputController);

    stage.setTitle(TITLE);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    try {
      launch(args);
    } finally {
      PROVIDER.reset();
      PROVIDER.stop();
    }
  }
}
