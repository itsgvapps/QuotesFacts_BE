package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.entity.AppErrorLogEntity;

public interface AppErrorLogService {

    AppErrorLogEntity logError(AppErrorLogEntity errorLog);
}
