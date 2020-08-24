package com.ope.ratelimiter.controller;
import com.ope.ratelimiter.service.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/")

public class RequestController {

  @Value("${header.userid.attribute.name}")
  public String USER_ID_ATTRIBUTE;

  @Autowired
  private RateLimiter rateLimiter;

  @PostMapping(path="/isallowed")
  public boolean isAllowed(@RequestHeader Map<String,String> header)
  {
    if (!header.containsKey(USER_ID_ATTRIBUTE)) return false;
    return rateLimiter.isAllowed(header.get(USER_ID_ATTRIBUTE));
  }
}
