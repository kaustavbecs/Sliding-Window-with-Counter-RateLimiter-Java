package com.ope.ratelimiter.service;

import com.ope.ratelimiter.service.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@SpringBootTest
class RateLimiterTests {

  @Autowired
  private RateLimiter rateLimiter;


  @Test
  public void checkForNewUser() throws Exception {
    assertThat(rateLimiter.isAllowed("NewBee")).isEqualTo(true);
  }

  @Test
  public void checkForOldUserWithNewTime() throws Exception {
    rateLimiter.isAllowed("OldFaithful");
    assertThat(rateLimiter.isAllowed("OldFaithful")).isEqualTo(true);
  }
  @Test
  public void checkForOldUserWithOldTimeWithinRateLimit() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeWithinTimeLimit")).isEqualTo(true);
  }
  @Test
  public void checkForOldUserWithOldTimeExceedingRateLimit() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(true);

    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(false);
  }
  @Test
  public void checkForRateLimitExceedShouldNotImpactOtherUsers() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit1")).isEqualTo(true);

    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit1")).isEqualTo(false);
    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit2")).isEqualTo(true);
  }
  @Test
  public void checkForOldCountDeletion() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("DeleteCount")).isEqualTo(true);

    assertThat(rateLimiter.isAllowed("DeleteCount")).isEqualTo(false);
    Thread.sleep(rateLimiter.TIME_LIMIT*1000/2); //Waiting for half the time limit - should return false
    assertThat(rateLimiter.isAllowed("DeleteCount")).isEqualTo(false);
    Thread.sleep(rateLimiter.TIME_LIMIT*1000/2); //Waiting for another half; overall delay is beyond time limit - should return true
    assertThat(rateLimiter.isAllowed("DeleteCount")).isEqualTo(true);
  }
  @Test
  public void checkForOldCountDeletionAndNewCountLimitExceed() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("DeleteCountAndNewCount")).isEqualTo(true);

    assertThat(rateLimiter.isAllowed("DeleteCountAndNewCount")).isEqualTo(false);
    Thread.sleep(rateLimiter.TIME_LIMIT*1000); //Overall delay is beyond time limit - should return true
    assertThat(rateLimiter.isAllowed("DeleteCountAndNewCount")).isEqualTo(true);
    for (int i=1;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("DeleteCountAndNewCount")).isEqualTo(true);
    assertThat(rateLimiter.isAllowed("DeleteCountAndNewCount")).isEqualTo(false);
  }
  @Test
  public void ChangeRateAtRuntimeAndCheckLimit() throws Exception {
    //ToDo
  }

}
