document.addEventListener("DOMContentLoaded", function () {
  // Xử lý form báo cáo
  document.querySelectorAll('form[action="/report-post"]').forEach((form) => {
    form.addEventListener("submit", function (e) {
      e.preventDefault();

      const postId = this.querySelector('input[name="postId"]').value;
      const reason = this.querySelector('textarea[name="reason"]').value;

      if (!reason.trim()) {
        alert("Vui lòng nhập lý do báo cáo");
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
          const toast = document.createElement("div");
          toast.className =
            "alert alert-success position-fixed top-0 end-0 m-3";
          toast.style.zIndex = "1055";
          toast.textContent = response;
          document.body.appendChild(toast);

          // Tự động ẩn sau 3 giây
          setTimeout(() => toast.remove(), 3000);
        })
        .catch((error) => {
          alert("Có lỗi xảy ra khi gửi báo cáo: " + error);
        });
    });
  });
});
