package com.sladamos.app.util.load;

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

    private static final String DEFAULT_PROFILE = "local";

    private static final String PROFILE_PREFIX = "application-";

    private static final String PROFILE_SUFFIX = ".properties";

    private static final String PROPERTY_SOURCE_PREFIX = "config-";

    public void loadProfiles(ConfigurableEnvironment contextEnvironment) {
        String[] activeProfiles = getActiveProfilesOrDefault(contextEnvironment);
        Arrays.stream(activeProfiles).forEach(this.loadProfile(contextEnvironment));
    }

    private String[] getActiveProfilesOrDefault(ConfigurableEnvironment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            return new String[]{DEFAULT_PROFILE};
        }
        return activeProfiles;
    }

    private Consumer<String> loadProfile(ConfigurableEnvironment contextEnvironment) {
        return profile -> {
            try {
                ClassPathResource resource = new ClassPathResource(PROFILE_PREFIX + profile + PROFILE_SUFFIX);
                addResourceToEnvironment(contextEnvironment, profile, resource);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load properties for profile: " + profile, e);
            }
        };
    }

    private void addResourceToEnvironment(ConfigurableEnvironment environment, String profile, ClassPathResource resource) throws IOException {
        if (resource.exists()) {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            PropertiesPropertySource propertySource = new PropertiesPropertySource(PROPERTY_SOURCE_PREFIX + profile, props);
            environment.getPropertySources().addLast(propertySource);
            log.info("Loaded profile: [profile: {}]", profile);
        } else {
            log.error("No properties found for: [profile: {}]", profile);
        }
    }
}
