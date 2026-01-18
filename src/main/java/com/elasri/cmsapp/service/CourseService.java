package com.elasri.cmsapp.service;



import com.elasri.cmsapp.model.Course;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {
    private final String xmlPath = "src/main/resources/data/courses.xml";

    private XmlService xmlService;
    private XPathService xpathService;
    private XsltService xsltService;

    public CourseService(XmlService xmlService,  XPathService xpathService, XsltService xsltService) {
        this.xmlService = xmlService;
        this.xpathService = xpathService;
        this.xsltService = xsltService;
    }

    // Lire tous les cours
    public List<Course> getAllCourses() throws Exception {
        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//course");

        List<Course> courses = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            courses.add(mapToCourse(e));
        }
        return courses;
    }

    public Course getCourseById(int id) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//course[@id='" + id + "']");

        if (nodes.getLength() == 0) return null;

        return mapToCourse((Element) nodes.item(0));
    }

    // ======================
    // CREATE
    // ======================

    private int generateId(Document doc) throws Exception {
        NodeList nodes = xpathService.evaluate(doc, "//course");
        return nodes.getLength() + 1;
    }

    public void addCourse(Course course) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        Element root = doc.getDocumentElement();

        int newId = generateId(doc);
        course.setId(newId);


        Element courseEl = doc.createElement("course");

        Element id = doc.createElement("id");
        id.setTextContent(String.valueOf(course.getId()));

        Element title = doc.createElement("title");
        title.setTextContent(course.getTitle());

        Element description = doc.createElement("description");
        description.setTextContent(course.getDescription());

        Element author = doc.createElement("author");
        author.setTextContent(course.getAuthor());

        Element category = doc.createElement("category");
        category.setTextContent(course.getCategory());

// ⚠️ ORDER IS IMPORTANT
        courseEl.appendChild(id);
        courseEl.appendChild(title);
        courseEl.appendChild(description);
        courseEl.appendChild(author);
        courseEl.appendChild(category);

        root.appendChild(courseEl);
        xmlService.saveXml(doc, xmlPath);
        xmlService.validateXml(xmlPath, "src/main/resources/schemas/courses.xsd");
    }



    // ======================
    // UPDATE
    // ======================
    public boolean updateCourse(Course course) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//course[@id='" + course.getId() + "']");

        if (nodes.getLength() == 0) return false;

        Element e = (Element) nodes.item(0);
        e.setAttribute("category", course.getCategory());
        e.getElementsByTagName("title").item(0)
                .setTextContent(course.getTitle());
        e.getElementsByTagName("author").item(0)
                .setTextContent(course.getAuthor());
        e.getElementsByTagName("description").item(0)
                .setTextContent(course.getDescription());

        xmlService.saveXml(doc, xmlPath);
        xmlService.validateXml(xmlPath, "src/main/resources/schemas/courses.xsd");
        return true;
    }

    // ======================
    // DELETE
    // ======================
    public boolean deleteCourse(int id) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);
        NodeList nodes = xpathService.evaluate(doc, "//course[@id='" + id + "']");

        if (nodes.getLength() == 0) return false;

        Node node = nodes.item(0);
        node.getParentNode().removeChild(node);

        xmlService.saveXml(doc, xmlPath);
        xmlService.validateXml(xmlPath, "src/main/resources/schemas/courses.xsd");
        return true;
    }

    // ======================
    // HTML GENERATION
    // ======================
    public String generateCoursesHtml() throws Exception {
        return xsltService.transform(
                xmlPath,
                "src/main/resources/xslt/courses.xsl"
        );
    }

    public String generateCourseHtml(int id) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("courseId", String.valueOf(id));

        return xsltService.transformWithParams(
                xmlPath,
                "src/main/resources/xslt/course-detail.xsl",
                params
        );
    }

    // ======================
    // UTILS
    // ======================
    private Course mapToCourse(Element element) {
        Course course = new Course();

        String idText = getTagValue("id", element);

        if (idText == null || idText.trim().isEmpty()) {
            throw new RuntimeException("Course ID is missing in XML");
        }

        course.setId(Integer.parseInt(idText.trim()));
        course.setTitle(getTagValue("title", element));
        course.setDescription(getTagValue("description", element));
        course.setAuthor(getTagValue("author", element));
        course.setCategory(getTagValue("category", element));

        return course;
    }




    private void append(Document doc, Element parent,
                        String tag, String value) {
        Element el = doc.createElement(tag);
        el.setTextContent(value);
        parent.appendChild(el);
    }

    public List<Course> search(String keyword) throws Exception {

        Document doc = xmlService.loadXml(xmlPath);

        String query =
                "//course[contains(title,'" + keyword + "') or contains(description,'" + keyword + "')]";

        NodeList nodes = xpathService.evaluate(doc, query);

        List<Course> results = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            results.add(mapToCourse((Element) nodes.item(i)));
        }
        return results;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) {
            return null;
        }
        return nodeList.item(0).getTextContent();
    }


    public List<Map<String, String>> getAllProfessors() throws Exception {
        Document doc = xmlService.loadXml("src/main/resources/data/professors.xml");
        NodeList nodes = xpathService.evaluate(doc, "//professor");

        List<Map<String, String>> professors = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            Map<String, String> prof = new HashMap<>();
            prof.put("id", e.getAttribute("id"));
            prof.put("name", getTagValue("name", e));
            professors.add(prof);
        }
        return professors;
    }

    public Map<String, String> getProfessorById(String id) throws Exception {
        Document doc = xmlService.loadXml("src/main/resources/data/professors.xml");
        NodeList nodes = xpathService.evaluate(doc, "//professor[@id='" + id + "']");

        if (nodes.getLength() == 0) return null;

        Element e = (Element) nodes.item(0);
        Map<String, String> prof = new HashMap<>();
        prof.put("id", e.getAttribute("id"));
        prof.put("name", getTagValue("name", e));
        return prof;
    }

}
