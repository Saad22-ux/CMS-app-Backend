package com.elasri.cmsapp.controller;

import com.elasri.cmsapp.model.Professor;
import com.elasri.cmsapp.service.ProfessorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professors")
@CrossOrigin(origins = "http://localhost:3000")

public class ProfessorController {
    private ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public List<Professor> all() throws Exception {
        return professorService.getAll();
    }

    @GetMapping("/{id}")
    public Professor one(@PathVariable int id) throws Exception {
        return professorService.getById(id);
    }

    @PostMapping
    public void create(@RequestBody Professor p) throws Exception {
        professorService.add(p);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable int id,
                          @RequestBody Professor p)
            throws Exception {
        p.setId(id);
        return professorService.update(p);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) throws Exception {
        return professorService.delete(id);
    }

    @GetMapping("/html")
    public String html() throws Exception {
        return professorService.generateHtml();
    }
}
