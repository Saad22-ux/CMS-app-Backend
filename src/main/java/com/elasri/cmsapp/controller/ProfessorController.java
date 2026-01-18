package com.elasri.cmsapp.controller;

import com.elasri.cmsapp.model.Professor;
import com.elasri.cmsapp.service.CourseService;
import com.elasri.cmsapp.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/professor")

public class ProfessorController {
    private ProfessorService professorService;
    private CourseService courseService;

    public ProfessorController(ProfessorService professorService, CourseService courseService) {
        this.professorService = professorService;
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    public Map<String, String> getProfessor(@PathVariable String id) throws Exception {
        return courseService.getProfessorById(id);
    }

    @PutMapping
    public void updateProfessor(@RequestBody Professor professor)
            throws Exception {
        professorService.updateProfessor(professor);
    }

    @GetMapping("/html")
    public String html() throws Exception {
        return professorService.generateHtml();
    }

    @GetMapping
    public List<Map<String, String>> getAllProfessors() throws Exception {
        return courseService.getAllProfessors();
    }
}
