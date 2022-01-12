import com.dinuberinde.hooks.HooksAOP;
import controller.Controller;
import helper.DataHolder;
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
import hooks.LogHookComponent;

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

        assertEquals("/hello", DataHolder.map.get("LogHookPRE").tag);
        assertEquals("1", DataHolder.map.get("LogHookPRE").value);

        assertEquals("/hello", DataHolder.map.get("LogHookPOST").tag);
        assertEquals("1", DataHolder.map.get("LogHookPOST").value);
    }

    @Test
    @Order(2)
    public void shouldCallSpringComponentHooks() throws Exception {
        mockMvc.perform(get("/hello-spring-component").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("hello-spring-component", DataHolder.map.get("LogHookComponent").tag);
        assertEquals("1", DataHolder.map.get("LogHookComponent").value);
    }

    @Test
    @Order(3)
    public void shouldCallDefaultPreAndPostHooks() throws Exception {
        mockMvc.perform(get("/hello-default-hook-methods").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("mytag", DataHolder.map.get("LogHookDefaultPRE").tag);
        assertEquals("1", DataHolder.map.get("LogHookDefaultPRE").value);

        assertEquals("mytag", DataHolder.map.get("LogHookDefaultPOST").tag);
        assertEquals("1", DataHolder.map.get("LogHookDefaultPOST").value);
    }

   @Test
   @Order(4)
   public void shouldCallMultiplePreAndPostHooks() throws Exception {
       mockMvc.perform(get("/hello-multiple-hooks").contentType("application/json"))
               .andExpect(status().isOk());


       assertEquals("/hello-multiple-hooks", DataHolder.map.get("LogHookDefaultPRE").tag);
       assertEquals("2", DataHolder.map.get("LogHookDefaultPRE").value);
       assertEquals("/hello-multiple-hooks", DataHolder.map.get("LogHookDefaultPOST").tag);
       assertEquals("2", DataHolder.map.get("LogHookDefaultPOST").value);

       assertEquals("/hello-multiple-hooks", DataHolder.map.get("LogHookPRE").tag);
       assertEquals("2", DataHolder.map.get("LogHookPRE").value);
       assertEquals("/hello-multiple-hooks", DataHolder.map.get("LogHookPOST").tag);
       assertEquals("2", DataHolder.map.get("LogHookPOST").value);
   }

    @Test
    @Order(5)
    public void shouldCallExceptioHook() {
        try {
            mockMvc.perform(get("/exception").contentType("application/json"))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            assertEquals(DataHolder.map.get("Exception").value, "java.lang.NullPointerException");
            assertEquals(DataHolder.map.get("Exception").tag, "myexceptiontag");

            assertEquals("/exception", DataHolder.map.get("LogHookPOST").tag);
            assertEquals("3", DataHolder.map.get("LogHookPOST").value);
        }
    }

    @Test
    @Order(6)
    public void shouldCallPreHookAndAccessTheHttpServletRequest() throws Exception {
        mockMvc.perform(get("/hello-http-servlet-request?query=tomcat").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("/hello-http-servlet-request", DataHolder.map.get("LogHttpServletRequestHook").tag);
        assertEquals("query=tomcat", DataHolder.map.get("LogHttpServletRequestHook").value);
    }

    @Test
    @Order(7)
    public void shouldCallDataInHook() throws Exception {
        String result = mockMvc.perform(get("/data-in?value=spring").contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("spring@this is injected by the hook method", result);
        assertEquals("this is injected by the hook method", DataHolder.map.get("DataInHookSupplier").value);
        assertEquals("", DataHolder.map.get("DataInHookSupplier").tag);
    }

    @Test
    @Order(8)
    public void shouldCallDataOutHook() throws Exception {
        String result = mockMvc.perform(get("/data-out?value=dataOutTest").contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("dataOutTest", result);
        assertEquals("dataOutTest", DataHolder.map.get("DataOutHookConsumer").value);
        assertEquals("", DataHolder.map.get("DataOutHookConsumer").tag);
    }
}
