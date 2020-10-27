package com.nirrattner.splittimer.views;

import com.google.inject.Inject;
import javafx.scene.Scene;

public class SplitTimerScene extends Scene {

  private static final int HEIGHT = 250;
  private static final int WIDTH = 550;

  @Inject
  public SplitTimerScene(SplitTimerParentPane parentPane) {
    super(parentPane, WIDTH, HEIGHT);

    this.getStylesheets().add("css/timer.css");
  }
}
