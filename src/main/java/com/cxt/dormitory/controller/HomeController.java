package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.ApplyRequest;
import com.cxt.dormitory.entity.StayRecord;
import com.cxt.dormitory.repository.BuildingRepository;
import com.cxt.dormitory.repository.RoomRepository;
import com.cxt.dormitory.repository.StudentRepository;
import com.cxt.dormitory.service.ApplyService;
import com.cxt.dormitory.service.StayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

/**
 * 首页仪表盘
 */
@Controller
public class HomeController {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StayService stayService;

    @Autowired
    private ApplyService applyService;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        model.addAttribute("nickname", principal != null ? principal.getName() : "");
        model.addAttribute("buildingCount", buildingRepository.count());
        model.addAttribute("roomCount", roomRepository.count());
        model.addAttribute("studentCount", studentRepository.count());
        model.addAttribute("activeStayCount", stayService.countActive());
        model.addAttribute("pendingApplyCount", applyService.countPending());

        List<ApplyRequest> applies = applyService.findAll();
        if (applies.size() > 5) {
            applies = applies.subList(0, 5);
        }
        model.addAttribute("recentApplies", applies);

        List<StayRecord> stays = stayService.findAll();
        if (stays.size() > 5) {
            stays = stays.subList(0, 5);
        }
        model.addAttribute("recentStays", stays);
        return "index";
    }
}
