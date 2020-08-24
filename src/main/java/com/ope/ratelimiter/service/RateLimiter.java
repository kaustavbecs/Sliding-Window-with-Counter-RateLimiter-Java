package com.ope.ratelimiter.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RateLimiter {

  @Value("${request.time.limit.inseconds}")
  private long TIME_LIMIT;

  @Value("${request.count.limit}")
  private long RATE_LIMIT;

  @Value("${time.window.count}")
  private long NUMBER_OF_TIME_WINDOWS;

  private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
  private Map<String, Map<Long, AtomicLong>> UserTimeMap = new ConcurrentHashMap<>();

  //Main method to check if new requests are allowed
  public boolean isAllowed(String username) {
    long currentTime = Instant.now().getEpochSecond() / NUMBER_OF_TIME_WINDOWS;
    //Reference for individual user count data
    Map<Long, AtomicLong> individualUserHits;

    // Case 1: User specific Entry does not exist
    if (!UserTimeMap.containsKey(username)) {
      individualUserHits = new HashMap<>();
      individualUserHits.put(currentTime, new AtomicLong(1L));
      UserTimeMap.put(username, individualUserHits);
      System.out.println("currentTimeWindow" + currentTime + "=" + true);
      return true;
    }
    // Case 2: User specific Entry exists, Time Window for the user may or may not exist in the map
    else {
      individualUserHits = UserTimeMap.get(username);
      return checkAndAddForExistingUsers(username, currentTime, individualUserHits);
    }
  }


  public boolean checkAndAddForExistingUsers(String username, long currentTimeWindow, Map<Long, AtomicLong> timeWindowVSCountMap) {
    Long countInOverallTime = removeOldEntriesForUser(username, currentTimeWindow, timeWindowVSCountMap);

    if (countInOverallTime < RATE_LIMIT) {
      //Handle new time windows
      if (!timeWindowVSCountMap.containsKey(currentTimeWindow))
        timeWindowVSCountMap.put(currentTimeWindow, new AtomicLong(0L));

      Long newCount = timeWindowVSCountMap.get(currentTimeWindow).longValue() + 1;
      timeWindowVSCountMap.remove(currentTimeWindow);
      timeWindowVSCountMap.put(currentTimeWindow, new AtomicLong(newCount));
      System.out.println("currentTimeWindow" + currentTimeWindow + "=" + true+" countInOverallTime="+countInOverallTime);
      return true;
    }
    System.out.println("currentTimeWindow" + currentTimeWindow + "=" + false+" countInOverallTime="+countInOverallTime);
    return false;
  }

  public long removeOldEntriesForUser(String username, long currentTimeWindow, Map<Long, AtomicLong> timeWindowVSCountMap)
  {
    Long countInOverallTime = 0L;
    for (Long timeWindow : timeWindowVSCountMap.keySet()) {
      //Remove old entries for the user
      if ((currentTimeWindow - timeWindow) * NUMBER_OF_TIME_WINDOWS > TIME_LIMIT)
        timeWindowVSCountMap.remove(timeWindow);
      //Compute overall request count for the user in the time_limit window
      else
        countInOverallTime += timeWindowVSCountMap.get(timeWindow).longValue();
    }
    return countInOverallTime;
  }
}
