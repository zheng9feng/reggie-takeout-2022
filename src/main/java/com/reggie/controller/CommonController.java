package com.reggie.controller;

import com.reggie.common.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author m0v1
 * @date 2022年10月13日 00:32
 */

@Slf4j
@RequestMapping("/common")
@RestController
public class CommonController {

    @Value("${reggie.file.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file 待保存的文件 注:file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件删除
     * @return
     */
    @PostMapping("/upload")
    public ResponseInfo<String> upload(MultipartFile file) {
        //获取原始的文件名
        String originalFilename = file.getOriginalFilename();
        //获取上传的文件后缀,如.png
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用uuid重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID() + suffix;

        try {
            //创建一个目录对象
            File dir = new File(basePath);
            //判断目录是否存在
            if (!dir.exists()) {
                //目录不存在需要创建
                dir.mkdir();
            }
            //将临时文件存储到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            log.error("文件上传异常!", e);
        }
        return ResponseInfo.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name     文件名
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath + name);

            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //代表图片文件
            response.setContentType("image/jpeg");

            // int len = 0;
            byte[] buffer = new byte[1024];
            // while ((len = fileInputStream.read(bytes)) != -1) {
            //     //向response缓冲区中写入字节，再由Tomcat服务器将字节内容组成Http响应返回给浏览器。
            //     outputStream.write(bytes, 0, len);
            //     //所储存的数据全部清空
            //     outputStream.flush();
            // }

            while (true) {
                int bufferLength = fileInputStream.read(buffer);
                if (bufferLength == -1) {
                    break;
                }

                outputStream.write(buffer, 0, bufferLength);
                outputStream.flush();
            }

            //关闭流
            fileInputStream.close();
            outputStream.close();

        } catch (IOException e) {
            log.error("文件下载异常!", e);
        }
    }
}
