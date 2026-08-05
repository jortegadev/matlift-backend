package com.matlift.readiness.domain.port.in;

import com.matlift.readiness.domain.model.DailyReadiness;

public interface SaveDailyReadinessUseCase {

    DailyReadiness execute(SaveDailyReadinessCommand command);
}
