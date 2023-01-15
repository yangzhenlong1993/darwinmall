package com.zhenlong.darwinmall.product;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhenlong.darwinmall.product.entity.BrandEntity;
import com.zhenlong.darwinmall.product.service.BrandService;
import com.zhenlong.darwinmall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
//
//    @Autowired(required = false)
//    OSSClient ossClient;

    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(186L);
        log.info("完整路径: {}", Arrays.asList(catelogPath));
    }

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setDescript("This is phone brand");
//        brandEntity.setName("Iphone");
//        brandService.save(brandEntity);
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("fixed brand");
//        brandService.updateById(brandEntity);

        List<BrandEntity> brandId = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        brandId.forEach(System.out::println);
        System.out.println("update successfully");
    }

//    @Test
//    public void testUpload() {
//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
////        String endpoint = "oss-ap-southeast-2.aliyuncs.com";
////        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
////        String accessKeyId = "LTAI5tEgtLEi9vmw3J3mkbfr";
////        String accessKeySecret = "6OdSxceQx9xhxX2PAB8L23oyf9INHi";
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "darwin-mall";
//        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//        String objectName = "example.png";
//        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
//        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        String filePath = "C:\\Users\\yangz\\Desktop\\example.png";
//
//        // 创建OSSClient实例。
//       //OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        try {
//            InputStream inputStream = new FileInputStream(filePath);
//            // 创建PutObjectRequest对象。
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
//            // 设置该属性可以返回response。如果不设置，则返回的response为空。
//            putObjectRequest.setProcess("true");
//            // 创建PutObject请求。
//            PutObjectResult result = ossClient.putObject(putObjectRequest);
//            // 如果上传成功，则返回200。
//            System.out.println(result.getResponse().getStatusCode());
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }

    @Test
    public void testStringRedisTemplate(){
        //k:hello v:word
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set("hello","world"+ UUID.randomUUID().toString());
        String hello = opsForValue.get("hello");
        System.out.println("之前保存的数据是"+hello);
    }

}
