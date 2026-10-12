package com.governai.user;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "role")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
