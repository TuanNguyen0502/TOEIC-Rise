package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.EGender;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private EGender gender;

    @Column(name = "avatar", columnDefinition = "VARCHAR(255)")
    private String avatar;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", unique = true)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatTitle> chatTitles;
}
