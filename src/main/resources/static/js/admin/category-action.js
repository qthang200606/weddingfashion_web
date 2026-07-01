/**
 * BRIDAL STUDIO - CATEGORY MODAL OPERATIONS
 */
document.addEventListener('DOMContentLoaded', function () {
    const categoryModalEl = document.getElementById('categoryModal');
    if (!categoryModalEl) return;

    const categoryModal = new bootstrap.Modal(categoryModalEl);
    const categoryForm = document.getElementById('categoryForm');

    // 🌟 ĐẢM BẢO ĐÃ KHAI BÁO BIẾN NÀY ĐỂ KHÔNG BỊ LỖI "not defined"
    const modalTitle = document.getElementById('categoryModalTitle');

    // 1. SỰ KIỆN: CLICK NÚT THÊM DANH MỤC MỚI
    document.getElementById('openAddCategoryBtn')?.addEventListener('click', function (e) {
        e.preventDefault();

        // Reset sạch form về trạng thái trống
        if (categoryForm) categoryForm.reset();

        const formId = document.getElementById('formCategoryId');
        if (formId) formId.value = '';

        // Cấu hình hành động lưu mới
        if (categoryForm) categoryForm.action = '/admin/categories/save';
        if (modalTitle) modalTitle.innerText = 'Thêm Danh Mục Mới';

        categoryModal.show();
    });

    // 2. SỰ KIỆN: CLICK NÚT SỬA DANH MỤC (LẤY DỮ LIỆU ĐỘNG)
    document.querySelectorAll('.btn-trigger-edit-category').forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();
            const categoryId = this.getAttribute('data-id');

            if (!categoryId || categoryId === 'undefined') {
                console.error("Lỗi: Nút bấm chưa có thuộc tính data-id!");
                return;
            }

            // Gọi REST API lấy dữ liệu thô dạng JSON từ Backend
            fetch('/admin/categories/api/' + categoryId)
                .then(response => {
                    if (!response.ok) throw new Error("Không thể tải thông tin danh mục.");
                    return response.json();
                })
                .then(data => {
                    // Tự động gán dữ liệu cũ lên các thẻ input trong Popup
                    const inputId = document.getElementById('formCategoryId');
                    const inputName = document.getElementById('formCategoryName');
                    const inputDesc = document.getElementById('formDescription');

                    if (inputId) inputId.value = data.categoryId;
                    if (inputName) inputName.value = data.categoryName;
                    if (inputDesc) inputDesc.value = data.description || '';

                    // Cấu hình đường dẫn submit và tiêu đề
                    if (categoryForm) categoryForm.action = '/admin/categories/save';

                    // 🌟 Kiểm tra biến an toàn trước khi gán text
                    if (modalTitle) {
                        modalTitle.innerText = 'Chỉnh Sửa Danh Mục #' + categoryId;
                    }

                    categoryModal.show();
                })
                .catch(err => alert(err.message));
        });
    });
});



