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
  public long TIME_LIMIT;

  @Value("${request.count.limit}")
  public long RATE_LIMIT;

  @Value("${time.window.count}")
  public long NUMBER_OF_TIME_WINDOWS;

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
      logger.debug("CurrentTimeWindow:" + currentTime +" Result:true "+ " Count:1");
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
      Long newCount = timeWindowVSCountMap.getOrDefault(currentTimeWindow, new AtomicLong(0)).longValue() + 1;
      timeWindowVSCountMap.put(currentTimeWindow, new AtomicLong(newCount));
      logger.debug("CurrentTimeWindow:" + currentTimeWindow +" Result:true "+ " Count:"+countInOverallTime);
      return true;
    }
    logger.debug("CurrentTimeWindow:" + currentTimeWindow +" Result:false "+ " Count:"+countInOverallTime);
    return false;
  }
  // Remove Old Entries for the user and returns the current overall request count for the user
  public long removeOldEntriesForUser(String username, long currentTimeWindow, Map<Long, AtomicLong> timeWindowVSCountMap)
  {
    List <Long> oldEntriesToBeDeleted=new ArrayList<>();
    long overallCount=0L;
    for (Long timeWindow : timeWindowVSCountMap.keySet()) {
      //Mark old entries (Entries older than the oldest valid time window within the time limit) for deletion
      if ((currentTimeWindow - timeWindow) >= TIME_LIMIT/NUMBER_OF_TIME_WINDOWS)
        oldEntriesToBeDeleted.add(timeWindow);
      else
        overallCount+=timeWindowVSCountMap.get(timeWindow).longValue();
    }
    timeWindowVSCountMap.keySet().removeAll(oldEntriesToBeDeleted);
    return overallCount;
  }
}
