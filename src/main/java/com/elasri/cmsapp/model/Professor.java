package com.elasri.cmsapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Professor {
    private int id;
    private String name;
    private String bio;
    private List<String> skills;
    private Map<String, String> publications;
}
