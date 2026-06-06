package com.aipo.weddingshop.repository;

import com.aipo.weddingshop.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    // Lấy banner hiển thị cho khách hàng (Tự động lọc theo ngày và trạng thái ACTIVE)
    @Query("SELECT b FROM Banner b WHERE b.status = 'ACTIVE' " +
            "AND (:currentDate BETWEEN b.startDate AND b.endDate)")
    List<Banner> findActiveBanners(@Param("currentDate") LocalDate currentDate);
}