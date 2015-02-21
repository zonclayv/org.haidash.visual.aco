package org.haidash.visual.aco.views;

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

		try {
			Platform.runLater(() -> {
				try {
					if (textArea == null) {
						return;
					}

					if (textArea.getText().length() == 0) {
						textArea.setText(message);
					} else {
						textArea.selectEnd();
						textArea.insertText(textArea.getText().length(), message);
					}

					textArea.selectEnd();

				} catch (final Throwable t) {
					System.out.println("Unable to append log to text area: " + t.getMessage());
				}
			});
		} catch (final IllegalStateException e) {
		}
	}
}
