package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.Building;
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
 * 宿舍楼栋管理
 */
@Controller
@RequestMapping("/dorm/buildings")
public class BuildingController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("buildings", roomService.findAllBuildings());
        return "dorm/buildings";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("building", new Building());
        model.addAttribute("formTitle", "新增楼栋");
        return "dorm/building-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("building", roomService.findBuildingById(id));
        model.addAttribute("formTitle", "编辑楼栋");
        return "dorm/building-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("building") Building building, RedirectAttributes redirectAttributes) {
        try {
            roomService.saveBuilding(building);
            redirectAttributes.addFlashAttribute("successMsg", "保存成功");
            return "redirect:/dorm/buildings";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            if (building.getId() == null) {
                return "redirect:/dorm/buildings/new";
            }
            return "redirect:/dorm/buildings/edit/" + building.getId();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteBuilding(id);
            redirectAttributes.addFlashAttribute("successMsg", "删除成功");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/dorm/buildings";
    }
}
