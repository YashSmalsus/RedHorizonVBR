package com.smalsus.redhorizonvbr.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalenderUtils {

    public static String getyear() {
        String dateValue = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        try {
            dateValue = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dateValue;
    }

    public static String getmonth() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        return String.valueOf(month);
    }

    public static String getdate() {
        String dateValue = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        try {
            dateValue = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dateValue;
    }

    public static String getCurrentDateTime() {
        String dateValue = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
        try {
            dateValue = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dateValue;
    }

    public static String getCurrentTimeWithDate() {
        String dateValue = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        try {
            dateValue = sdf.format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dateValue;
    }

    public static String convertTimeDateFormat(String input) {
        if (input != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy 'at' hh:mm aa");
                Date date = inputFormat.parse(input);
                String formattedDate = outputFormat.format(date);
                System.out.println(formattedDate);
                return formattedDate;
            } catch (final ParseException e) {
                e.printStackTrace();
            }
            return input;
        }
        return " ";

    }

    public static int minuteAgo(long currentTime, long anotherTime) {
        long differenceInMillis = 0;
        differenceInMillis = currentTime - anotherTime;
        return (int) ((differenceInMillis) / 1000L / 60L);
    }

    public static int minuteAgo(String datetime) {
        Date date = null; // Parse into Date object
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = Calendar.getInstance().getTime(); // Get time now

        long differenceInMillis = 0;
        if (date != null) {
            differenceInMillis = now.getTime() - date.getTime();
        }
        long differenceInHours = (differenceInMillis) / 1000L / 60L; // Divide by millis/sec, secs/min, mins/hr
        return (int) differenceInHours;
    }

    public static String getDate(String dateString) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = format1.parse(dateString);
            DateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "xx";
        }
    }

    public static long getTimeLong(String dateString) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

            Date date = format1.parse(dateString);
            return date.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public static String getTime(String dateString) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = format1.parse(dateString);
            DateFormat sdf = new SimpleDateFormat("h:mm a");
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format((date));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "xx";
        }
    }

    public static int isTimeBetweenDate(String minDate, String maxDate) {
        // 1 - current  , 2 - future , 3 - past
        int status = 0;
        try {
            Date time1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(minDate);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            Date time2 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(maxDate);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);

            Calendar calendar3 = Calendar.getInstance();

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                status = 1;
            } else if (x.before(calendar1.getTime())) {
                status = 2;
            } else {
                status = 3;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static long getRandomNumber() {
        long x = (long) ((Math.random() * ((100000 - 0) + 1)) + 0);
        return x;
    }

    private String convertTimeFormat(String input) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh-mm");
            Date dateObj = sdf.parse(input);
            input = new SimpleDateFormat("hh-mm aa").format(dateObj);
            return input;
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return input;

    }

    public String getLocalFormatedDate(String oldDate) {

        if (oldDate == null) {
            return "";
        }

        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'UTC'");
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value;
        String dueDateAsNormal = "";
        try {
            value = oldFormatter.parse(oldDate);
            SimpleDateFormat newFormatter = new SimpleDateFormat("MM/dd/yyyy - hh:mm a");

            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }


}
