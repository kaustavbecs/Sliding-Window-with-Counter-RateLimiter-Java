package com.ope.ratelimiter.controller;
import com.ope.ratelimiter.service.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")

public class RequestController {

  @Autowired
  private RateLimiter rateLimiter;

  @PostMapping(path="/isallowed")
  public boolean isAllowed(@RequestHeader (name="userid")  String userId)
  {
    if (StringUtils.isEmpty(userId)) return false;
    return rateLimiter.isAllowed(userId);
  }
}
