package org.haidash.visual.aco.ui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class TextAreaAppender extends WriterAppender {

    /**
     * Set the target TextArea for the logging information to appear.
     *
     * @param textArea
     */
    public static void setTextArea(final TextArea textArea) {
        TextAreaAppender.textArea = textArea;
    }

    private static volatile TextArea textArea = null;

    @Override
    public void append(final LoggingEvent loggingEvent) {

        final String message = this.layout.format(loggingEvent);

        Platform.runLater(() -> {

            if (textArea == null) {
                return;
            }

            final String text = textArea.getText();
            final int length = text.length();

            if (length == 0) {
                textArea.setText(message);
            } else {
                textArea.selectEnd();
                textArea.insertText(length, message);
            }

            textArea.selectEnd();

        });
    }
}
