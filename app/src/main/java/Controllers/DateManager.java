package Controllers;

import android.util.Log;

import java.time.LocalDate;

public class DateManager {

    //this method checks if a start date is before an end date
    public static boolean isStartBeforeEnd(String start, String end) {
        //parse values
        String startMonth = String.valueOf(start.charAt(0)) + String.valueOf(start.charAt(1));
        String startDay = String.valueOf(start.charAt(2)) + String.valueOf(start.charAt(3));
        String startYear = String.valueOf(start.charAt(4)) + String.valueOf(start.charAt(5)) + String.valueOf(start.charAt(6)) + String.valueOf(start.charAt(7));

        String endMonth = String.valueOf(end.charAt(0)) + String.valueOf(end.charAt(1));
        String endDay = String.valueOf(end.charAt(2)) + String.valueOf(end.charAt(3));
        String endYear = String.valueOf(end.charAt(4)) + String.valueOf(end.charAt(5)) + String.valueOf(end.charAt(6)) + String.valueOf(end.charAt(7));


        //convert to int
        int intMonth = Integer.parseInt(startMonth);
        int intDay = Integer.parseInt(startDay);
        int intYear = Integer.parseInt(startYear);
        int intMonthEnd = Integer.parseInt(endMonth);
        int intDayEnd = Integer.parseInt(endDay);
        int intYearEnd = Integer.parseInt(endYear);

        LocalDate startDate = LocalDate.of(intYear, intMonth, intDay);
        LocalDate endDate = LocalDate.of(intYearEnd, intMonthEnd, intDayEnd);


        if(startDate.isEqual(endDate) || endDate.isAfter(startDate)) {
            return true;
        }
        else {
            return false;
        }
    }

    //format number only string to readable date (strings as stored in Room database)
    public static String formatDate(String date) {
        //parse date values
        String startMonth = String.valueOf(date.charAt(0)) + String.valueOf(date.charAt(1));
        String startDay = String.valueOf(date.charAt(2)) + String.valueOf(date.charAt(3));
        String startYear = String.valueOf(date.charAt(4)) + String.valueOf(date.charAt(5)) + String.valueOf(date.charAt(6)) + String.valueOf(date.charAt(7));

        //store as MM/dd/YYYY and return
        return startMonth + "/" + startDay + "/" + startYear;
    }

    //remove all "/" from date
    public static String deFormatDate(String date) {
        //parse date values
        String startMonth = String.valueOf(date.charAt(0)) + String.valueOf(date.charAt(1));
        String startDay = String.valueOf(date.charAt(3)) + String.valueOf(date.charAt(4));
        String startYear = String.valueOf(date.charAt(6)) + String.valueOf(date.charAt(7)) + String.valueOf(date.charAt(8)) + String.valueOf(date.charAt(9));

        //store as MM/dd/YYYY and return
        return startMonth + startDay  + startYear;
    }


}
