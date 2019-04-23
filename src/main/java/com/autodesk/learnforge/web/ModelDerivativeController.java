package com.autodesk.learnforge.web;

import com.autodesk.client.ApiResponse;
import com.autodesk.client.api.DerivativesApi;
import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.client.model.*;
import com.autodesk.learnforge.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/**
 * 模型转换
 * @author Lenovo
 */
@RestController
public class ModelDerivativeController
{
    @Autowired
    private OAuthService oAuthService;

    /**
     * 模型转换
     *
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping("//api/forge/modelderivative/jobs")
    public Object doModelTranslate(@RequestBody Map<String, String> map) throws Exception
    {
        OAuth2TwoLegged forgeOAuth = oAuthService.getOAuthInternal();
        String objectName = map.get("objectName");
        DerivativesApi derivativesApi = new DerivativesApi();
        // build the payload to translate the file to svf
        JobPayload job = new JobPayload();
        JobPayloadInput input = new JobPayloadInput();
        input.setUrn(objectName);
        JobPayloadOutput output = new JobPayloadOutput();
        JobPayloadItem formats = new JobPayloadItem();
        formats.setType(JobPayloadItem.TypeEnum.SVF);
        formats.setViews(Arrays.asList(JobPayloadItem.ViewsEnum._3D));
        output.setFormats(Arrays.asList(formats));
        job.setInput(input);
        job.setOutput(output);
        ApiResponse<Job> response = derivativesApi.translate(job, true, forgeOAuth, forgeOAuth.getCredentials());
        return response;
    }

}