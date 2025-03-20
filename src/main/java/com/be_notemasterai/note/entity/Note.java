package com.be_notemasterai.note.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.be_notemasterai.group.entity.Group;
import com.be_notemasterai.member.entity.Member;
import com.be_notemasterai.note.dto.NoteUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "notes")
@EntityListeners(AuditingEntityListener.class)
public class Note {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @JoinColumn(name = "owner_id", nullable = false)
  @ManyToOne(fetch = LAZY)
  private Member owner;

  @Setter
  @JoinColumn(name = "group_id")
  @ManyToOne(fetch = LAZY)
  private Group group;

  @Column(nullable = false)
  private String title;

  @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
  private String originalText;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String summary;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public static Note of(Member owner, String title, String originalText, String summary) {

    return Note.builder()
        .owner(owner)
        .group(null)
        .title(title)
        .originalText(originalText)
        .summary(summary)
        .build();
  }

  public void updateNote(NoteUpdateRequest noteUpdateRequest) {

    title = noteUpdateRequest.title();
    summary = noteUpdateRequest.summary();
  }
}