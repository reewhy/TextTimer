import javafx.scene.media.AudioClip;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
    JToggleButton toggleButton;
    JLabel timer;
    Timer timeTimer;
    JButton startBtn;
    JButton stopBtn;

    public TextTimer(){
        timeCalculator = new TimeCalculator();

        // Get the saved settings from json file
        JSONParser jsonParser = new JSONParser();
        try(FileReader file = new FileReader("config.json")){
            Object obj = jsonParser.parse(file);
            JSONObject json = (JSONObject) obj;
            // Get the enum value from string
            timeCalculator.wpm = TimeCalculaterWPM.valueOf((String) json.get("WPM"));
        }catch (Exception ex){
            ex.printStackTrace();
        }

        // Create a frame
        JFrame frame = new JFrame();

        // Create scrollable text area
        textArea = new JTextArea(1, 1);
        scroll = new JScrollPane(textArea);

        // Create buttons
        JButton btn = new JButton("Calcola");
        JButton exeBtn = new JButton("Get text from photo");
        toggleButton = new JToggleButton("Punctuation");
        fastBtn = new JButton("FAST");
        mediumBtn = new JButton("MEDIUM");
        slowBtn = new JButton("SLOW");
        startBtn = new JButton("START");
        stopBtn = new JButton("STOP");
        timer = new JLabel("00:00:00");

        // Set text area
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Set scroll
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Set the timer label
        timer.setHorizontalAlignment(SwingConstants.CENTER);
        timer.setFont(new Font(Font.DIALOG, Font.PLAIN, 50));

        // Disable the stop btn
        stopBtn.setEnabled(false);

        // Set bounds
        scroll.setBounds(10, 10, 670, 490);
        btn.setBounds(10, 520,90, 30);
        timer.setBounds(0, 550, 700, 70);
        startBtn.setBounds(110, 520,90, 30);
        stopBtn.setBounds(210, 520, 90, 30);
        exeBtn.setBounds(10, 620, 180, 30);
        fastBtn.setBounds(580 ,520,90, 30);
        mediumBtn.setBounds(480, 520, 90, 30);
        slowBtn.setBounds(380, 520, 90, 30);
        toggleButton.setBounds(545, 620, 130, 30);

        // Add action listeners
        btn.addActionListener(this::calculateTime);
        fastBtn.addActionListener(this::fastSpeed);
        mediumBtn.addActionListener(this::mediumSpeed);
        slowBtn.addActionListener(this::slowSpeed);
        startBtn.addActionListener(this::startTime);
        stopBtn.addActionListener(this::stopTime);
        exeBtn.addActionListener(this::executePythonClick);

        // Add components to the frame
        frame.add(toggleButton);
        frame.add(scroll);
        frame.add(btn);
        frame.add(fastBtn);
        frame.add(mediumBtn);
        frame.add(slowBtn);
        frame.add(timer);
        frame.add(startBtn);
        frame.add(stopBtn);
        frame.add(exeBtn);

        // Get on which wpm is the calculator set on and disable its button
        switch(timeCalculator.wpm){
            case SLOW -> slowBtn.setEnabled(false);
            case FAST -> fastBtn.setEnabled(false);
            case MEDIUM -> mediumBtn.setEnabled(false);
        }

        // Set the frame and show it
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    private void executePythonClick(ActionEvent event) {
        // Get the file to open
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            // Create a new png file
            File newFile = new File("file.png");
            try {
                if (newFile.createNewFile()) {
                    System.out.println("File creato");
                }
                // Copy the select file to the new file
                Files.copy(selectedFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        try{
            // Create a new process
            Process p = Runtime.getRuntime().exec("python test.py");
            // Create a reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder finalText = new StringBuilder();
            // Get the result of the python code
            while((line = reader.readLine()) != null){
                System.out.println(line + "\n");
                finalText.append(line);
            }
            // Set the area text to the result of python code
            textArea.setText(finalText.toString());
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
        if(timeTimer == null){
            return;
        }
        sent = true;
        timeTimer.start();
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    private void updateTime(ActionEvent event) {
        // Get if the timer ended
        boolean ended = timeCalculator.secondStep();
        updateTimer();
        if(ended && sent){
            try{
                sent = false;
                // Send a notification
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage("some-icon.png");
                TrayIcon trayIcon = new TrayIcon(image, "Java AWT Tray Demo");
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
                trayIcon.displayMessage("Timer", "Tempo scaduto!", TrayIcon.MessageType.INFO);
                // Play an audio
                start();
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
        timeTimer.start();
    }

    public void updateTimer(){
        // Create the text for the timer label
        timeTimer.stop();
        int h = timeCalculator.h;
        int m = timeCalculator.m;
        int s = timeCalculator.s;
        StringBuilder builder = new StringBuilder();
        if(h < 10)
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
        // Set the time calculator text to the text area
        timeCalculator.setText(textArea.getText());
        // Obtain how much seconds you need to read that
        seconds = timeCalculator.calculateTime(toggleButton.isSelected());
        // Convert it from seconds to HH/mm/ss
        int s = (int)seconds % 60;
        int h = (int)seconds / 60;
        int m = h % 60;
        h = h / 60;
        // Set the time calculator time
        timeCalculator.setTime(h, m ,s);
        // Create a new timer
        timeTimer = new Timer(1000, this::updateTime);
        updateTimer();
    }

    // Set the speed to slow
    private void slowSpeed(ActionEvent event) {
        timeCalculator.setWpm(TimeCalculaterWPM.SLOW);
        slowBtn.setEnabled(false);
        fastBtn.setEnabled(true);
        mediumBtn.setEnabled(true);
        updateSettings();
    }

    // Set the speed to medium
    private void mediumSpeed(ActionEvent event) {
        timeCalculator.setWpm(TimeCalculaterWPM.MEDIUM);
        slowBtn.setEnabled(true);
        fastBtn.setEnabled(true);
        mediumBtn.setEnabled(false);
        updateSettings();
    }

    // Set the speed to fast
    private void fastSpeed(ActionEvent event) {
        timeCalculator.setWpm(TimeCalculaterWPM.FAST);
        slowBtn.setEnabled(true);
        fastBtn.setEnabled(false);
        mediumBtn.setEnabled(true);
        updateSettings();
    }

    public void start(){
        // Start an audio
        AudioClip ac = new AudioClip(new File("C:/Users/39353/Documents/TextTimer/kil.wav").toURI().toString());
        ac.play();
    }

    public void updateSettings(){
        // Update the json file
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
        // Create a new frame
        TextTimer textTimer = new TextTimer();
        System.out.println(textTimer + " avviato!");

        // Create a new json file if needed
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