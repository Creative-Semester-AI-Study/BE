package com.sejong.aistudyassistant.quiz.Entity;

import com.sejong.aistudyassistant.quiz.Entity.Quiz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private String optionText;
}
