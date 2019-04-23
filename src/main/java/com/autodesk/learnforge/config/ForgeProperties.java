package com.autodesk.learnforge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: maxwellens
 */
@Data
@Component
@ConfigurationProperties(prefix = "autodesk.forge")
public class ForgeProperties
{
    private String clientId;

    private String clientSecret;
}
