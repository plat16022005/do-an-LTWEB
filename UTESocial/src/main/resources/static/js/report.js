document.addEventListener("DOMContentLoaded", function () {
  // Hàm hiển thị toast notification
  function showToast(message, type = "success") {
    const toast = document.createElement("div");
    toast.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
    toast.style.zIndex = "1055";
    toast.style.minWidth = "300px";
    toast.innerHTML = `
      <strong>${
        type === "success" ? "✓" : type === "danger" ? "✗" : "ℹ"
      }</strong> ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(toast);

    // Tự động ẩn sau 4 giây
    setTimeout(() => {
      toast.classList.remove("show");
      setTimeout(() => toast.remove(), 150);
    }, 4000);
  }

  // Xử lý form báo cáo bài viết
  document.querySelectorAll('form[action="/report-post"]').forEach((form) => {
    form.addEventListener("submit", function (e) {
      e.preventDefault();

      const postId = this.querySelector('input[name="postId"]').value;
      const reason = this.querySelector('textarea[name="reason"]').value;

      if (!reason.trim()) {
        showToast("Vui lòng nhập lý do báo cáo", "warning");
        return;
      }

      fetch("/report-post", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `postId=${postId}&reason=${encodeURIComponent(reason)}`,
      })
        .then((res) => res.text())
        .then((response) => {
          // Đóng modal
          const modal = bootstrap.Modal.getInstance(this.closest(".modal"));
          modal.hide();

          // Reset form
          this.reset();

          // Hiển thị thông báo
          showToast(response, "success");
        })
        .catch((error) => {
          showToast("Có lỗi xảy ra khi gửi báo cáo: " + error, "danger");
        });
    });
  });

  // Xử lý form báo cáo người dùng
  const reportUserForm = document.getElementById("reportUserForm");
  if (reportUserForm) {
    reportUserForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const username = this.querySelector('input[name="username"]').value;
      const reason = this.querySelector('textarea[name="reason"]').value;

      if (!reason.trim()) {
        showToast("Vui lòng nhập lý do báo cáo", "warning");
        return;
      }

      fetch("/report-user", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `username=${encodeURIComponent(
          username
        )}&reason=${encodeURIComponent(reason)}`,
      })
        .then((res) => {
          if (res.ok) {
            return res.text();
          } else {
            return res.text().then((text) => {
              throw new Error(text);
            });
          }
        })
        .then((response) => {
          // Đóng modal
          const modal = bootstrap.Modal.getInstance(this.closest(".modal"));
          modal.hide();

          // Reset form
          this.reset();

          // Hiển thị thông báo thành công
          showToast(response, "success");
        })
        .catch((error) => {
          // Hiển thị thông báo lỗi
          showToast(error.message || "Có lỗi xảy ra khi gửi báo cáo", "danger");
        });
    });
  }
});
