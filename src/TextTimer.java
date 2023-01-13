import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.script.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;

import javafx.scene.media.MediaPlayer;

@SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "RedundantExplicitVariableType"})
public class TextTimer extends  TimeCalculator{
    JTextArea textArea;
    JScrollPane scroll;
    float seconds;
    boolean sent = false;
    TimeCalculator timeCalculator;
    JButton fastBtn;
    JButton mediumBtn;
    JButton slowBtn;
    JLabel timer;
    Timer timeTimer;
    JButton startBtn;
    JButton stopBtn;

    public TextTimer(){
        timeCalculator = new TimeCalculator();
        JSONParser jsonParser = new JSONParser();
        try(FileReader file = new FileReader("config.json")){
            Object obj = jsonParser.parse(file);
            JSONObject json = (JSONObject) obj;
            timeCalculator.wpm = TimeCalculaterWPM.valueOf((String) json.get("WPM"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        JFrame frame = new JFrame();
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);

        textArea = new JTextArea(1, 1);
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(10, 10, 670, 490);
        frame.add(scroll);
        
        JButton btn = new JButton("Calcola");
        btn.setBounds(10, 520,90, 30);
        btn.addActionListener(this::calculateTime);
        frame.add(btn);

        fastBtn = new JButton("FAST");
        mediumBtn = new JButton("MEDIUM");
        slowBtn = new JButton("SLOW");
        fastBtn.setBounds(580 ,520,90, 30);
        mediumBtn.setBounds(480, 520, 90, 30);
        slowBtn.setBounds(380, 520, 90, 30);
        fastBtn.addActionListener(this::fastSpeed);
        mediumBtn.addActionListener(this::mediumSpeed);
        slowBtn.addActionListener(this::slowSpeed);
        frame.add(fastBtn);
        frame.add(mediumBtn);
        frame.add(slowBtn);

        switch(timeCalculator.wpm){
            case SLOW -> slowBtn.setEnabled(false);
            case FAST -> fastBtn.setEnabled(false);
            case MEDIUM -> mediumBtn.setEnabled(false);
        }
        timer = new JLabel("00:00:00");
        timer.setHorizontalAlignment(SwingConstants.CENTER);
        timer.setBounds(0, 550, 700, 70);
        timer.setFont(new Font(Font.DIALOG, Font.PLAIN, 50));
        frame.add(timer);
        startBtn = new JButton("START");
        startBtn.setBounds(110, 520,90, 30);
        startBtn.addActionListener(this::startTime);
        frame.add(startBtn);
        stopBtn = new JButton("STOP");
        stopBtn.setBounds(210, 520, 90, 30);
        stopBtn.addActionListener(this::stopTime);
        frame.add(stopBtn);
        stopBtn.setEnabled(false);

        JButton exeBtn = new JButton("Execute");
        exeBtn.setBounds(210, 620, 90, 30);
        exeBtn.addActionListener(this::executePythonClick);
        frame.add(exeBtn);
    }

    private void executePythonClick(ActionEvent event) {
        try{
            Process p = Runtime.getRuntime().exec("python test.py");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while((line = reader.readLine()) != null){
                System.out.println(line + "\n");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void stopTime(ActionEvent event) {
        timeTimer.stop();
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    private void startTime(ActionEvent event) {
        sent = true;
        timeTimer.start();
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    private void updateTime(ActionEvent event) {
        boolean ended = timeCalculator.secondStep();
        updateTimer();
        if(ended && sent){
            try{
                sent = false;
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage("some-icon.png");
                TrayIcon trayIcon = new TrayIcon(image, "Java AWT Tray Demo");
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
                trayIcon.displayMessage("Timer", "Tempo scaduto!", TrayIcon.MessageType.INFO);
                start();
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
        timeTimer.start();
    }

    public void updateTimer(){
        timeTimer.stop();
        int h = timeCalculator.h;
        int m = timeCalculator.m;
        int s = timeCalculator.s;
        StringBuilder builder = new StringBuilder();
        if(h<10)
            builder.append("0"+h);
        else
            builder.append(h);
        builder.append(":");
        if(m < 10)
            builder.append("0"+m);
        else
            builder.append(m);
        builder.append(":");
        if(s < 10)
            builder.append("0" + s);
        else
            builder.append(s);
        timer.setText(builder.toString());
    }

    private void calculateTime(ActionEvent event) {
        timeCalculator.setText(textArea.getText());
        seconds = timeCalculator.calculateTime();
        int s = (int)seconds % 60;
        int h = (int)seconds / 60;
        int m = h % 60;
        h = h / 60;
        timeCalculator.setTime(h, m ,s);
        timeTimer = new Timer(1000, this::updateTime);
        updateTimer();
    }

    private void slowSpeed(ActionEvent event) {
        timeCalculator.setWpm(TimeCalculaterWPM.SLOW);
        slowBtn.setEnabled(false);
        fastBtn.setEnabled(true);
        mediumBtn.setEnabled(true);
        updateSettings();
    }

    private void mediumSpeed(ActionEvent event) {
        timeCalculator.setWpm(TimeCalculaterWPM.MEDIUM);
        slowBtn.setEnabled(true);
        fastBtn.setEnabled(true);
        mediumBtn.setEnabled(false);
        updateSettings();
    }

    private void fastSpeed(ActionEvent event) {
        timeCalculator.setWpm(TimeCalculaterWPM.FAST);
        slowBtn.setEnabled(true);
        fastBtn.setEnabled(false);
        mediumBtn.setEnabled(true);
        updateSettings();
    }

    public void start(){
        AudioClip ac = new AudioClip(new File("C:/Users/39353/Documents/TextTimer/kil.wav").toURI().toString());
        ac.play();
    }

    public void updateSettings(){
        JSONObject settingsTxt = new JSONObject();
        settingsTxt.put("WPM", timeCalculator.wpm.name());

        try(FileWriter file = new FileWriter("config.json")){
            file.write(settingsTxt.toJSONString());
            file.flush();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args){
        TextTimer textTimer = new TextTimer();
        System.out.println(textTimer + " avviato!");

        File config = new File("config.json");
        try{
            if(config.createNewFile()){
                System.out.println("File creato");
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}