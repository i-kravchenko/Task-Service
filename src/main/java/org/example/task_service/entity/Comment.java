package org.example.task_service.entity;

import jakarta.persistence.*;

@Entity
public class Comment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne
    @JoinColumn(columnDefinition = "task_id")
    private Task task;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;
}
