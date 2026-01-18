package com.elasri.cmsapp.controller;

import com.elasri.cmsapp.model.Course;
import com.elasri.cmsapp.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/courses")
public class CourseController {

    private CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    @GetMapping
    public List<Course> getCourses() throws Exception {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course one(@PathVariable int id) throws Exception {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public void addCourse(@RequestBody Course course) throws Exception {
        courseService.addCourse(course);
    }

    @DeleteMapping("/{id}")
    public boolean deleteCourse(@PathVariable int id) throws Exception {
        return courseService.deleteCourse(id);
    }

    @PutMapping("/{id}")
    public boolean updateCourse(@PathVariable int id, @RequestBody Course course) throws Exception {
        course.setId(id);
        return courseService.updateCourse(course);
    }

    @GetMapping("/html")
    public String html() throws Exception {
        return courseService.generateCoursesHtml();
    }

    @GetMapping("/html/{id}")
    public String htmlOne(@PathVariable int id) throws Exception {
        return courseService.generateCourseHtml(id);
    }

    @GetMapping("/search")
    public List<Course> search(@RequestParam String q)
            throws Exception {
        return courseService.search(q);
    }


}
