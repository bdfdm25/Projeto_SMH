/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videointerativo;

import java.io.File;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
/**
 *
 * @author Willyam
 */
public class Player extends Application {

    private static final String MEDIA = "file:///users/brenodm/Matisyahu.mp4";
    private static String arg1;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("MediaPlayer");
        Group root = new Group();
        Scene scene = new Scene(root, 600, 600);

        
        //Media media = new Media(new File("file:///users/brenodm/Matisyahu.mp4").toURI().toString());
        //Media media = new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv");
        //MediaPlayer mediaPlayer = new MediaPlayer(media);
        //mediaPlayer.setAutoPlay(true);
        
        
        Media media = new Media((arg1 != null) ? arg1 : MEDIA);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        
        VideoInterativo mediaControl = new VideoInterativo(mediaPlayer);
        scene.setRoot(mediaControl);
        
        
        /*
        WebView webview = new WebView();
        webview.getEngine().load("https://www.youtube.com/watch?v=Arp6nnGs6iI");
        webview.setPrefSize(1024, 768);
        scene = new Scene(webview);   
        */
        
        
        primaryStage.setHeight(600);
        primaryStage.setWidth(800);
        primaryStage.setScene(scene);        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
