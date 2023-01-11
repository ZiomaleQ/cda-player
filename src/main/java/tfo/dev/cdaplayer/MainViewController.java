package tfo.dev.cdaplayer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.transform.Scale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class MainViewController {
    @FXML
    private TextField url;

    Gson gson = new Gson();

    @FXML
    private MediaView mediaView;

    @FXML
    protected void onPlayButtonClick() {

        String urlString = url.getCharacters().toString();
        String html = null;

        try {
            URL page = new URL(urlString);

            InputStream stream = page.openStream();

            html = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            stream.close();

        } catch (IOException e) {

            //Write message for now
            System.out.println(e.getMessage());
        }

        if (html == null) return;

        Document doc = Jsoup.parse(html);
        Element elt = doc.getElementById("mediaplayer" + urlString.substring(urlString.lastIndexOf('/') + 1));

        assert elt != null;
        String playerData = elt.attr("player_data");

        JsonObject src = gson.fromJson(playerData, JsonObject.class);
        String videoKey = src.get("video").getAsJsonObject().get("file").getAsString();

        String videoUrl = decodeKey(videoKey);

        MediaPlayer mediaPlayer = new MediaPlayer(new Media(videoUrl));

        Scale scale = new Scale(0.6, 0.6);

        mediaView.getTransforms().add(scale);

        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.play();
    }

    String decodeKey(String videoKey) {
        String code = videoKey
                .replace("_XDDD", "")
                .replace("_CDA", "")
                .replace("_ADC", "")
                .replace("_CXD", "")
                .replace("_QWE", "")
                .replace("_Q5", "")
                .replace("_IKSDE", "");

        code = Pattern.compile("[a-zA-Z]").matcher(code).replaceAll(match -> {
            char ch = match.group().charAt(0);

            int firstExpr;

            if (Character.isUpperCase(ch)) {
                firstExpr = 90;
            } else {
                firstExpr = 122;
            }

            int initial = Character.codePointAt(match.group(), 0) + 13;

            if (firstExpr < initial) {
                initial -= 26;
            }

            return Character.toString(initial);
        });

        code = Pattern.compile("[a-zA-Z]").matcher(code).replaceAll(match -> {
            char ch = match.group().charAt(0);

            int firstExpr;

            if (Character.isUpperCase(ch)) {
                firstExpr = 90;
            } else {
                firstExpr = 122;
            }

            int initial = Character.codePointAt(match.group(), 0) + 13;

            if (firstExpr < initial) {
                initial -= 26;
            }

            return Character.toString(initial);
        });

        code = URLDecoder.decode(code, StandardCharsets.UTF_8);

        StringBuilder stringBuilder = new StringBuilder();

        for (int e = 0; e < code.length(); e++) {
            int f = code.codePointAt(e);
            char toAppend = code.charAt(e);
            if (32 <= f && 126 >= f) toAppend = Character.toChars(33 + (f + 14) % 94)[0];
            stringBuilder.append(toAppend);
        }

        code = stringBuilder.toString()
                .replace(".cda.mp4", "")
                .replace(".2cda.pl", ".cda.pl")
                .replace(".3cda.pl", ".cda.pl");


        if (code.contains("/upstream")) {
            code = code.replaceAll("/upstream", ".mp4/upstream");
        } else {
            code += ".mp4";
        }

        return "https://" + code;
    }
}