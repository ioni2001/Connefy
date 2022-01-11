package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.Message;
import com.example.finalproject.domain.User;
import com.example.finalproject.utils.Constants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.w3c.dom.events.MouseEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ViewConversationController implements Observer{

    private Controller service;
    private Stage primaryStage;
    private User userInConversation;
    private Label messageToReply = null;

    @FXML
    private Label conversationWithLbl;

    @FXML
    private VBox chatPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField messageText;

    @FXML
    private CheckBox replyAllBtn;

    public void setService(Controller service){
        this.service = service;
        this.service.addObserver(this);
        conversationWithLbl.setText(userInConversation.getFirstName() + " " + userInConversation.getLastName());
        initModel();
    }

    public void setUserInConversation(User user){
        this.userInConversation = user;
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void initModel() {
        chatPane.setMinHeight(0);
        List<Message> messageList = this.service.viewConversation(this.service.getCurrentEmail(), this.userInConversation.getEmail());
        double layoutY = 10d;
        for(Message message:messageList){
            Label replyMessage = null;
            boolean hasReply = false;
            if(message.getReply() != null) {
                hasReply = true;
                replyMessage = new Label("Replied: " + message.getReply().getMessage());
                replyMessage.setFont(new Font(18));
                replyMessage.setTranslateY(layoutY - 5);
                replyMessage.setMaxWidth(160);
                replyMessage.setWrapText(true);
            }

            Label messageLabel = new Label(message.getMessage());
            messageLabel.setFont(new Font(24));
            messageLabel.setStyle("-fx-border-radius: 20px; -fx-background-radius: 20px;");
            messageLabel.setTranslateY(layoutY);
            messageLabel.setMaxWidth(160);
            messageLabel.setWrapText(true);
            messageLabel.setId(message.getId().toString());
            messageLabel.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>(){
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 1) {
                            messageToReply = messageLabel;
                            messageToReply.setStyle(messageToReply.getStyle() + "; " + "-fx-background-color: #8b7373");
                        }
                        if(event.getClickCount()== 2){
                            messageToReply.setStyle(messageToReply.getStyle() + "; " + "-fx-background-color: #C0C0C0");
                            messageToReply = null;
                        }
                    }
                }
            });

            Label dateLabel = new Label("Sent on " + message.getDate().format(Constants.DATE_TIME_FORMATTER));
            dateLabel.setFont(new Font(10));
            dateLabel.setTranslateY(layoutY + 5);
            layoutY += 35;
            if(message.getFrom().getEmail().equals(this.service.getCurrentEmail())){
                messageLabel.setTranslateX(550);
                messageLabel.setTextFill(Color.web("#FFFFFF"));
                messageLabel.setStyle(messageLabel.getStyle() + "; " + "-fx-background-color: #3558c2");
                if(hasReply){
                    replyMessage.setTranslateX(535);
                }
                dateLabel.setTranslateX(570);
            }
            else{
                messageLabel.setTranslateX(20);
                messageLabel.setStyle(messageLabel.getStyle() + ";" + "-fx-background-color: #C0C0C0");
                if(hasReply){
                    replyMessage.setTranslateX(5);
                }
                dateLabel.setTranslateX(40);
            }
            if(hasReply){
                chatPane.getChildren().add(replyMessage);
                chatPane.applyCss();
                chatPane.layout();
                chatPane.setMinHeight(chatPane.getMinHeight() + replyMessage.getHeight() + 40);
            }
            chatPane.getChildren().add(messageLabel);
            chatPane.getChildren().add(dateLabel);
            chatPane.applyCss();
            chatPane.layout();
            chatPane.setMinHeight(chatPane.getMinHeight() + messageLabel.getHeight() + 40);
            chatPane.setMinHeight(chatPane.getMinHeight() + dateLabel.getHeight());
        }
    }


    @FXML
    void handleReplyBtn(ActionEvent event){
        String message = messageText.getText();
        if(messageToReply == null)
            MessageAlert.showErrorMessage(null, "Select a message to reply!");
        if(!replyAllBtn.isSelected()) {
            this.service.replyMessage(Long.parseLong(messageToReply.getId()), message);
        }
        else{
            this.service.replyAll(Long.parseLong(messageToReply.getId()), message);
        }

        messageToReply.setStyle(messageToReply.getStyle() + "; " + "-fx-background-color: #C0C0C0");
        messageText.setText("");
        messageToReply = null;
    }

    @FXML
    void handleBackBtn(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("messanger-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        MessengerController messangerController = fxmlLoader.getController();
        messangerController.setService(service);
        messangerController.setStage(primaryStage);
    }

    @FXML
    void handleSendBtn(ActionEvent event) {
        String message = messageText.getText();
        this.service.sendMessage(Arrays.asList(userInConversation), message);
        messageText.setText("");
    }

    @Override
    public void update(Observable o, Object arg) {
        chatPane.getChildren().clear();
        initModel();
    }
}
