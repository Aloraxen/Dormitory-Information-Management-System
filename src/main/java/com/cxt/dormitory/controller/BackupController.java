package com.cxt.dormitory.controller;

import com.cxt.dormitory.entity.BackupRecord;
import com.cxt.dormitory.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

/**
 * 系统管理：数据备份与恢复（仅管理员）
 */
@Controller
@RequestMapping("/admin/backups")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("backups", backupService.findAll());
        return "admin/backups";
    }

    /** 执行备份 */
    @PostMapping("/do")
    public String doBackup(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            BackupRecord record = backupService.doBackup(principal != null ? principal.getName() : "admin");
            redirectAttributes.addFlashAttribute("successMsg",
                    "备份成功：" + record.getFileName() + "（" + formatSize(record.getSize()) + "）");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/backups";
    }

    /** 下载备份文件 */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        BackupRecord record = backupService.findById(id);
        if (record == null || record.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(record.getFilePath());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String fileName = record.getFileName() == null ? "backup.sql" : record.getFileName();
        String encodedName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /** 从备份恢复 */
    @PostMapping("/restore/{id}")
    public String restore(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            backupService.restore(id);
            redirectAttributes.addFlashAttribute("successMsg", "数据库恢复完成");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/backups";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        backupService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "记录已删除");
        return "redirect:/admin/backups";
    }

    private String formatSize(Long size) {
        if (size == null || size <= 0) {
            return "0 B";
        }
        if (size < 1024) {
            return size + " B";
        }
        if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        }
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}
