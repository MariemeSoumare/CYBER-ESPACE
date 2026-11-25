package com.cyberescape.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`level`")
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "level_number", nullable = false, unique = true)
    private Integer sequenceOrder;

    protected Level() {
    }

    public Level(String name, String description, Integer sequenceOrder) {
        this.name = name;
        this.description = description;
        this.sequenceOrder = sequenceOrder;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }
}
