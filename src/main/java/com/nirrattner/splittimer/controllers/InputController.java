package com.nirrattner.splittimer.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

@Singleton
public class InputController implements EventHandler<KeyEvent> {

  private final StateController stateController;

  @Inject
  public InputController(StateController stateController) {
    this.stateController = stateController;
  }

  @Override
  public void handle(KeyEvent event) {
    switch (event.getCode()) {
      case R:
        stateController.reset();
        break;
    }
  }
}
