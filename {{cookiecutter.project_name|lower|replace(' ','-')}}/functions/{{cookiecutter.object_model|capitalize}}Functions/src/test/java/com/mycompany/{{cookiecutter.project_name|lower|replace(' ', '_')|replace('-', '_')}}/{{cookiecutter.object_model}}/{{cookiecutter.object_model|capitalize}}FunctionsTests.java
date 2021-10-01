package com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}};

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.emitters.Emitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.dataaccess.DataAccess;
import com.mycompany.{{cookiecutter.project_name|lower|replace(' ', '_')|replace('-', '_')}}.{{cookiecutter.object_model}}.model.{{cookiecutter.object_model|capitalize}};
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class {{cookiecutter.object_model|capitalize}}FunctionsTests {

    public static final String ID = "8f72f972-48b6-4655-b179-5e698a2f1c4f";

    @Mock
    protected Context context;

    @Mock
    protected DataAccess<{{cookiecutter.object_model|capitalize}}> dataAccess;

    protected final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    protected void mockXRay() {
        Emitter blankEmitter = mock(Emitter.class);
        AWSXRay.setGlobalRecorder(AWSXRayRecorderBuilder.standard().withEmitter(blankEmitter).build());
        AWSXRay.clearTraceEntity();
        AWSXRay.beginSegment("test");
    }

    @BeforeAll
    protected static void mockEMF() throws Exception {
        // See https://github.com/awslabs/aws-embedded-metrics-node#configuration
        updateEnv("AWS_EMF_ENVIRONMENT", "Local");
    }

    @SuppressWarnings({ "unchecked" })
    public static void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }
}
