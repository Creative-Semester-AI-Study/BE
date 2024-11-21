package com.sejong.aistudyassistant.timetable;

import com.sejong.aistudyassistant.subject.Subject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/study/TimeTable")
public class TimeTableController {

    private final TimeTableService timeTableService;

    public TimeTableController(TimeTableService timeTableService) {
        this.timeTableService = timeTableService;
    }

    @GetMapping("/{day}")
    public ResponseEntity<List<Subject>> getSubjectsByDay(@PathVariable String day) {
        List<Subject> subjects = timeTableService.checkCertainDayTimeTable(day);

        if (subjects == null || subjects.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subjects);
    }
}
