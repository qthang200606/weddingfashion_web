package com.aipo.weddingshop.service;

import com.aipo.weddingshop.entity.Banner;
import com.aipo.weddingshop.repository.BannerRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    // Cho Admin: Xem tất cả banner
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    // Cho Khách hàng: Chỉ xem banner đang kích hoạt và còn hạn
    public List<Banner> getActiveBannersForUser() {
        return bannerRepository.findActiveBanners(LocalDate.now());
    }

    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy banner với ID: " + id));
    }

    public Banner saveBanner(Banner banner) {
        // Tự động chuẩn hóa trạng thái mặc định nếu trống
        if (banner.getStatus() == null) {
            banner.setStatus("ACTIVE");
        }
        return bannerRepository.save(banner);
    }

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }
}