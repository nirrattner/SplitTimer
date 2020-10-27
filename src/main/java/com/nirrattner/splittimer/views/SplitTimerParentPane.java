package com.nirrattner.splittimer.views;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nirrattner.splittimer.views.splits.SplitsPane;
import com.nirrattner.splittimer.views.timer.TimerPane;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

@Singleton
public class SplitTimerParentPane extends VBox {

  @Inject
  public SplitTimerParentPane(
      SplitsPane splitsPane,
      TimerPane timerPane,
      SplitTimerMenuBar menuBar) {
    this.setAlignment(Pos.CENTER_RIGHT);

    getChildren().addAll(menuBar, splitsPane, timerPane);
  }
}
