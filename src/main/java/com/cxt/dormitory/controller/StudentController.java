package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.Student;
import com.cxt.dormitory.entity.StayRecord;
import com.cxt.dormitory.entity.User;
import com.cxt.dormitory.repository.UserRepository;
import com.cxt.dormitory.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/**
 * 学生信息管理（宿管/管理员）
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String list(@ModelAttribute("keyword") String keyword, Model model) {
        List<Student> students = studentService.search(keyword);
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        return "students/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("studentUsers", userRepository.findByRoleOrderByIdAsc("ROLE_STUDENT"));
        model.addAttribute("formTitle", "新增学生");
        return "students/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        model.addAttribute("student", studentService.findById(id));
        model.addAttribute("studentUsers", userRepository.findByRoleOrderByIdAsc("ROLE_STUDENT"));
        model.addAttribute("formTitle", "编辑学生");
        return "students/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("student") Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.save(student);
            redirectAttributes.addFlashAttribute("successMsg", "保存成功");
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            if (student.getId() == null) {
                return "redirect:/students/new";
            }
            return "redirect:/students/edit/" + student.getId();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes) {
        studentService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "删除成功");
        return "redirect:/students";
    }

    /**
     * 学生查看本人信息（学生角色）
     */
    @GetMapping("/my")
    public String myInfo(Model model, Principal principal) {
        User current = userRepository.findByUsername(principal.getName());
        Student student = studentService.findByUserId(current.getId());
        if (student == null) {
            // 账户未关联学生档案时，给出提示
            model.addAttribute("noStudentInfo", true);
            return "students/my";
        }
        model.addAttribute("student", student);
        model.addAttribute("noStudentInfo", false);
        StayRecord currentStay = null;
        List<StayRecord> stays = studentService.findStaysOfStudent(student.getId());
        for (StayRecord stay : stays) {
            if ("在住".equals(stay.getStatus())) {
                currentStay = stay;
                break;
            }
        }
        model.addAttribute("currentStay", currentStay);
        model.addAttribute("stays", stays);
        return "students/my";
    }

    /**
     * 学生自助更新联系方式（PPT 中"学生可随时更新个人信息"）
     */
    @PostMapping("/my/save")
    public String updateMy(@ModelAttribute("student") Student student,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        User current = userRepository.findByUsername(principal.getName());
        Student existing = studentService.findByUserId(current.getId());
        if (existing == null || !existing.getId().equals(student.getId())) {
            redirectAttributes.addFlashAttribute("errorMsg", "无权修改他人信息");
            return "redirect:/students/my";
        }
        existing.setPhone(student.getPhone());
        existing.setEmail(student.getEmail());
        studentService.save(existing);
        redirectAttributes.addFlashAttribute("successMsg", "个人信息已更新");
        return "redirect:/students/my";
    }
}
