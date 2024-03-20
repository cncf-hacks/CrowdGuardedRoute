package com.root.repository.mart;

import com.root.entity.mart.MemberLocationMart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLocationRepository extends JpaRepository<MemberLocationMart, String> {
}
