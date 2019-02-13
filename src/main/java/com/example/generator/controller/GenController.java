package com.example.generator.controller;

import com.example.generator.util.DownUtil;
import com.example.generator.util.FileUtil;
import com.example.generator.util.LogUtil;
import com.example.generator.util.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: zyw
 * @Date: 2019/2/12
 */
@Controller
@RequestMapping({"/api/gen"})
public class GenController {
    private static final Logger log = LoggerFactory.getLogger(GenController.class);
    private static Map<String, String> runList = new ConcurrentHashMap();
    private static final ExecutorService executorService;

    public GenController() {
    }

    @GetMapping({"/gener"})
    @ResponseBody
    public void gen(@RequestParam String bizLine, @RequestParam String artifactId) {
        if (!StringUtils.isBlank(bizLine) && !StringUtils.isBlank(artifactId)) {
            String appName = artifactId.toLowerCase();
            String fileName = this.genFileName(bizLine, appName);
            if (runList.containsKey(fileName)) {
                log.warn("APP：" + fileName + "正在生成中，请稍后再试!");
            } else {
                runList.put(fileName, "RUN");
            }

            executorService.execute(() -> {
                try {
                    if (!DownUtil.exist("/usr/local/template-gen/template/", fileName)) {
                        LogUtil.push(this.getLogTag(bizLine, artifactId), "正在复制和调整模板......");
                        log.info("正在复制和调整模板......");
                        DownUtil.copyAndRefactor("/usr/local/template-gen/template/MMCMAMS/", appName);
                        log.info("模板复制成功......");
                        LogUtil.push(this.getLogTag(bizLine, artifactId), "模板复制成功......");
                        LogUtil.push(this.getLogTag(bizLine, artifactId), "模板调整成功......");
                        LogUtil.push(this.getLogTag(bizLine, artifactId), "正在压缩模板......");
                        log.info("正在压缩模板......");
                        ZipUtil.zip("/usr/local/template-gen/template/".concat(appName), "/usr/local/template-gen/template/".concat(fileName));
                        log.info("模板压缩成功......");
                        log.info("删除临时文件夹......");
                        FileUtil.deleteFile(new File("/usr/local/template-gen/template/".concat(appName)));
                        log.info("删除临时文件夹成功......");
                        LogUtil.push(this.getLogTag(bizLine, artifactId), "模板压缩成功......");
                        LogUtil.push(this.getLogTag(bizLine, artifactId), "APP: [" + fileName + "]模板完成");
                    } else {
                        LogUtil.push(this.getLogTag(bizLine, artifactId), "APP：[" + fileName + "]已经生成，无需再次生成");
                        log.info("APP：" + fileName + "已经生成，无需再次生成");
                    }

                    LogUtil.push(this.getLogTag(bizLine, artifactId), "SUCCESS");
                } catch (Throwable var9) {
                    LogUtil.push(this.getLogTag(bizLine, artifactId), "复制文件异常：".concat(var9.getMessage()));
                    log.error("文件生成失败：", var9);
                } finally {
                    runList.remove(fileName);
                    log.info("APP: " + fileName + "基础模板生成完成");
                }

            });
        } else {
            LogUtil.push(this.getLogTag(bizLine, artifactId), "业务线和应用名称不可为空");
            throw new IllegalArgumentException("业务线和应用名称不可为空");
        }
    }

    @GetMapping({"/logList"})
    @ResponseBody
    public Map<String, List<String>> getLog(@RequestParam String bizLine, @RequestParam String artifactId) {
        List<String> logList = LogUtil.pop(this.getLogTag(bizLine, artifactId));
        Map<String, List<String>> result = new HashMap();
        result.put("logList", logList);
        return result;
    }

    @GetMapping({"/down"})
    public void down(@RequestParam String bizLine, @RequestParam String artifactId, HttpServletResponse response) {
        if (!StringUtils.isBlank(bizLine) && !StringUtils.isBlank(artifactId)) {
            artifactId = artifactId.toLowerCase();
            String fileName = this.genFileName(bizLine, artifactId);
            if (DownUtil.exist("/usr/local/template-gen/template/", fileName)) {
                DownUtil.down(response, "/usr/local/template-gen/template/", fileName);
            } else {
                throw new IllegalStateException("下载文件异常，未找到对应文件");
            }
        } else {
            throw new IllegalArgumentException("业务线和应用名称不可为空");
        }
    }

    @GetMapping({"/get"})
    @ResponseBody
    public Map<String, String> get(@RequestParam String bizLine, @RequestParam String artifactId) {
        Map<String, String> result = new HashMap();
        artifactId = artifactId.toLowerCase();
        String fileName = this.genFileName(bizLine, artifactId);
        String filePath = "/usr/local/template-gen/template/".concat(fileName);
        if (DownUtil.exist("/usr/local/template-gen/template/", fileName)) {
            result.put("name", fileName);
            result.put("size", (new File(filePath)).length() + "");
        }

        return result;
    }

    private String genFileName(String bizLine, String artifactId) {
        return bizLine.concat(".").concat(artifactId).concat(".zip");
    }

    private String getLogTag(String bizLine, String artifactId) {
        return bizLine.concat("-").concat(artifactId);
    }

    static {
        executorService = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }
}
