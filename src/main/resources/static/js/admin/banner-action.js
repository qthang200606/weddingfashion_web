/**
 * BRIDAL STUDIO - BANNER POPUP OPERATOR CONTROL
 */
document.addEventListener('DOMContentLoaded', function () {
    const bannerModalEl = document.getElementById('bannerModal');
    if (!bannerModalEl) return;

    // SỬA TẠI ĐÂY: Thay 'Bootstrap.Modal' thành 'bootstrap.Modal' (chữ b viết thường)
    const bannerModal = new bootstrap.Modal(bannerModalEl);
    const bannerForm = document.getElementById('bannerForm');
    const modalTitle = document.getElementById('bannerModalTitle');

    // 1. TRƯỜNG HỢP: BẤM NÚT THÊM MỚI BANNER
    document.getElementById('openAddBannerBtn')?.addEventListener('click', function (e) {
        e.preventDefault();

        // Làm sạch toàn bộ dữ liệu cũ trong form dữ liệu
        bannerForm.reset();
        document.getElementById('formBannerId').value = '';

        // Cấu hình lại hành động đẩy dữ liệu của Form về API Lưu mới
        bannerForm.action = '/admin/banners/save';
        modalTitle.innerText = 'Thêm Banner Quảng Cáo Mới';

        bannerModal.show();
    });

    // 2. TRƯỜNG HỢP: BẤM NÚT CHỈNH SỬA BANNER
    document.querySelectorAll('.btn-trigger-edit').forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();
            const bannerId = this.getAttribute('data-id');

            // Gọi AJAX lấy thông tin chi tiết Banner từ Server để điền vào Form Popup
            fetch('/admin/api/banners/' + bannerId)
                .then(response => {
                    if (!response.ok) throw new Error("Không thể tải thông tin banner.");
                    return response.json();
                })
                .then(data => {
                    // Đổ dữ liệu trả về vào các ô Input tương ứng
                    document.getElementById('formBannerId').value = data.bannerId;
                    document.getElementById('formTitle').value = data.title;
                    document.getElementById('formImageUrl').value = data.imageUrl;
                    document.getElementById('formContent').value = data.content;

                    if(data.startDate) document.getElementById('formStartDate').value = data.startDate.split('T')[0];
                    if(data.endDate) document.getElementById('formEndDate').value = data.endDate.split('T')[0];
                    
                    document.getElementById('formStatus').value = data.status;

                    // Đổi thông số hành động Form sang API cập nhật
                    bannerForm.action = '/admin/banners/update';
                    modalTitle.innerText = 'Chỉnh Sửa Cấu Hình Banner #' + bannerId;

                    // Kích hoạt hiển thị Popup lên màn hình
                    bannerModal.show();
                })
                .catch(err => alert(err.message));
        });
    });
});