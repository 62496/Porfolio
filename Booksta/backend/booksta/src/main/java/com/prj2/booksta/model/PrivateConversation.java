package com.prj2.booksta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "private_conversation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant1_id", "participant2_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "messages")
@ToString(exclude = "messages")
public class PrivateConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "private_conversation_seq")
    @SequenceGenerator(
            name = "private_conversation_seq",
            sequenceName = "private_conversation_sequence",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant1_id")
    private User participant1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant2_id")
    private User participant2;

    @JsonIgnore
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrivateMessage> messages = new ArrayList<>();

    public boolean involvesUser(Long userId) {
        return participant1 != null && participant2 != null
                && (participant1.getId().equals(userId) || participant2.getId().equals(userId));
    }
}
