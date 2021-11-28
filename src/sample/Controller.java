package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class Controller implements Initializable {

  @FXML
  private VBox vboxParent;

  @FXML
  private MediaView mvVideo;

  private MediaPlayer mpVideo;
  private Media mediaVideo;

  @FXML
  private HBox hboxControls;

  @FXML
  private HBox hboxVolume;

  @FXML
  private Button buttonPPR;


  @FXML
  private Label labelCurrentTime;
  @FXML
  private Label labelTotalTime;
  @FXML
  private Label labelFullScreen;
  @FXML
  private Label labelSpeed;
  @FXML
  private Label labelVolume;

  @FXML
  private Slider sliderVolume;
  @FXML
  private Slider sliderTime;

  private boolean atEndOfVideo = false;
  private boolean isPlaying = true;
  private boolean isMuted = true;

  private ImageView ivPlay;
  private ImageView ivPause;
  private ImageView ivRestart;
  private ImageView ivVolume;
  private ImageView ivFullScreen;
  private ImageView ivMute;
  private ImageView ivExit;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    final int IV_SIZE = 25;

    mediaVideo = new Media(new File("src/resources/final.mp4").toURI().toString());
    mpVideo = new MediaPlayer(mediaVideo);
    mvVideo.setMediaPlayer(mpVideo);

    Image imagePlay = new Image(new File("src/resources/play-btn.png").toURI().toString());
    ivPlay = new ImageView(imagePlay);
    ivPlay.setFitHeight(IV_SIZE);
    ivPlay.setFitWidth(IV_SIZE);

    Image imageStop = new Image(new File("src/resources/stop-btn.png").toURI().toString());
    ivPause = new ImageView(imageStop);
    ivPause.setFitHeight(IV_SIZE);
    ivPause.setFitWidth(IV_SIZE);

    Image imageRestart = new Image(new File("src/resources/restart-btn.png").toURI().toString());
    ivRestart = new ImageView(imageRestart);
    ivRestart.setFitHeight(IV_SIZE);
    ivRestart.setFitWidth(IV_SIZE);

    Image imageVol = new Image(new File("src/resources/volume-btn.png").toURI().toString());
    ivVolume = new ImageView(imageVol);
    ivVolume.setFitHeight(IV_SIZE);
    ivVolume.setFitWidth(IV_SIZE);

    Image imageFull = new Image(new File("src/resources/fullscreen.png").toURI().toString());
    ivFullScreen = new ImageView(imageFull);
    ivFullScreen.setFitHeight(IV_SIZE);
    ivFullScreen.setFitWidth(IV_SIZE);

    Image imageMute = new Image(new File("src/resources/mute.png").toURI().toString());
    ivMute = new ImageView(imageMute);
    ivMute.setFitHeight(IV_SIZE);
    ivMute.setFitWidth(IV_SIZE);

    Image imageExit = new Image(new File("src/resources/exitscreen.png").toURI().toString());
    ivExit = new ImageView(imageExit);
    ivExit.setFitHeight(IV_SIZE);
    ivExit.setFitWidth(IV_SIZE);

    buttonPPR.setGraphic(ivPause);
    labelVolume.setGraphic(ivMute);
    labelSpeed.setText("1X");
    labelFullScreen.setGraphic(ivFullScreen);

    buttonPPR.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        Button buttonPlay = (Button) actionEvent.getSource();
        if (atEndOfVideo) {
          sliderTime.setValue(0);
          atEndOfVideo = false;
          isPlaying = false;
        }
        if (isPlaying) {
          buttonPlay.setGraphic(ivPlay);
          mpVideo.pause();
          isPlaying = false;
        } else {
          buttonPlay.setGraphic(ivPause);
          mpVideo.play();
          isPlaying = true;
        }
      }
    });


    hboxVolume.getChildren().remove(sliderVolume);

    mpVideo.volumeProperty().bindBidirectional(sliderVolume.valueProperty());

    bindCurrentTimeLabel();

    sliderVolume.valueProperty().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        mpVideo.setVolume(sliderVolume.getValue());
        if (mpVideo.getVolume() != 0.0) {
          labelVolume.setGraphic(ivVolume);
          isMuted = false;
        } else {
          labelVolume.setGraphic(ivMute);
          isMuted = true;
        }
      }
    });

    labelSpeed.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (labelSpeed.getText().equals("1X")) {
          labelSpeed.setText("2X");
          mpVideo.setRate(2.0);
        } else {
          labelSpeed.setText("1X");
          mpVideo.setRate(1.0);
        }
      }
    });

    labelVolume.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (isMuted) {
          labelVolume.setGraphic(ivVolume);
          sliderVolume.setValue(0.2);
          isMuted = false;
        } else {
          labelVolume.setGraphic(ivMute);
          sliderVolume.setValue(0);
          isMuted = true;
        }
      }
    });

    labelVolume.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if (hboxVolume.lookup("#sliderVolume") == null) {
          hboxVolume.getChildren().add(sliderVolume);
          sliderVolume.setValue(mpVideo.getVolume());
        }
      }
    });

    hboxVolume.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        hboxVolume.getChildren().remove(sliderVolume);
      }
    });

    vboxParent.sceneProperty().addListener(new ChangeListener<Scene>() {
      @Override
      public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
        if (oldScene == null && newScene != null) {
          mvVideo.fitHeightProperty().bind(newScene.heightProperty().subtract(hboxControls.heightProperty().add(20)));
        }
      }
    });


    


  }

  public void bindCurrentTimeLabel() {
    labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return getTime(mpVideo.getCurrentTime()) + " / ";
      }
    }, mpVideo.currentTimeProperty()));
  }

  public String getTime(Duration time) {
    int hours = (int) time.toHours();
    int minutes = (int) time.toMinutes();
    int seconds = (int) time.toSeconds();

    if (seconds > 59) seconds = seconds % 60;
    if (minutes > 59) minutes = minutes % 60;
    if (hours > 59) hours = hours % 60;

    if (hours > 0) return String.format("%d:%02d:%02d",
            hours, minutes, seconds);
    else return String.format("%02d:%02d", minutes, seconds);

  }

}
