CREATE DATABASE meeting_application;

DROP TABLE simple_meeting;

CREATE TABLE simple_meeting(
  id SERIAL NOT NULL UNIQUE,
  s_date DATE NOT NULL,
  e_date DATE NOT NULL,
  day_of_week character(3) NOT NULL,
  PRIMARY KEY(s_date, e_date, day_of_week)
);

DROP TABLE simple_meeting_exception;

CREATE TABLE simple_meeting_exception(
  id SERIAL NOT NULL,
  meeting_id INT NOT NULL REFERENCES simple_meeting(id),
  exception_date DATE NOT NULL,
  is_cancelled BOOLEAN,
  is_rescheduled BOOLEAN,
  rescheduled_date DATE,
  PRIMARY KEY(id, meeting_id)
);

CREATE TABLE meeting_user(
  id SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(50)
);

CREATE TABLE meeting(
  id SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  s_date DATE NOT NULL DEFAULT current_date,
  e_date DATE,
  s_time TIMESTAMP NOT NULL,
  e_time TIMESTAMP NOT NULL,
  is_full_day BOOLEAN DEFAULT FALSE,
  is_recurring BOOLEAN DEFAULT FALSE,
  parent_id INT,
  created_by_id INT NOT NULL REFERENCES user(id),
  created_date DATE NOT NULL DEFAULT current_date
);

CREATE TABLE recurrance_type (
  id SERIAL NOT NULL PRIMARY KEY,
  recurrance_type VARCHAR(20)
);

CREATE TABLE recurrance_pattern (
  meeting_id INT NOT NULL REFERENCES meeting(id),
  recurrance_type_id INT NOT NULL REFERENCES recurrance_type(id),
  separation_count INT,
  max_num_occurances INT,
  day_of_week INT,
  week_of_month INT,
  day_of_month INT,
  month_of_year INT,
  PRIMARY KEY(meeting_id)
);

CREATE TABLE meeting_exception (
  id INT NOT NULL PRIMARY KEY,
  meeting_id INT NOT NULL REFERENCES meeting(id),
  is_rescheduled BOOLEAN,
  is_cancelled BOOLEAN,
  s_date DATE NOT NULL,
  e_date DATE NOT NULL,
  s_time TIMESTAMP,
  e_time TIMESTAMP,
  is_full_day BOOLEAN DEFAULT FALSE,
  created_by_id INT NOT NULL,
  created_date DATE NOT NULL DEFAULT current_date
);

/* http://stackoverflow.com/questions/5030546/how-to-gt-the-count-of-current-month-sundays-in-psql */

CREATE TABLE calendar
(
  cal_date date NOT NULL,
  year_of_date integer NOT NULL,
  month_of_year integer NOT NULL,
  day_of_month integer NOT NULL,
  day_of_week character(3) NOT NULL,
  CONSTRAINT calendar_pkey PRIMARY KEY(cal_date),
  CONSTRAINT calendar_check CHECK (year_of_date::double precision = date_part('year'::text, cal_date)),
  CONSTRAINT calendar_check1 CHECK (month_of_year::double precision = date_part('month'::text, cal_date)),
  CONSTRAINT calendar_check2 CHECK (day_of_month::double precision = date_part('day'::text, cal_date)),
  CONSTRAINT calendar_check3 CHECK (day_of_week::text =
                                    CASE
                                      WHEN date_part('dow'::text, cal_date) = 0::double precision THEN 'Sun'::text
                                      WHEN date_part('dow'::text, cal_date) = 1::double precision THEN 'Mon'::text
                                      WHEN date_part('dow'::text, cal_date) = 2::double precision THEN 'Tue'::text
                                      WHEN date_part('dow'::text, cal_date) = 3::double precision THEN 'Wed'::text
                                      WHEN date_part('dow'::text, cal_date) = 4::double precision THEN 'Thu'::text
                                      WHEN date_part('dow'::text, cal_date) = 5::double precision THEN 'Fri'::text
                                      WHEN date_part('dow'::text, cal_date) = 6::double precision THEN 'Sat'::text
                                      ELSE NULL::text
                                      END)
)
  WITH (
    OIDS=FALSE
  );

ALTER TABLE calendar OWNER TO pos;

CREATE INDEX calendar_day_of_month
  ON calendar
    USING btree
    (day_of_month);

CREATE INDEX calendar_day_of_week
  ON calendar
    USING btree
    (day_of_week);

CREATE INDEX calendar_month_of_year
  ON calendar
    USING btree
    (month_of_year);

CREATE INDEX calendar_year_of_date
  ON calendar
    USING btree
    (year_of_date);

CREATE OR REPLACE FUNCTION insert_range_into_calendar(from_date date, to_date date)
  RETURNS void AS
$BODY$
DECLARE
  this_date date := from_date;

BEGIN

  while(this_date < to_date) LOOP
    INSERT INTO calendar (cal_date, year_of_date, month_of_year, day_of_month, day_of_week)
    VALUES (this_date, extract(year from this_date), extract(month from this_date), extract(day from this_date),
            CASE WHEN extract(dow from this_date) = 0 then 'Sun'
                 WHEN extract(dow from this_date) = 1 then 'Mon'
                 WHEN extract(dow from this_date) = 2 then 'Tue'
                 WHEN extract(dow from this_date) = 3 then 'Wed'
                 WHEN extract(dow from this_date) = 4 then 'Thu'
                 WHEN extract(dow from this_date) = 5 then 'Fri'
                 WHEN extract(dow from this_date) = 6 then 'Sat'
              end);
    this_date = this_date + interval '1 day';
  end loop;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;