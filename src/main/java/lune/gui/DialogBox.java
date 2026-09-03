package lune.gui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A single chat message bubble: a Label holding the message text next to
 * an ImageView holding the speaker's avatar. Created right-aligned (user
 * style) by default; flip() switches it to left-aligned, for Lune's
 * replies.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load /view/DialogBox.fxml", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Flips this dialog box so the image is on the left and the text on
     * the right, instead of the default right-aligned (image-on-the-right)
     * layout used for the user's own messages.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a right-aligned dialog box for a message the user typed.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog box for one of Lune's replies.
     */
    public static DialogBox getLuneDialog(String text, Image image) {
        DialogBox db = new DialogBox(text, image);
        db.flip();
        return db;
    }
}
