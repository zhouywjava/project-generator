package com.example.generator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description:
 * @Author: zyw
 * @Date: 2019/2/12
 */
public abstract class DownUtil {
    private static final Logger log = LoggerFactory.getLogger(DownUtil.class);

    public static void down(HttpServletResponse response, String filePath, String fileName)
    {
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte['?'];
        BufferedInputStream bis = null;
        try
        {
            OutputStream os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(new File(filePath.concat(fileName))));
            int i = bis.read(buff);
            while (i != -1)
            {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bis != null) {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyAndRefactor(String oldPath, String artifactId)
            throws IOException
    {
        File source = new File(oldPath);

        String newPath = oldPath.replaceAll("mmcmams", artifactId).replaceAll("mmcmams".toUpperCase(), artifactId);

        File newFile = new File(newPath);
        if (source.isDirectory())
        {
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
            File[] subFileList = source.listFiles();
            if ((null != subFileList) && (subFileList.length > 0)) {
                for (File oldSubFile : subFileList) {
                    copyAndRefactor(oldSubFile.getAbsolutePath(), artifactId);
                }
            }
        }
        else if (needEdit(oldPath).booleanValue())
        {
            String content = readFile(source);
            if (StringUtils.isNotBlank(content)) {
                content = content.replaceAll("mmcmams", artifactId);
            } else {
                content = "";
            }
            write2File(newPath, content);
        }
        else
        {
            copyFile(oldPath, newPath);
        }
    }

    private static void copyFile(String oldPath, String newPath)
            throws IOException
    {
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = new FileInputStream(oldFile);
        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte['?'];
        int readByte;
        while ((readByte = in.read(buffer)) != -1) {
            out.write(buffer, 0, readByte);
        }
        in.close();
        out.close();
    }

    private static String readFile(File file)
    {
        String encoding = "UTF-8";
        Long fileLength = Long.valueOf(file.length());
        byte[] fileContent = new byte[fileLength.intValue()];
        try
        {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            return new String(fileContent, encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("文件读取失败", e);
        }
        return null;
    }

    private static void write2File(String filePath, String content)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(content.getBytes());
            fos.close();
        }
        catch (Exception e)
        {
            log.error("文件写入出错", e);
        }
    }

    private static Boolean needEdit(String path)
    {
        return Boolean.valueOf((path.endsWith(".xml")) || (path.endsWith("properties")) || (path.endsWith(".java")));
    }

    public static Boolean exist(String path, String fileName)
    {
        return Boolean.valueOf(new File(path.concat(fileName)).exists());
    }
}
