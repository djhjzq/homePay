package com.demchenko.homepay.entity;

import com.demchenko.homepay.entity.basic.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@Entity
public class Role extends NamedEntity {

    @OneToMany(mappedBy = "role")
    private Set<User> userSet;

}
