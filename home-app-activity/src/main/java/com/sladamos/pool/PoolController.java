package com.sladamos.activity;

import com.sladamos.activity.dto.GetPoolsResponse;
import com.sladamos.pool.PoolService;
import com.sladamos.pool.function.PoolsToResponseFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/pool")
@RequiredArgsConstructor
public class PoolController {

    private final PoolService service;

    private final PoolsToResponseFunction poolsToResponseFunction;

    @GetMapping
    public GetPoolsResponse getPools() {
        log.info("Request fetching all pools");
        GetPoolsResponse response = poolsToResponseFunction.apply(service.getAllPools());
        log.info("Fetched all pools: [count: {}]", response.getPools().size());
        return response;
    }
}
