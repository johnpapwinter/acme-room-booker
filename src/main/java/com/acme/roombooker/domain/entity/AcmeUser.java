package com.acme.roombooker.domain.entity;

import com.acme.roombooker.domain.enums.AcmeRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "acme_users")
@Getter
@Setter
public class AcmeUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AcmeRole role;

    @OneToMany(mappedBy = "acmeUser")
    private List<Meeting> meetings = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcmeUser acmeUser = (AcmeUser) o;
        return Objects.equals(id, acmeUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
