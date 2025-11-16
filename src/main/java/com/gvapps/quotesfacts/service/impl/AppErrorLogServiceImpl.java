package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.entity.AppErrorLogEntity;
import com.gvapps.quotesfacts.repository.AppErrorLogRepository;
import com.gvapps.quotesfacts.service.AppErrorLogService;
import org.springframework.stereotype.Service;

@Service
public class AppErrorLogServiceImpl implements AppErrorLogService {

    private final AppErrorLogRepository repository;

    public AppErrorLogServiceImpl(AppErrorLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public AppErrorLogEntity logError(AppErrorLogEntity errorLog) {
        return repository.save(errorLog);
    }
}

