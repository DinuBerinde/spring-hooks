import com.dinuberinde.hooks.HooksAOP;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import helper.DataHolder;
import helper.Person;
import hooks.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {Controller.class, HooksAOP.class, LogHookComponent.class})
@AutoConfigureMockMvc
@Import(AnnotationAwareAspectJAutoProxyCreator.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableWebMvc
public class HookTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void shouldTriggerPreAndPostHooks() throws Exception {
        mockMvc.perform(get("/hello?query=tomcat").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("/hello", DataHolder.map.get(LogHook.LOG_PRE).tag);
        assertEquals("tomcat", DataHolder.map.get(LogHook.LOG_PRE).value);

        assertEquals("/hello", DataHolder.map.get(LogHook.LOG_POST).tag);
        assertEquals("1", DataHolder.map.get(LogHook.LOG_POST).value);
    }

   @Test
   @Order(2)
   public void shouldTriggerMultiplePreAndPostHooks() throws Exception {
       mockMvc.perform(get("/hello-multiple-hooks?query=hello-world").contentType("application/json"))
               .andExpect(status().isOk());


       assertEquals("/hello-multiple-hooks", DataHolder.map.get(LogHookComponent.class.getName()).tag);
       assertEquals("2", DataHolder.map.get(LogHookComponent.class.getName()).value);

       assertEquals("/hello-multiple-hooks", DataHolder.map.get(LogHook.LOG_PRE).tag);
       assertEquals("hello-world", DataHolder.map.get(LogHook.LOG_PRE).value);
       assertEquals("/hello-multiple-hooks", DataHolder.map.get(LogHook.LOG_POST).tag);
       assertEquals("2", DataHolder.map.get(LogHook.LOG_POST).value);
   }

    @Test
    @Order(3)
    public void shouldTriggerExceptionHook() {
        try {
            mockMvc.perform(get("/exception").contentType("application/json"))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            assertEquals("java.lang.NullPointerException", DataHolder.map.get(LogException.class.getName()).value);
            assertEquals("/exception", DataHolder.map.get(LogException.class.getName()).tag);

            assertEquals("/exception", DataHolder.map.get(LogHook.LOG_POST).tag);
            assertEquals("3", DataHolder.map.get(LogHook.LOG_POST).value);
        }
    }

    @Test
    @Order(4)
    public void shouldTriggerPersonHooks() throws Exception {
        // create person without an ID
        Person inputPerson = new Person();
        inputPerson.setName("tom");
        inputPerson.setSurname("cat");

        // invoke rest method
        String result = mockMvc.perform(post("/create-person")
                        .content(toJsonString(inputPerson))
                        .contentType("application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(Charset.defaultCharset())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // result of the rest invocation
        Person person = fromJson(result, Person.class);

        // testing dataIn hook
        assertEquals("tom", person.getName());
        assertEquals("cat", person.getSurname());
        assertEquals(20, person.getId());

        // testing dataOut hook
        assertEquals("tom", DataHolder.map.get(PersonDataOutHook.class.getName()).value);
    }

    public static String toJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
