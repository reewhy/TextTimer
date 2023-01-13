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
        System.out.println(words.length);
        float nWords = words.length;
        return nWords / this.wpm.WPM * 60;
    }

    void setTime(int hour, int minute, int second){
        this.h = hour;
        this.m = minute;
        this.s = second;
    }

    boolean secondStep(){
        switch(this.s){
            case 0:
                switch(this.m){
                    case 0:
                        switch(this.h){
                            case 0:
                                return true;
                            default:
                                System.out.println("Tolta un'ora");
                                this.h--;
                                this.m = 59;
                                this.s = 59;
                        }
                        break;
                    default:
                        System.out.println("Tolto un minuto");
                        this.m--;
                        this.s = 59;
                }
                break;
            default:
                this.s--;
        }
        return false;
    }

}
