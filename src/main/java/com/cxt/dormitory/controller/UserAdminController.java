package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.User;
import com.cxt.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 系统管理：用户与权限管理（仅管理员）
 */
@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("formTitle", "新增用户");
        return "admin/user-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("formTitle", "编辑用户");
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("登录账号不能为空");
            }
            user.setUsername(user.getUsername().trim());
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMsg", "保存成功");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            if (user.getId() == null) {
                return "redirect:/admin/users/new";
            }
            return "redirect:/admin/users/edit/" + user.getId();
        }
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.toggleEnabled(id);
        redirectAttributes.addFlashAttribute("successMsg", "状态已更新");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "删除成功");
        return "redirect:/admin/users";
    }
}
