package com.nirrattner.splittimer.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import javafx.application.Platform;

import java.awt.event.KeyEvent;

@Singleton
public class HotkeyInputController implements HotKeyListener {

  private final StateController stateController;

  @Inject
  public HotkeyInputController(StateController stateController) {
    this.stateController = stateController;
  }

  @Override
  public void onHotKey(HotKey hotKey) {
    switch (hotKey.keyStroke.getKeyCode()) {
      case KeyEvent.VK_SPACE:
        Platform.runLater(stateController::split);
        break;
      case KeyEvent.VK_R:
        Platform.runLater(stateController::reset);
        break;
    }
  }
}
