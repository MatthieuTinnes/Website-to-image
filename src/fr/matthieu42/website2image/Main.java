package fr.matthieu42.website2image;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        for(int i =0 ; i <= 3 ; i++){
            if(getParameters().getRaw().get(i) == null){
                System.out.println("Usage : java -jar Website-to-image.jar [website] [delay] [Screenshot width] [Screenshot height]");
                return;
            }
        }
        String webPage = getParameters().getRaw().get(0);
        int delay = Integer.parseInt(getParameters().getRaw().get(1));
        int width = Integer.parseInt(getParameters().getRaw().get(2));
        int height = Integer.parseInt(getParameters().getRaw().get(3));

        System.out.println("Screen of " + webPage + "with a delay of " + delay + " milliseconds and a size of " + width + "x" + height);
        WebView webView = new WebView();
        webView.setPrefSize(width, height);
        ScrollPane pane = new ScrollPane();
        pane.setContent(webView);
        primaryStage.setScene(new Scene(pane));
        File capture = new File("cap.png");
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Timer timer = new Timer() ;
                TimerTask waitForJSLoad = new TimerTask() {
                    public void run() {
                        Platform.runLater(() -> {
                            WritableImage image = webView.snapshot(null, null);
                            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                            try {
                                ImageIO.write(bufferedImage, "png", capture);
                                System.out.println("Screenshot saved as cap.png");
                                primaryStage.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                };
                timer.schedule(waitForJSLoad, delay);

            }
        });
        webView.getEngine().load(webPage);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
