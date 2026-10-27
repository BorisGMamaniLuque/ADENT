package com.adent.identity.domain;
import com.adent.shared.domain.BaseEntity; import jakarta.persistence.*;
@Entity @Table(name="roles")
public class Role extends BaseEntity {
 @Column(nullable=false,unique=true,length=50) private String code;
 @Column(nullable=false,length=100) private String name;
 public String getCode(){return code;} public void setCode(String v){code=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
}
