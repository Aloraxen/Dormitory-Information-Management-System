package com.cxt.dormitory.service;

import com.cxt.dormitory.entity.BackupRecord;
import com.cxt.dormitory.repository.BackupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据备份与恢复：通过调用本机 MySQL 的 mysqldump / mysql 命令实现
 */
@Service
public class BackupService {

    @Autowired
    private BackupRepository backupRepository;

    @Autowired
    private Environment environment;

    public List<BackupRecord> findAll() {
        return backupRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public BackupRecord findById(Long id) {
        return id == null ? null : backupRepository.findById(id).orElse(null);
    }

    /** 执行数据库备份（导出 SQL 文件） */
    @Transactional
    public BackupRecord doBackup(String operator) throws IOException, InterruptedException {
        String dir = environment.getProperty("dorm.backup.dir", "backups");
        String mysqldump = environment.getProperty("dorm.backup.mysqldump-path", "mysqldump");
        String jdbcUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username", "root");
        String password = environment.getProperty("spring.datasource.password", "");
        String database = parseDatabase(jdbcUrl);
        String host = parseHost(jdbcUrl);
        String port = parsePort(jdbcUrl);

        Path dirPath = Paths.get(dir);
        Files.createDirectories(dirPath);
        String fileName = "dormitory_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";
        Path outFile = dirPath.resolve(fileName);

        List<String> command = new ArrayList<String>();
        command.add(mysqldump);
        command.add("-h");
        command.add(host);
        command.add("-P");
        command.add(port);
        command.add("-u");
        command.add(username);
        command.add("-p" + password);
        command.add("--default-character-set=utf8mb4");
        command.add("--databases");
        command.add(database);

        int exitCode = runWithFallback(command, outFile.toFile(), null);
        if (exitCode != 0) {
            throw new IllegalStateException("备份失败（退出码 " + exitCode
                    + "）。请检查 application.yml 中 dorm.backup.mysqldump-path 是否正确指向本机 mysqldump，且 MySQL 服务已启动。");
        }

        BackupRecord record = new BackupRecord();
        record.setFileName(fileName);
        record.setFilePath(outFile.toAbsolutePath().toString());
        record.setSize(Files.size(outFile));
        record.setOperator(operator == null ? "admin" : operator);
        return backupRepository.save(record);
    }

    /** 从备份文件恢复数据库 */
    public void restore(Long id) throws IOException, InterruptedException {
        BackupRecord record = backupRepository.findById(id).orElse(null);
        if (record == null || record.getFilePath() == null) {
            throw new IllegalArgumentException("备份记录不存在");
        }
        Path file = Paths.get(record.getFilePath());
        if (!Files.exists(file)) {
            throw new IllegalStateException("备份文件不存在：" + record.getFilePath());
        }
        String mysql = environment.getProperty("dorm.backup.mysql-path", "mysql");
        String jdbcUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username", "root");
        String password = environment.getProperty("spring.datasource.password", "");
        String database = parseDatabase(jdbcUrl);
        String host = parseHost(jdbcUrl);
        String port = parsePort(jdbcUrl);

        List<String> command = new ArrayList<String>();
        command.add(mysql);
        command.add("-h");
        command.add(host);
        command.add("-P");
        command.add(port);
        command.add("-u");
        command.add(username);
        command.add("-p" + password);
        command.add("--default-character-set=utf8mb4");
        command.add(database);

        int exitCode = runWithFallback(command, null, file.toFile());
        if (exitCode != 0) {
            throw new IllegalStateException("恢复失败（退出码 " + exitCode
                    + "）。请检查 application.yml 中 dorm.backup.mysql-path 配置及 MySQL 服务状态。");
        }
    }

    /**
     * 先按原配置执行命令；若失败（常见原因：本机 my.ini 配置损坏导致
     * mysql 客户端无法解析选项），自动加 --no-defaults 参数跳过配置文件重试。
     */
    private int runWithFallback(List<String> command, File outputFile, File inputFile)
            throws IOException, InterruptedException {
        int exitCode = startProcess(command, false, outputFile, inputFile).waitFor();
        if (exitCode == 0) {
            return 0;
        }
        return startProcess(command, true, outputFile, inputFile).waitFor();
    }

    private Process startProcess(List<String> command, boolean skipConfigFile, File outputFile, File inputFile)
            throws IOException {
        List<String> cmd = new ArrayList<String>(command);
        if (skipConfigFile) {
            cmd.add(1, "--no-defaults");
        }
        ProcessBuilder builder = new ProcessBuilder(cmd);
        if (outputFile != null) {
            builder.redirectOutput(outputFile);
        }
        if (inputFile != null) {
            builder.redirectInput(inputFile);
        }
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder.start();
    }

    @Transactional
    public void delete(Long id) {
        backupRepository.deleteById(id);
    }

    private String parseDatabase(String jdbcUrl) {
        String rest = jdbcUrl.substring("jdbc:mysql://".length());
        int slash = rest.indexOf('/');
        if (slash < 0) {
            throw new IllegalArgumentException("数据源URL格式不正确");
        }
        String db = rest.substring(slash + 1);
        int question = db.indexOf('?');
        if (question >= 0) {
            db = db.substring(0, question);
        }
        return db;
    }

    private String parseHost(String jdbcUrl) {
        String rest = jdbcUrl.substring("jdbc:mysql://".length());
        int slash = rest.indexOf('/');
        if (slash < 0) {
            throw new IllegalArgumentException("数据源URL格式不正确");
        }
        String authority = rest.substring(0, slash);
        int colon = authority.indexOf(':');
        return colon >= 0 ? authority.substring(0, colon) : authority;
    }

    private String parsePort(String jdbcUrl) {
        String rest = jdbcUrl.substring("jdbc:mysql://".length());
        int slash = rest.indexOf('/');
        if (slash < 0) {
            return "3306";
        }
        String authority = rest.substring(0, slash);
        int colon = authority.indexOf(':');
        if (colon < 0) {
            return "3306";
        }
        return authority.substring(colon + 1);
    }
}
