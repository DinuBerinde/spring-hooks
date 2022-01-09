import com.dinuberinde.webhooks.WebhooksAOP;
import controller.Controller;
import helper.DataHolder;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import webhooks.LogWebhookComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = {Controller.class, WebhooksAOP.class, LogWebhookComponent.class})
@AutoConfigureMockMvc
@Import(AnnotationAwareAspectJAutoProxyCreator.class)
public class WebhookTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldCallPreAndPostWebhooks() throws Exception {
        mockMvc.perform(get("/hello").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("/hello", DataHolder.map.get("LogWebhookPRE").tag);
        assertEquals("1", DataHolder.map.get("LogWebhookPRE").value);

        assertEquals("/hello", DataHolder.map.get("LogWebhookPOST").tag);
        assertEquals("1", DataHolder.map.get("LogWebhookPOST").value);
    }

    @Test
    public void shouldCallSpringComponentWebhooks() throws Exception {
        mockMvc.perform(get("/hello-spring-component").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("hello-spring-component", DataHolder.map.get("LogWebhookComponent").tag);
        assertEquals("1", DataHolder.map.get("LogWebhookComponent").value);
    }

    @Test
    public void shouldCallDefaultPreAndPostWebhooks() throws Exception {
        mockMvc.perform(get("/hello-default-webhook-methods").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("mytag", DataHolder.map.get("LogWebhookDefaultPRE").tag);
        assertEquals("1", DataHolder.map.get("LogWebhookDefaultPRE").value);

        assertEquals("mytag", DataHolder.map.get("LogWebhookDefaultPOST").tag);
        assertEquals("1", DataHolder.map.get("LogWebhookDefaultPOST").value);
    }

   @Test
   public void shouldCallMultiplePreAndPostWebhooks() throws Exception {
       mockMvc.perform(get("/hello-multiple-webhooks").contentType("application/json"))
               .andExpect(status().isOk());


       assertEquals("/hello-multiple-webhooks", DataHolder.map.get("LogWebhookDefaultPRE").tag);
       assertEquals("2", DataHolder.map.get("LogWebhookDefaultPRE").value);
       assertEquals("/hello-multiple-webhooks", DataHolder.map.get("LogWebhookDefaultPOST").tag);
       assertEquals("2", DataHolder.map.get("LogWebhookDefaultPOST").value);

       assertEquals("/hello-multiple-webhooks", DataHolder.map.get("LogWebhookPRE").tag);
       assertEquals("2", DataHolder.map.get("LogWebhookPRE").value);
       assertEquals("/hello-multiple-webhooks", DataHolder.map.get("LogWebhookPOST").tag);
       assertEquals("2", DataHolder.map.get("LogWebhookPOST").value);
   }

    @Test
    public void shouldCallExceptionWebhook() {
        try {
            mockMvc.perform(get("/exception").contentType("application/json"))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            assertEquals(DataHolder.map.get("Exception").value, "java.lang.NullPointerException");
            assertEquals(DataHolder.map.get("Exception").tag, "myexceptiontag");

            assertEquals("/exception", DataHolder.map.get("LogWebhookPOST").tag);
            assertEquals("3", DataHolder.map.get("LogWebhookPOST").value);
        }
    }

    @Test
    public void shouldCallPreWebhookAndAccessTheHttpServletRequest() throws Exception {
        mockMvc.perform(get("/hello-http-servlet-request?query=tomcat").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("/hello-http-servlet-request", DataHolder.map.get("LogHttpServletRequestWebhook").tag);
        assertEquals("query=tomcat", DataHolder.map.get("LogHttpServletRequestWebhook").value);
    }

    @Test
    public void shouldCallDataInWebhook() throws Exception {
        String result = mockMvc.perform(get("/data-in?value=spring").contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("spring@this is injected by the webhook method", result);
        assertEquals("this is injected by the webhook method", DataHolder.map.get("DataInWebhookSupplier").value);
        assertEquals("", DataHolder.map.get("DataInWebhookSupplier").tag);
    }

    @Test
    public void shouldCallDataOutWebhook() throws Exception {
        String result = mockMvc.perform(get("/data-out?value=dataOutTest").contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("dataOutTest", result);
        assertEquals("dataOutTest", DataHolder.map.get("DataOutWebhookConsumer").value);
        assertEquals("", DataHolder.map.get("DataOutWebhookConsumer").tag);
    }
}
