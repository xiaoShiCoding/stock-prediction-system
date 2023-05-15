package com.stock.controller;

import com.stock.annotation.RoleEnum;
import com.stock.annotation.Roles;
import com.stock.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author admin
 * @date 2023/4/11 0011 15:43
 * @description
 */
@RestController
@RequestMapping("/database")
@Api("数据库接口")
public class DatabaseController {
    @Value("${spring.datasource.password}")
    String password;

    private static final String BASE_SQL_FILE = "D:" + System.getProperty("file.separator") + "stock.sql";

    private static final String BACKUP_ERROR = "{\"code\": 500,\"msg\": \"The Database Backup Fail.\"}";

    @Roles(RoleEnum.Admin)
    @ApiOperation("备份数据库")
    @GetMapping("/backup")
    public void backup(HttpServletResponse response) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        String command = "mysqldump -u root -p" + password + " stock -r " + BASE_SQL_FILE;
        try {
            String[] commands = {"cmd", "/c", command};
            Process exec = runtime.exec(commands);
            if (exec.waitFor() == 0) {
                File baseSqlFile = new File(BASE_SQL_FILE);
                byte[] buf = new byte[(int) baseSqlFile.length()];
                int read;
                try (FileInputStream inputStream = new FileInputStream(BASE_SQL_FILE)) {
                    read = inputStream.read(buf);
                }
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=stock.sql");
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(buf, 0, read);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.getOutputStream().print(BACKUP_ERROR);
        }
    }

    @Roles(RoleEnum.Admin)
    @ApiOperation("导入数据库")
    @PostMapping("/restore")
    public Result restore(MultipartFile file) throws Exception {
        File baseSqlFile = new File(BASE_SQL_FILE);
        if (!baseSqlFile.exists()) {
            if (!baseSqlFile.createNewFile()) {
                return Result.error(null, "未知错误，数据库导入失败");
            }
        }
        file.transferTo(baseSqlFile);

        // 等待文件写入完成
        Thread.sleep(500);

        Runtime runtime = Runtime.getRuntime();
        String command = "mysql -u root -p" + password + " stock < " + BASE_SQL_FILE;
        try {
            String[] commands = {"cmd", "/c", command};
            runtime.exec(commands);
            return Result.success(null, "数据库导入成功");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(null, "未知错误，数据库导入失败");
        }
    }
}
