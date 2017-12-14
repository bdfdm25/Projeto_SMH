/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videointerativo;

/**
 *
 * @author Willyam
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.media.MediaPlayer.Status;
import javafx.event.EventHandler;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.TextField;


public class VideoInterativo extends BorderPane {

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar;
    
    ServerSocket serverSocket;

    public VideoInterativo(final MediaPlayer mediaPlayer) {
        
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.setDaemon(true); //terminate the thread when program end
        socketServerThread.start();

        this.mediaPlayer = mediaPlayer;
        setStyle("-fx-background-color: #FFFFFF;");
        mediaView = new MediaView(mediaPlayer);
        Pane mediaViewPane = new Pane();
        mediaViewPane.getChildren().add(mediaView);
        mediaViewPane.setStyle("-fx-background-color: black;");
        setCenter(mediaViewPane);
        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);
       
        final Button playButton = new Button(">");

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mediaPlayer.getStatus();
                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    return;
                }
                if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }
            }
        });
        
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                    playButton.setText("||");
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setText(">");
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            public void run() {
                duration = mediaPlayer.getMedia().getDuration();
                updateValues();
            }
        });

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    playButton.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });
        
        
        mediaBar.getChildren().add(playButton);
        Label space0 = new Label("   ");
        mediaBar.getChildren().add(space0);
        Label timeLabel = new Label("Timeline: ");
        mediaBar.getChildren().add(timeLabel);
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);

        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
        
        
        mediaBar.getChildren().add(timeSlider);
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);
        Label space2 = new Label("          ");
        mediaBar.getChildren().add(space2);
        Label space1 = new Label("          ");
        mediaBar.getChildren().add(space1);
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);

        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });

        mediaBar.getChildren().add(volumeSlider);
        setBottom(mediaBar);
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                @SuppressWarnings("deprecation")
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());

                    if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
                    }
                }
            });
        }
    }  

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }
    
    
    //Socket
   
    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 4242;
        int count = 0;

        @Override
        public void run() {
            try {
                Socket socket = null;                
                serverSocket = new ServerSocket(SocketServerPORT);

                while (true) {
                    socket = serverSocket.accept();
                    count++;
                    
                    //Start another thread 
                    //to prevent blocked by empty dataInputStream
                    Thread acceptedThread = new Thread(
                        new ServerSocketAcceptedThread(socket, count));
                    acceptedThread.setDaemon(true); //terminate the thread when program end
                    acceptedThread.start();

                }
            } catch (IOException ex) {
                Logger.getLogger(VideoInterativo.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }

    private class ServerSocketAcceptedThread extends Thread {

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        int count;

        ServerSocketAcceptedThread(Socket s, int c) {
            socket = s;
            count = c;
        }

         @Override
        public void run() {
            try {
                dataInputStream = new DataInputStream(
                    socket.getInputStream());
                dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());

                String messageFromClient = dataInputStream.readUTF();                
                System.out.println(messageFromClient);
                
                //Trata mensagens do cliente
                if(messageFromClient.equals("getTime"))
                {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    String msgReply = formatTime(currentTime, duration);
                    System.out.println(msgReply);
                    dataOutputStream.writeUTF(msgReply);
                }
                else if(messageFromClient.contains("setTime"))
                {
                    double time = Double.valueOf(messageFromClient.replace("setTime ", ""));
                    int min = (int)time * 60;                    
                    double seg = (time - (int)time)*100;
                    time = min + seg;
                    time = (1000*time);                    
                    System.out.println(time);
                    System.out.println(Duration.millis(time));
                    mediaPlayer.seek(Duration.millis(time));
                }
                                
            } catch (IOException ex) {
                Logger.getLogger(VideoInterativo.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(VideoInterativo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(VideoInterativo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(VideoInterativo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                    .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                    .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                            + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(VideoInterativo.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return ip;
    }
    
}