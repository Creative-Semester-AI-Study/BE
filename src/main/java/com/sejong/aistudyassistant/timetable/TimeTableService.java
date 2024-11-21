package com.sejong.aistudyassistant.timetable;

import com.sejong.aistudyassistant.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeTableService {
    private TimeTable timeTable;

    public void createTimeTable(){
        this.timeTable = new TimeTable();
    }

    public void addSubjectTimeTable(String daysString, Subject subject) {
        List<String> daysList = Arrays.stream(daysString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        for (DayTimeTable dayTimeTable : timeTable.getTimeTables()) {
            for (String day : daysList) {
                if (dayTimeTable.getDay().equals(day)) {
                    dayTimeTable.addDayTimeTable(subject);
                    break;
                }
            }
        }
    }

    public List<Subject> checkCertainDayTimeTable(String day) {
        if (timeTable == null || timeTable.getTimeTables() == null) {
            return Collections.emptyList();
        }
        for (DayTimeTable dayTimeTable : timeTable.getTimeTables()) {
            if (dayTimeTable.getDay().equals(day)) {
                return dayTimeTable.getSubjects();
            }
        }
        return Collections.emptyList();
    }
}
