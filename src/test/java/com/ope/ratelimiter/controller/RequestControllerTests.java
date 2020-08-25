package com.ope.ratelimiter.controller;

import com.ope.ratelimiter.service.RateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RequestControllerTests {

  @Autowired
  private MockMvc restMockMvc;

  @Autowired
  private RateLimiter rateLimiter;


  public MockHttpServletRequestBuilder myFactoryRequest(String url) {
    return MockMvcRequestBuilders.post(url)
      .header("userid", "user1");
  }

  @Test
  public void checkForNewUser() throws Exception {
    restMockMvc.perform(myFactoryRequest("/isallowed"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(content().string("true"));
  }
  @Test
  public void checkForRequestWithoutRequiredHeader() throws Exception {
    restMockMvc.perform(post("/isallowed"))
      .andExpect(status().isOk())
      .andExpect(content().string("false"));
  }
}
