/**
 * BRIDAL STUDIO - PRODUCT MODAL OPERATOR CONTROL WITH IMAGE PREVIEW
 */
document.addEventListener('DOMContentLoaded', function () {
    const productModalEl = document.getElementById('productModal');
    if (!productModalEl) return;

    const productModal = new bootstrap.Modal(productModalEl);
    const productForm = document.getElementById('productForm');
    const modalTitle = document.getElementById('productModalTitle');
    const previewImg = document.getElementById('preview');

    // 1. KHI BẤM NÚT: THÊM MỚI SẢN PHẨM
    document.getElementById('openAddProductBtn')?.addEventListener('click', function (e) {
        e.preventDefault();

        // Reset form và ẩn ảnh preview cũ đi
        productForm.reset();
        document.getElementById('formProductId').value = '';
        if (previewImg) {
            previewImg.src = '';
            previewImg.style.display = 'none';
        }

        // Trỏ đường dẫn form về link save của bạn
        productForm.action = '/admin/products/save';
        modalTitle.innerText = 'Thêm Sản Phẩm Váy Cưới Mới';

        productModal.show();
    });

    // 2. KHI BẤM NÚT: SỬA SẢN PHẨM (ĐỔ DATA BẰNG AJAX)
    document.querySelectorAll('.btn-trigger-edit-product').forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();
            const productId = this.getAttribute('data-id');

            // Gọi AJAX lấy chi tiết thông tin sản phẩm
            fetch('/admin/products/api/' + productId)
                .then(response => {
                    if (!response.ok) throw new Error("Không thể lấy dữ liệu sản phẩm.");
                    return response.json();
                })
                .then(data => {
                    // Đổ các trường dữ liệu text/number cơ bản vào form popup
                    document.getElementById('formProductId').value = data.productId;
                    document.getElementById('formProductName').value = data.productName;
                    document.getElementById('formPrice').value = data.price;
                    document.getElementById('formStockQuantity').value = data.stockQuantity;
                    document.getElementById('formDescription').value = data.description || '';

                    // Khớp danh mục được chọn (Category)
                    if (data.category) {
                        document.getElementById('formCategory').value = data.category.categoryId;
                    } else {
                        document.getElementById('formCategory').value = '';
                    }

                    // Xử lý hiển thị lại ảnh cũ đang có trên hệ thống vào ô Preview
                    if (data.imageUrl && previewImg) {
                        previewImg.src = data.imageUrl;
                        previewImg.style.display = 'block';
                    } else if (previewImg) {
                        previewImg.style.display = 'none';
                    }

                    // Đổi action form hướng về đường dẫn lưu sửa đổi của bạn
                    productForm.action = '/admin/products/save'; // form của bạn dùng chung /save xử lý cả lưu mới lẫn update dựa vào productId
                    modalTitle.innerText = 'Cập Nhật Sản Phẩm #' + productId;

                    productModal.show();
                })
                .catch(err => alert(err.message));
        });
    });
});