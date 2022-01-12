import com.dinuberinde.hooks.HooksAOP;
import controller.Controller;
import helper.DataHolder;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {Controller.class, HooksAOP.class, LogHookComponent.class})
@AutoConfigureMockMvc
@Import(AnnotationAwareAspectJAutoProxyCreator.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HookTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void shouldCallPreAndPostHooks() throws Exception {
        mockMvc.perform(get("/hello").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("/hello", DataHolder.map.get(LogHook.LOG_PRE).tag);
        assertEquals("1", DataHolder.map.get(LogHook.LOG_PRE).value);

        assertEquals("/hello", DataHolder.map.get(LogHook.LOG_POST).tag);
        assertEquals("1", DataHolder.map.get(LogHook.LOG_POST).value);
    }

    @Test
    @Order(2)
    public void shouldCallSpringComponentHooks() throws Exception {
        mockMvc.perform(get("/hello-spring-component").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("hello-spring-component", DataHolder.map.get(LogHookComponent.class.getName()).tag);
        assertEquals("1", DataHolder.map.get(LogHookComponent.class.getName()).value);
    }

    @Test
    @Order(3)
    public void shouldCallDefaultPreAndPostHooks() throws Exception {
        mockMvc.perform(get("/hello-default-hook-methods").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("mytag", DataHolder.map.get(LogHookDefault.LOG_PRE).tag);
        assertEquals("1", DataHolder.map.get(LogHookDefault.LOG_PRE).value);

        assertEquals("mytag", DataHolder.map.get(LogHookDefault.LOG_POST).tag);
        assertEquals("1", DataHolder.map.get(LogHookDefault.LOG_POST).value);
    }

   @Test
   @Order(4)
   public void shouldCallMultiplePreAndPostHooks() throws Exception {
       mockMvc.perform(get("/hello-multiple-hooks").contentType("application/json"))
               .andExpect(status().isOk());


       assertEquals("/hello-multiple-hooks", DataHolder.map.get(LogHookDefault.LOG_PRE).tag);
       assertEquals("2", DataHolder.map.get(LogHookDefault.LOG_PRE).value);
       assertEquals("/hello-multiple-hooks", DataHolder.map.get(LogHookDefault.LOG_POST).tag);
       assertEquals("2", DataHolder.map.get(LogHookDefault.LOG_POST).value);

       assertEquals("/hello-multiple-hooks", DataHolder.map.get(LogHook.LOG_PRE).tag);
       assertEquals("2", DataHolder.map.get(LogHook.LOG_PRE).value);
       assertEquals("/hello-multiple-hooks", DataHolder.map.get(LogHook.LOG_POST).tag);
       assertEquals("2", DataHolder.map.get(LogHook.LOG_POST).value);
   }

    @Test
    @Order(5)
    public void shouldCallExceptionHook() {
        try {
            mockMvc.perform(get("/exception").contentType("application/json"))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            assertEquals(DataHolder.map.get(LogException.class.getName()).value, "java.lang.NullPointerException");
            assertEquals(DataHolder.map.get(LogException.class.getName()).tag, "myexceptiontag");

            assertEquals("/exception", DataHolder.map.get(LogHook.LOG_POST).tag);
            assertEquals("3", DataHolder.map.get(LogHook.LOG_POST).value);
        }
    }

    @Test
    @Order(6)
    public void shouldCallPreHookAndAccessTheHttpServletRequest() throws Exception {
        mockMvc.perform(get("/hello-http-servlet-request?query=tomcat").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("/hello-http-servlet-request", DataHolder.map.get(LogHttpServletRequestHook.class.getName()).tag);
        assertEquals("query=tomcat", DataHolder.map.get(LogHttpServletRequestHook.class.getName()).value);
    }

    @Test
    @Order(7)
    public void shouldCallDataInHook() throws Exception {
        String result = mockMvc.perform(get("/data-in?value=spring").contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("spring@this is supplied by the hook method", result);
        assertEquals("this is supplied by the hook method", DataHolder.map.get(DataInHookSupplier.class.getName()).value);
        assertEquals("", DataHolder.map.get(DataInHookSupplier.class.getName()).tag);
    }

    @Test
    @Order(8)
    public void shouldCallDataOutHook() throws Exception {
        String result = mockMvc.perform(get("/data-out?value=dataOutTestValue").contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("dataOutTestValue", result);
        assertEquals("dataOutTestValue", DataHolder.map.get(DataOutHookConsumer.class.getName()).value);
        assertEquals("", DataHolder.map.get(DataOutHookConsumer.class.getName()).tag);
    }
}
