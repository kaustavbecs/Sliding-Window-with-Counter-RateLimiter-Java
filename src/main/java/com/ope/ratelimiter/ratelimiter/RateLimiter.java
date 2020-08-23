package com.ope.ratelimiter.ratelimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RateLimiter {

  @Value("${request.time.limit}")
  private long TIME_LIMIT;

  @Value("${request.count.limit}")
  private long RATE_LIMIT;

  private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
  Map<String, Queue<Long>> UserTimeMap = new HashMap<>();

  public boolean isAllowed(String username) {
    long currTime = System.currentTimeMillis();
    //Reference for individual user count data
    Queue<Long> IndividualUserHits;
    if (!UserTimeMap.containsKey(username)) {
      IndividualUserHits = new LinkedList<>();
      checkAndAdd(currTime, IndividualUserHits);
      return true;
    } else {
      IndividualUserHits = UserTimeMap.get(username);
      return checkAndAdd(currTime, IndividualUserHits);
    }
   }
  public boolean checkAndAdd(long timestamp, Queue<Long> q) {
    //Remove old entries for the user
    while (!q.isEmpty() && q.peek() - timestamp >= TIME_LIMIT)
      q.poll();
    //Check count within the time limit
    if (q.size() < RATE_LIMIT) {
      //Add additional entry for the user
      q.add(timestamp);
      return true;
    }
    return false;
  }
}
