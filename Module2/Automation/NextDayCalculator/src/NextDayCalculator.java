import java.time.LocalDate;

public class NextDayCalculator {
    public static String nextDay(LocalDate date){
        return date.plusDays(1).toString();
    }
}
