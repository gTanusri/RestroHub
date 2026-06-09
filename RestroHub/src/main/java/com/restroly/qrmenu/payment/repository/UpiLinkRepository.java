package com.restroly.qrmenu.payment.repository;

import com.restroly.qrmenu.payment.entity.UpiLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UpiLinkRepository extends JpaRepository<UpiLink, Long> {

    boolean existsByUpiIdIgnoreCase(String upiId);

    List<UpiLink> findAllByOrderByDefaultLinkDescCreatedAtAsc();

    Optional<UpiLink> findByDefaultLinkTrue();
}
