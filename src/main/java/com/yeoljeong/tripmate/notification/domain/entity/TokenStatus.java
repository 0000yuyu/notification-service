package com.yeoljeong.tripmate.notification.domain.entity;

import com.yeoljeong.tripmate.notification.domain.constants.TokenActiveStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.RequiredArgsConstructor;

@Embeddable
@RequiredArgsConstructor
public class TokenStatus {

  @Enumerated(EnumType.STRING)
  @Column(name = "token_status", nullable = false)
  TokenActiveStatus activeStatus;

  @Column(nullable = false)
  Integer failCount;

  private TokenStatus(TokenActiveStatus tokenActiveStatus, int failCount) {
    this.activeStatus = tokenActiveStatus;
    this.failCount = failCount;
  }

  public static TokenStatus activeInitial() {
    return new TokenStatus(TokenActiveStatus.ACTIVE, 0);
  }

  public static TokenStatus inactiveInitial() {
    return new TokenStatus(TokenActiveStatus.INACTIVE, 0);
  }

  public TokenStatus markFailure(int MAX_FAIL_COUNT) {
    int next = this.failCount + 1;
    if (next >= MAX_FAIL_COUNT) {
      return new TokenStatus(TokenActiveStatus.EXPIRED, next);
    }
    return new TokenStatus(this.activeStatus, this.failCount + 1);
  }

  public boolean isUsable() {
    return this.activeStatus == TokenActiveStatus.ACTIVE;
  }
}
