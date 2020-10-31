package com.nirrattner.splittimer.views.splits;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nirrattner.splittimer.controllers.listeners.SplitsStateListener;
import com.nirrattner.splittimer.models.Split;
import com.nirrattner.splittimer.models.SplitsState;
import com.nirrattner.splittimer.util.TimeFormatter;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SplitsPane extends ScrollPane implements SplitsStateListener {

  private static final String SPLIT_LABEL_STYLE_CLASS = "split-label";
  private static final String BEST_SPLIT_LABEL_STYLE_CLASS = "best-split-label";
  private static final String PLUS_SPLIT_LABEL_STYLE_CLASS = "plus-split-label";
  private static final String MINUS_SPLIT_LABEL_STYLE_CLASS = "minus-split-label";
  private static final double LABEL_WIDTH = 150;

  private final TimeFormatter timeFormatter;
  private List<HBox> splitPanes;

  @Inject
  public SplitsPane(TimeFormatter timeFormatter) {
    this.timeFormatter = timeFormatter;
    this.splitPanes = Collections.emptyList();

    this.fitToHeightProperty().setValue(true);
    this.fitToWidthProperty().setValue(true);
    this.pannableProperty().setValue(true);
    this.vbarPolicyProperty().setValue(ScrollBarPolicy.NEVER);
    VBox.setVgrow(this, Priority.ALWAYS);

    this.setFocusTraversable(false);
    this.setFocused(false);
  }

  @Override
  public void setState(SplitsState state) {
    ensureVisible(state.getCurrentIndex());
    VBox vBox = new VBox();
    splitPanes = state.getSplits().stream()
        .map(this::toSplitPane)
        .collect(Collectors.toUnmodifiableList());
    vBox.getChildren().addAll(splitPanes);
    setContent(vBox);
  }

  private HBox toSplitPane(Split split) {
    HBox hBox = new HBox();

    Label nameLabel = new Label(split.getConfiguration().getName());
    nameLabel.getStyleClass().add(SPLIT_LABEL_STYLE_CLASS);
    hBox.getChildren().add(nameLabel);

    split.getTimestamp()
        .stream()
        .flatMap(value -> split.getConfiguration().getBestRunTimestamp()
            .stream()
            .map(previousValue -> value - previousValue))
        .map(timeFormatter::formatSplitDifference)
        .map(Label::new)
        .peek(label -> label.getStyleClass().add(
            label.getText().startsWith("+")
                ? PLUS_SPLIT_LABEL_STYLE_CLASS
                : MINUS_SPLIT_LABEL_STYLE_CLASS))
        .peek(label -> label.getStyleClass().add(SPLIT_LABEL_STYLE_CLASS))
        .map(StackPane::new)
        .peek(pane -> pane.setAlignment(Pos.BASELINE_RIGHT))
        .peek(pane -> HBox.setHgrow(pane, Priority.ALWAYS))
        .forEach(hBox.getChildren()::add);

    split.getTimestamp()
        .or(split.getConfiguration()::getBestRunTimestamp)
        .stream()
        .map(timeFormatter::formatSplit)
        .map(Label::new)
        .peek(label -> {
          if (split.getTimestamp().isEmpty())
            label.getStyleClass().add(BEST_SPLIT_LABEL_STYLE_CLASS);
        })
        .peek(label -> label.getStyleClass().add(SPLIT_LABEL_STYLE_CLASS))
        .map(StackPane::new)
        .peek(pane -> pane.setAlignment(Pos.BASELINE_RIGHT))
        .peek(pane -> HBox.setHgrow(pane, Priority.ALWAYS))
        .peek(pane -> {
          if (split.getConfiguration().getBestRunTimestamp().isPresent() && split.getTimestamp().isPresent()) {
            pane.setMinWidth(LABEL_WIDTH);
            pane.setMaxWidth(LABEL_WIDTH);
          }
        })
        .forEach(hBox.getChildren()::add);
    return hBox;
  }

  private void ensureVisible(int index) {
    if (index < splitPanes.size()) {
      double viewHeight = getHeight();
      double splitsPaneHeight = getContent().getBoundsInLocal().getHeight();
      double paneBottomPosition = (index + 1) * splitPanes.get(index).getHeight();
      if (paneBottomPosition > viewHeight) {
        setVvalue((paneBottomPosition - viewHeight) / (splitsPaneHeight - viewHeight));
      } else {
        setVvalue(0);
      }
    }
  }
}
