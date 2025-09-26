package com.staffservice.staffservice.entities;

import com.staffservice.staffservice.dtos.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Staff")
public class Staff implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private  Long staffId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "admission_year")
    private Long admissionYear;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;


    @Column(name = "department")
    private String department;

    @Column(name = "password")
    private String password;

    @Column(name = "birth_year")
    private Long birthYear;

    @Enumerated(EnumType.STRING)
    private Role role;






    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
