package com.ope.ratelimiter.ratelimiter;

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
class RatelimiterApplicationTests {

  @Autowired
  private RateLimiter rateLimiter;


  @Test
  public void checkForNewUser() throws Exception {
    assertThat(rateLimiter.isAllowed("NewBee")).isEqualTo(true);
  }

  @Test
  public void checkForOldUserWithNewTime() throws Exception {
    rateLimiter.isAllowed("OldFaithful");
    Thread.sleep(101);
    assertThat(rateLimiter.isAllowed("OldFaithful")).isEqualTo(true);
  }
  @Test
  public void checkForOldUserWithOldTimeWithinRateLimit() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(true);
  }
  @Test
  public void checkForOldUserWithOldTimeExceedingRateLimit() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(true);
    Thread.sleep(1);
    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondRate")).isEqualTo(false);
    Thread.sleep(1);
    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondRate")).isEqualTo(false);
  }
  @Test
  public void checkForRateLimitExceedShouldNotImpactOtherUsers() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(true);
    Thread.sleep(1);
    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondRate")).isEqualTo(false);
    Thread.sleep(1);
    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTime")).isEqualTo(true);
  }
  @Test
  public void checkForOldCountDeletion() throws Exception {
    for (int i=0;i<rateLimiter.RATE_LIMIT;i++)
      assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(true);

    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(false);
    Thread.sleep(15000);
    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(false);
    Thread.sleep(15000);
    assertThat(rateLimiter.isAllowed("OldFaithfulWithOldTimeBeyondTimeLimit")).isEqualTo(true);
  }
  @Test
  public void ChangeRateAtRuntimeAndCheckLimit() throws Exception {
    //ToDo
  }

}
