
/* Sample query pulls all meeting occurances for each day of the week */
select simple_meeting.day_of_week, count(*) from calendar, simple_meeting where cal_date between simple_meeting.s_date
  AND simple_meeting.e_date AND calendar.day_of_week = simple_meeting.day_of_week group by simple_meeting.day_of_week;

/* Sample query pulls the meeting occurences and removes any exceptions (simple_meeting_exception) which is only 1 right now */
select simple_meeting.day_of_week, count(*) from calendar, simple_meeting where cal_date between simple_meeting.s_date
  AND simple_meeting.e_date AND calendar.day_of_week = simple_meeting.day_of_week AND calendar.cal_date
  NOT IN (select exception_date from simple_meeting_exception where simple_meeting.id = meeting_id) group by simple_meeting.day_of_week;

/* Base Query for getting the Day of Week and number of meetings in the scheduled meetings */
select simple_meeting.day_of_week, count(*) from calendar, simple_meeting where cal_date between simple_meeting.s_date
                                                                                  AND simple_meeting.e_date group by simple_meeting.day_of_week;




/* Query all occurances of a meeting on a weekday given the date ranges in simple_meeting */
select count(*), simple_meeting.day_of_week from simple_meeting inner join calendar on
  simple_meeting.day_of_week = calendar.day_of_week where cal_date between simple_meeting.s_date and
    simple_meeting.e_date group by simple_meeting.day_of_week;

/*Query all occurances of a meeting minus the ones we cancelled in the simple_meeting_exception */
select count(*), simple_meeting.day_of_week from simple_meeting inner join calendar on
  simple_meeting.day_of_week = calendar.day_of_week where cal_date between simple_meeting.s_date
    and simple_meeting.e_date and simple_meeting.day_of_week LIKE 'Sun' and
      cal_date NOT IN (Select cal_date from calendar inner join simple_meeting_exception on
        exception_date = cal_date where day_of_week LIKE 'Sun') group by simple_meeting.day_of_week ;