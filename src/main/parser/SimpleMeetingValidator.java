package main.parser;
import Models.SimpleMeeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SimpleMeetingValidator {

    private static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final String DAY_OF_WEEK_REGEX = "[a-Z]{3}";
    private static Set<String> validDays = new LinkedHashSet<String>();
    private Pattern validPattern;

    public SimpleMeetingValidator(){
        validDays.add("Mon");
        validDays.add("Tue");
        validDays.add("Wed");
        validDays.add("Thu");
        validDays.add("Fri");
        validDays.add("Sat");
        validDays.add("Sun");
    }

    public SimpleMeetingValidator(LinkedHashSet<String> newValidDays){
        validDays = newValidDays;
    }

    public boolean validateSimpleMeeting(SimpleMeeting m){
        boolean isValid = false;
        isValid = validateDate(m.getStartDate());
        isValid = validateDate(m.getEndDate());
        isValid = validateDayOfWeek(m.getDayOfWeek());
        return isValid;
    }

    public boolean validateDayOfWeek(String dow){
        if(validDays.contains(dow))
            return true;
        else
            return false;

    }
    public boolean validateDate(String date){
        try{
            dateFormat.parse(date);
            return true;
        }catch(ParseException e){
            return false;
        }
    }
}
