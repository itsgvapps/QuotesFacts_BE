package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.entity.AppErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppErrorLogRepository extends JpaRepository<AppErrorLogEntity, Long> {
}