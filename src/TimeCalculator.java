public class TimeCalculator {
    String text;
    TimeCalculaterWPM wpm = TimeCalculaterWPM.SLOW;
    int h = 0, m = 0, s = 0;

    enum TimeCalculaterWPM{
        FAST(300),
        MEDIUM(250),
        SLOW(200);
        public final int WPM;
        TimeCalculaterWPM(int WPM){
            this.WPM = WPM;
        }
    }

    // Set the calculator WPM (Word per Minute)
    void setWpm(TimeCalculaterWPM newWpm){
        this.wpm = newWpm;
    }
    // Set calculator text
    void setText(String newText){
        this.text = newText;
    }
    // Calculate how much seconds you need to read the text
    float calculateTime(boolean pointsAndCommas){
        float expressionSeconds = 0;
        String[] words = this.text.split(" ");
        System.out.println(words.length);
        float nWords = words.length;
        if(pointsAndCommas){
            for(String word: words){
                char[] characters = word.toCharArray();
                for(char character: characters){
                    switch (character) {
                        case '.', '?' -> expressionSeconds += 2;
                        case ',' -> expressionSeconds += 1;
                    }
                }
            }
        }
        return nWords / this.wpm.WPM * 60 + expressionSeconds * this.wpm.WPM/1000;
    }
    // Change the time
    void setTime(int hour, int minute, int second){
        this.h = hour;
        this.m = minute;
        this.s = second;
    }
    // Decrease the time by one second
    boolean secondStep(){
        if (this.s == 0) {
            if (this.m == 0) {
                if (this.h == 0) {
                    return true;
                } else {
                    System.out.println("Tolta un'ora");
                    this.h--;
                    this.m = 59;
                    this.s = 59;
                }
            } else {
                System.out.println("Tolto un minuto");
                this.m--;
                this.s = 59;
            }
        } else {
            this.s--;
        }
        return false;
    }

}
