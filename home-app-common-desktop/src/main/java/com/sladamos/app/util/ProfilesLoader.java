package com.sladamos.app.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;

@Slf4j
public class ProfilesLoader {

    public void loadProfiles(ConfigurableEnvironment contextEnvironment) {
        String[] activeProfiles = contextEnvironment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            activeProfiles = new String[]{"local"};
        }
        Arrays.stream(activeProfiles).forEach(this.loadProfile(contextEnvironment));
    }

    private Consumer<String> loadProfile(ConfigurableEnvironment contextEnvironment) {
        return profile -> {
            try {
                ClassPathResource resource = new ClassPathResource("application-" + profile + ".properties");
                addResourceIfExists(contextEnvironment, profile, resource);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load properties for profile: " + profile, e);
            }
        };
    }

    private void addResourceIfExists(ConfigurableEnvironment contextEnvironment, String profile, ClassPathResource resource) throws IOException {
        if (resource.exists()) {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            PropertiesPropertySource propertySource = new PropertiesPropertySource("config-" + profile, props);
            contextEnvironment.getPropertySources().addLast(propertySource);
            log.info("Loaded profile: [profile: {}]", profile);
        } else {
            log.error("No properties found for: [profile: {}]", profile);
        }
    }
}
