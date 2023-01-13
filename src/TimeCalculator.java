import java.sql.Time;

public class TimeCalculator {
    String text;
    TimeCalculaterWPM wpm = TimeCalculaterWPM.SLOW;
    int h = 0, m = 0, s = 0;

    enum TimeCalculaterWPM{
        FAST(300),
        MEDIUM(250),
        SLOW(200);
        public final int WPM;
        private TimeCalculaterWPM(int WPM){
            this.WPM = WPM;
        }
    }
    void setWpm(TimeCalculaterWPM newWpm){
        this.wpm = newWpm;
    }
    void setText(String newText){
        this.text = newText;
    }
    float calculateTime(){
        String[] words = this.text.split(" ");
        float nWords = words.length;
        return nWords / this.wpm.WPM * 60;
    }

    void setTime(int hour, int minute, int second){
        this.h = hour;
        this.m = minute;
        this.s = second;
    }

    boolean secondStep(){
        if (this.s == 0) {
            if (this.m == 0) {
                if (this.h == 0) {
                     return true;
                } else {
                    this.h--;
                    this.m = 59;
                    this.h = 59;
                }
            }
            this.m--;
            this.s = 59;
        }
        this.s--;
        return false;
    }

}
