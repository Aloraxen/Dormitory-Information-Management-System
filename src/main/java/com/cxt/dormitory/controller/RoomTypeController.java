package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.RoomType;
import com.cxt.dormitory.service.RoomService;
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
 * 宿舍房型管理
 */
@Controller
@RequestMapping("/dorm/room-types")
public class RoomTypeController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("roomTypes", roomService.findAllRoomTypes());
        return "dorm/room-types";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("roomType", new RoomType());
        model.addAttribute("formTitle", "新增房型");
        return "dorm/room-type-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("roomType", roomService.findRoomTypeById(id));
        model.addAttribute("formTitle", "编辑房型");
        return "dorm/room-type-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("roomType") RoomType roomType, RedirectAttributes redirectAttributes) {
        try {
            roomService.saveRoomType(roomType);
            redirectAttributes.addFlashAttribute("successMsg", "保存成功");
            return "redirect:/dorm/room-types";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            if (roomType.getId() == null) {
                return "redirect:/dorm/room-types/new";
            }
            return "redirect:/dorm/room-types/edit/" + roomType.getId();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoomType(id);
            redirectAttributes.addFlashAttribute("successMsg", "删除成功");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/dorm/room-types";
    }
}
