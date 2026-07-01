document.addEventListener("DOMContentLoaded", function() {
    const dateInput = document.getElementById('modalAppointmentDate');
    if(dateInput) dateInput.min = new Date().toISOString().split('T')[0];

    // Hàm lấy danh sách lịch hẹn của tài khoản đang đăng nhập
    function loadMyAppointments() {
        const container = document.getElementById('appointmentListContainer');
        if (!container) return;

        fetch('/appointments/api/my-list')
        .then(res => res.json())
        .then(data => {
            const appCountEl = document.getElementById('appCount');
            if (appCountEl) appCountEl.innerText = data.length;

            if(data.length === 0) {
                container.innerHTML = '<div class="text-center py-5 text-muted small"><i class="fa-regular fa-calendar-times fs-2 mb-2 d-block text-black-50"></i>Nàng chưa có lịch hẹn thử váy nào.</div>';
                return;
            }

            let html = '';
            data.forEach(app => {
                let badgeClass = 'badge-pending';
                let statusText = 'Chờ duyệt';
                if(app.status === 'CONFIRMED') { badgeClass = 'badge-confirmed'; statusText = 'Đã xác nhận'; }
                else if(app.status === 'CANCELLED') { badgeClass = 'badge-cancelled'; statusText = 'Đã hủy'; }

                // Định dạng hiển thị ngày giờ từ ISO
                let dateObj = new Date(app.appointmentDate);
                let formattedTime = dateObj.toLocaleTimeString('vi-VN', {hour: '2-digit', minute:'2-digit'}) + ' - ' + dateObj.toLocaleDateString('vi-VN', {day: '2-digit', month: '2-digit', year: 'numeric'});

                html += `
                    <div class="app-history-card">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="fw-bold text-dark fs-6"><i class="fa-regular fa-clock text-muted me-1"></i> ${formattedTime}</span>
                            <span class="badge ${badgeClass} px-2 py-1 rounded small">${statusText}</span>
                        </div>
                        <p class="m-0 text-muted small"><i class="fa-regular fa-user me-1"></i> Khách hàng: <span class="text-dark fw-medium">${app.fullName}</span></p>
                        ${app.note ? `<p class="m-0 text-muted small mt-1"><i class="fa-regular fa-comment-dots me-1"></i> Ghi chú: <span class="text-secondary">${app.note}</span></p>` : ''}
                    </div>
                `;
            });
            container.innerHTML = html;
        })
        .catch(() => {
            container.innerHTML = '<div class="text-center py-3 text-danger small">Không thể lấy dữ liệu lịch hẹn!</div>';
        });
    }

    // Tự động tải danh sách lịch khi click mở Modal hoặc bấm chuyển sang Tab Lịch Hẹn
    const btnOpenModalHub = document.getElementById('btnOpenModalHub');
    const myAppointmentsTab = document.getElementById('my-appointments-tab');

    if (btnOpenModalHub) btnOpenModalHub.addEventListener('click', loadMyAppointments);
    if (myAppointmentsTab) myAppointmentsTab.addEventListener('click', loadMyAppointments);

    // Gửi Form Đặt Lịch Mới bằng AJAX
    const form = document.getElementById('ajaxAppointmentForm');
    const alertBox = document.getElementById('modalAlert');
    const btnSubmit = document.getElementById('btnSubmitModal');

    if(form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            btnSubmit.disabled = true;
            btnSubmit.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Đang gửi...';

            fetch('/appointments/api/book', { method: 'POST', body: new FormData(form) })
            .then(res => {
                if (res.redirected) { window.location.href = res.url; return; }
                return res.json();
            })
            .then(data => {
                alertBox.classList.remove('d-none', 'alert-danger', 'alert-success');
                if (data && data.status === 'success') {
                    alertBox.classList.add('alert-success');
                    alertBox.innerHTML = `<i class="fa-regular fa-circle-check me-1"></i> ${data.message}`;
                    form.reset();
                    loadMyAppointments(); // Làm mới lại số lượng và danh sách tab bên cạnh ngay lập tức
                } else {
                    alertBox.classList.add('alert-danger');
                    alertBox.innerHTML = `<i class="fa-solid fa-triangle-exclamation me-1"></i> ${data.message}`;
                }
            })
            .catch(() => {
                alertBox.classList.add('alert-danger');
                alertBox.innerHTML = 'Lỗi kết nối hệ thống!';
            })
            .finally(() => {
                btnSubmit.disabled = false;
                btnSubmit.innerHTML = '<i class="fa-regular fa-paper-plane me-1"></i> Xác nhận gửi lịch hẹn';
            });
        });
    }
});