package com.changgou.file.controller;

import com.changgou.file.util.FastDFSClient;
import com.changgou.file.util.FastDFSFile;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * docker run -d --network=host --name tracker  delron/fastdfs tracker
     * docker run -d --network=host --name storage -e TRACKER_SERVER=10.33.72.96:22122  -e GROUP_NAME=group1 delron/fastdfs storage
     * docker exec -it storage bash
     * vi /etc/fdfs/storage.conf 其中 http.server_port=8888
     * vi /usr/local/nginx/conf/nginx.conf
     * server {
     *         listen       8888;
     *         server_name  localhost;
     *         location ~/group[0-9]/ {
     *             add_header Cache-Control no-store;  # 删除图片自动清除缓存
     *             ngx_fastdfs_module;
     *         }
     *         error_page   500 502 503 504  /50x.html;
     *         location = /50x.html {
     *             root html;
     *         }
     *     }
     * docker update --restart always storage
     * docker update --restart always tracker
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file){
        try{
            //判断文件是否存在
            if (file == null){
                throw new RuntimeException("文件不存在");
            }
            //获取文件的完整名称
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)){
                throw new RuntimeException("文件不存在");
            }

            //获取文件的扩展名称  abc.jpg   jpg
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            //获取文件内容
            byte[] content = file.getBytes();

            //创建文件上传的封装实体类
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename,content,extName);

            //基于工具类进行文件上传,并接受返回参数  String[]
            String[] uploadResult = FastDFSClient.upload(fastDFSFile);

            //封装返回结果
            String url = FastDFSClient.getTrackerUrl()+uploadResult[0]+"/"+uploadResult[1];
            return new Result(true, StatusCode.OK,"文件上传成功",url);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Result(false, StatusCode.ERROR,"文件上传失败");
    }
}
