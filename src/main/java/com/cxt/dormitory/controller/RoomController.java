package com.cxt.dormitory.controller;

import com.cxt.dormitory.dto.BedInfo;
import com.cxt.dormitory.entity.Room;
import com.cxt.dormitory.service.RoomService;
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
 * 宿舍房间管理（含床位可视化）
 */
@Controller
@RequestMapping("/dorm/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String list(@RequestParam(value = "buildingId", required = false) Long buildingId, Model model) {
        List<com.cxt.dormitory.entity.Building> buildings = roomService.findAllBuildings();
        model.addAttribute("buildings", buildings);
        if (buildingId == null && !buildings.isEmpty()) {
            buildingId = buildings.get(0).getId();
        }
        model.addAttribute("currentBuildingId", buildingId);
        model.addAttribute("roomVOs", roomService.findRoomVOsByBuilding(buildingId));
        return "dorm/rooms";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Room room = roomService.findRoomById(id);
        if (room == null) {
            return "redirect:/dorm/rooms";
        }
        List<BedInfo> beds = roomService.buildBedInfos(id);
        model.addAttribute("room", room);
        model.addAttribute("beds", beds);
        return "dorm/room-detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("buildings", roomService.findAllBuildings());
        model.addAttribute("roomTypes", roomService.findAllRoomTypes());
        model.addAttribute("formTitle", "新增房间");
        return "dorm/room-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("room", roomService.findRoomById(id));
        model.addAttribute("buildings", roomService.findAllBuildings());
        model.addAttribute("roomTypes", roomService.findAllRoomTypes());
        model.addAttribute("formTitle", "编辑房间");
        return "dorm/room-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("room") Room room, RedirectAttributes redirectAttributes) {
        try {
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("successMsg", "保存成功");
            return "redirect:/dorm/rooms?buildingId=" + room.getBuilding().getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            if (room.getId() == null) {
                return "redirect:/dorm/rooms/new";
            }
            return "redirect:/dorm/rooms/edit/" + room.getId();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Room room = roomService.findRoomById(id);
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("successMsg", "删除成功");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        if (room != null && room.getBuilding() != null) {
            return "redirect:/dorm/rooms?buildingId=" + room.getBuilding().getId();
        }
        return "redirect:/dorm/rooms";
    }
}
