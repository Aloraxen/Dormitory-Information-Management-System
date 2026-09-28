package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.Room;
import com.cxt.dormitory.entity.StayRecord;
import com.cxt.dormitory.entity.Student;
import com.cxt.dormitory.service.RoomService;
import com.cxt.dormitory.service.StayService;
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

import java.util.List;

/**
 * 住宿安排管理（分配床位、调整、退宿）
 */
@Controller
@RequestMapping("/stay")
public class StayController {

    @Autowired
    private StayService stayService;

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stays", stayService.findAll());
        return "stay/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(value = "roomId", required = false) Long roomId,
                          @RequestParam(value = "bedNo", required = false) String bedNo,
                          Model model) {
        StayRecord stay = new StayRecord();
        stay.setStatus("在住");
        if (roomId != null) {
            com.cxt.dormitory.entity.Room presetRoom = new com.cxt.dormitory.entity.Room();
            presetRoom.setId(roomId);
            stay.setRoom(presetRoom);
        }
        if (bedNo != null && !bedNo.trim().isEmpty()) {
            stay.setBedNo(bedNo);
        }
        model.addAttribute("stay", stay);
        model.addAttribute("students", roomService.listStudents());
        model.addAttribute("rooms", roomService.listAllRooms());
        model.addAttribute("formTitle", "分配床位");
        return "stay/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("stay", stayService.findById(id));
        model.addAttribute("students", roomService.listStudents());
        model.addAttribute("rooms", roomService.listAllRooms());
        model.addAttribute("formTitle", "调整住宿");
        return "stay/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("stay") StayRecord stay, RedirectAttributes redirectAttributes) {
        try {
            if (stay.getId() == null) {
                stayService.assign(stay);
                redirectAttributes.addFlashAttribute("successMsg", "床位分配成功");
            } else {
                stayService.update(stay);
                redirectAttributes.addFlashAttribute("successMsg", "住宿信息已更新");
            }
            return "redirect:/stay";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            if (stay.getId() == null) {
                return "redirect:/stay/new";
            }
            return "redirect:/stay/edit/" + stay.getId();
        }
    }

    @PostMapping("/checkout/{id}")
    public String checkout(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            stayService.checkout(id);
            redirectAttributes.addFlashAttribute("successMsg", "退宿办理完成");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/stay";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        stayService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "记录已删除");
        return "redirect:/stay";
    }
}
