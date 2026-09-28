package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.ApplyRequest;
import com.cxt.dormitory.entity.Student;
import com.cxt.dormitory.entity.User;
import com.cxt.dormitory.repository.UserRepository;
import com.cxt.dormitory.service.ApplyService;
import com.cxt.dormitory.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * 学生申请管理：学生提交申请，宿管/管理员处理
 */
@Controller
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserRepository userRepository;

    /** 宿管/管理员：全部申请 */
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("applies", applyService.findAll());
        return "apply/list";
    }

    /** 学生：我的申请 */
    @GetMapping("/my")
    public String my(Principal principal, Model model) {
        User current = userRepository.findByUsername(principal.getName());
        Student student = studentService.findByUserId(current.getId());
        if (student == null) {
            model.addAttribute("noStudentInfo", true);
            return "apply/my";
        }
        model.addAttribute("applies", applyService.findByStudentId(student.getId()));
        model.addAttribute("student", student);
        model.addAttribute("noStudentInfo", false);
        return "apply/my";
    }

    /** 学生：提交申请 */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("apply", new ApplyRequest());
        model.addAttribute("applyTypes", java.util.Arrays.asList(
                ApplyRequest.TYPE_ROOM_TYPE, ApplyRequest.TYPE_TRANSFER, ApplyRequest.TYPE_REPAIR));
        return "apply/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("apply") ApplyRequest apply, Principal principal,
                       RedirectAttributes redirectAttributes) {
        User current = userRepository.findByUsername(principal.getName());
        Student student = studentService.findByUserId(current.getId());
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "当前账户未关联学生档案，无法提交申请");
            return "redirect:/apply/my";
        }
        ApplyRequest form = new ApplyRequest();
        Student ref = new Student();
        ref.setId(student.getId());
        form.setStudent(ref);
        form.setType(apply.getType());
        form.setTitle(apply.getTitle());
        form.setContent(apply.getContent());
        try {
            applyService.submit(form);
            redirectAttributes.addFlashAttribute("successMsg", "申请已提交，等待宿管处理");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/apply/new";
        }
        return "redirect:/apply/my";
    }

    /** 宿管/管理员：处理申请 */
    @PostMapping("/handle/{id}")
    public String handle(@PathVariable("id") Long id,
                         @RequestParam("status") String status,
                         @RequestParam(value = "reply", required = false) String reply,
                         RedirectAttributes redirectAttributes) {
        applyService.handle(id, status, reply);
        redirectAttributes.addFlashAttribute("successMsg", "处理完成");
        return "redirect:/apply/list";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        applyService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "记录已删除");
        return "redirect:/apply/list";
    }
}
