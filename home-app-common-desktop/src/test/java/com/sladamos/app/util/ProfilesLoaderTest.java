package com.sladamos.app.util;

import com.sladamos.app.util.load.ProfilesLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilesLoaderTest {

    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private MutablePropertySources propertySources;

    @InjectMocks
    private ProfilesLoader profilesLoader;

    @Test
    void shouldLoadLocalProfileWhenNoActiveProfilesAreSet() {
        when(environment.getActiveProfiles()).thenReturn(new String[0]);
        when(environment.getPropertySources()).thenReturn(propertySources);

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> {
                    when(mock.exists()).thenReturn(true);
                    when(mock.getInputStream()).thenReturn(new ByteArrayInputStream("dummy.key=value".getBytes()));
                })) {

            profilesLoader.loadProfiles(environment);

            ArgumentCaptor<PropertySource<?>> captor = ArgumentCaptor.forClass(PropertySource.class);
            verify(propertySources).addLast(captor.capture());

            assertThat(captor.getValue().getName()).isEqualTo("config-local");
        }
    }

    @Test
    void shouldNotThrowExceptionWhenProfileFileNotFound() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"nonexistent"});

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> when(mock.exists()).thenReturn(false))) {

            profilesLoader.loadProfiles(environment);

            verify(environment, never()).getPropertySources();
        }
    }
}