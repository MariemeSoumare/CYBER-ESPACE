package com.cyberescape.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "question_text", nullable = false, columnDefinition = "text")
    private String prompt;

    protected Question() {
    }

    public Question(Level level, String prompt) {
        this.level = level;
        this.prompt = prompt;
    }

    public Long getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public String getPrompt() {
        return prompt;
    }

    public int getPoints() {
        return 10;
    }
}
