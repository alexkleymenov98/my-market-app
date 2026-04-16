package ru.yandex.practicum.mymarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.mymarket.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // @Query("SELECT o.id as id, o.totalSum as totalSum, op.count as count " +
    //    "FROM OrderEntity o " +
    //    "JOIN o.orderProducts op")
    // List<OrderEntity> findAllWithCount();
}