package com.autodesk.learnforge.web;

import com.autodesk.client.ApiResponse;
import com.autodesk.client.api.BucketsApi;
import com.autodesk.client.api.ObjectsApi;
import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.client.model.*;
import com.autodesk.learnforge.service.OAuthService;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * OSS相关服务
 *
 * @author Lenovo
 */
@RestController
public class OssController
{
    @Autowired
    private OAuthService oAuthService;

    /**
     * Bucket列表
     *
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/api/forge/oss/buckets")
    public List<Object> getBuckets(String id) throws Exception
    {
        List<Object> list = new ArrayList<>();

        // get oAuth of internal, in order to get the token with higher permissions
        OAuth2TwoLegged forgeOAuth = oAuthService.getOAuthInternal();
        if (id.equals("#"))
        {
            // root
            BucketsApi bucketsApi = new BucketsApi();
            ApiResponse<Buckets> buckets = bucketsApi.getBuckets("us", 100, null, forgeOAuth,
                    forgeOAuth.getCredentials());
            // iterate buckets
            for (int i = 0; i < buckets.getData().getItems().size(); i++)
            {
                Map<String, Object> map = new HashMap<>();
                BucketsItems eachItem = buckets.getData().getItems().get(i);
                map.put("id", eachItem.getBucketKey());
                map.put("text", eachItem.getBucketKey());
                map.put("type", "bucket");
                map.put("children", true);
                list.add(map);
            }
        } else
        {
            // as we have the id (bucketKey), let's return all objects
            ObjectsApi objectsApi = new ObjectsApi();
            ApiResponse<BucketObjects> objects = objectsApi.getObjects(id, 100, null, null, forgeOAuth,
                    forgeOAuth.getCredentials());
            // iterate each items of the bucket
            for (int i = 0; i < objects.getData().getItems().size(); i++)
            {

                Map<String, Object> map = new HashMap<>();
                // make a note with the base64 urn of the item
                ObjectDetails eachItem = objects.getData().getItems().get(i);
                String base64Urn = DatatypeConverter.printBase64Binary(eachItem.getObjectId().getBytes());
                map.put("id", base64Urn);
                map.put("text", eachItem.getObjectKey());
                map.put("type", "object");
                map.put("children", false);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 创建Bucket
     *
     * @param map
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("/api/forge/oss/buckets")
    public Object doPost(@RequestBody Map<String, String> map) throws Exception
    {

        // get oAuth of internal, in order to get the token with higher permissions
        OAuth2TwoLegged forgeOAuth = oAuthService.getOAuthInternal();
        String bucketKey = map.get("bucketKey");
        // build the payload of the http request
        BucketsApi bucketsApi = new BucketsApi();
        PostBucketsPayload postBuckets = new PostBucketsPayload();
        postBuckets.setBucketKey(bucketKey);
        // expires in 24h
        //postBuckets.setPolicyKey(PostBucketsPayload.PolicyKeyEnum.TRANSIENT);
        //永久存储
        postBuckets.setPolicyKey(PostBucketsPayload.PolicyKeyEnum.PERSISTENT);
        ApiResponse<Bucket> newbucket = bucketsApi.createBucket(postBuckets, null, forgeOAuth,
                forgeOAuth.getCredentials());
        return newbucket;
    }

    @PostMapping("/api/forge/oss/objects")
    public Object uploadFile(HttpServletRequest req, HttpServletResponse res) throws Exception
    {

        String bucketKey = "";
        String filename = "";
        ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
        List<FileItem> items = fileUpload.parseRequest(new ServletRequestContext(req));
        Iterator iter = items.iterator();
        File fileToUpload = null;
        while (iter.hasNext())
        {
            FileItem item = (FileItem) iter.next();
            if (!item.isFormField())
            {
                filename = item.getName();
                // store the file stream on server
                String thisFilePathOnServer = System.getProperty("java.io.tmpdir") + "/" + filename;
                fileToUpload = new File(thisFilePathOnServer);
                item.write(fileToUpload);
            } else
            {
                // get bucket name
                if (item.getFieldName().equals("bucketKey"))
                {
                    bucketKey = item.getString();
                }
            }
        }
        System.out.println("fileToUpload:");
        System.out.println(fileToUpload);
        ObjectsApi objectsApi = new ObjectsApi();
        OAuth2TwoLegged forgeOAuth = oAuthService.getOAuthInternal();
        ApiResponse<ObjectDetails> response = objectsApi.uploadObject(bucketKey, filename,
                (int) fileToUpload.length(), fileToUpload, null, null, forgeOAuth, forgeOAuth.getCredentials());
        return response;
    }

}
