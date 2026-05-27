package ru.yandex.practicum.payment.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.payment.entity.BalanceEntity;

@Repository
public interface BalanceRepository extends R2dbcRepository<BalanceEntity, String> {

}
