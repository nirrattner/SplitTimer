package com.nirrattner.splittimer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nirrattner.splittimer.controllers.HotkeyInputController;
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
    try {
      Injector injector = Guice.createInjector(new SplitTimerModule(stage));
      Scene scene = injector.getInstance(SplitTimerScene.class);
      HotkeyInputController hotkeyInputController = injector.getInstance(HotkeyInputController.class);

      PROVIDER.register(KeyStroke.getKeyStroke("SPACE"), hotkeyInputController);
      PROVIDER.register(KeyStroke.getKeyStroke("R"), hotkeyInputController);

      stage.setTitle(TITLE);
      stage.setScene(scene);
      stage.show();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }

  public static void main(String[] args) {
    try {
      launch(args);
    } catch (Exception e) {
      PROVIDER.reset();
      PROVIDER.stop();
    }
  }
}
