package com.be_notemasterai.group.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.be_notemasterai.group.dto.GroupRequest;
import com.be_notemasterai.group.dto.GroupUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Table(name = "groups")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Group {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(name = "group_name", nullable = false)
  private String groupName;

  @Column(name = "group_description")
  private String groupDescription;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public static Group of(GroupRequest groupRequest) {

    return Group.builder()
        .groupName(groupRequest.groupName())
        .groupDescription(groupRequest.groupDescription())
        .build();
  }

  public void update(GroupUpdateRequest groupUpdateRequest) {

    groupName = groupUpdateRequest.groupName();
    groupDescription = groupUpdateRequest.groupDescription();
  }
}