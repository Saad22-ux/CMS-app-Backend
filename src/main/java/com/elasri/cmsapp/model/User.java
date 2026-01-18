package com.elasri.cmsapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    private int id;
    private String role;
    private String name;
    private String email;
    private List<Integer> courseIds;
}
