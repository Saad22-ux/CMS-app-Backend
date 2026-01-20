package com.elasri.cmsapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class Course {
    private int id;
    private String title;
    private String category;
    private String description;

    private int authorId;

}
