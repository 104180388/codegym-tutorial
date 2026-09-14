// Modal and interaction handling
document.addEventListener('DOMContentLoaded', function () {
    // Delete Modal controls
    const deleteModal = document.getElementById('deleteModal');
    const openModalBtn = document.getElementById('openDeleteModalBtn');
    const cancelModalBtn = document.getElementById('cancelDeleteBtn');

    if (openModalBtn && deleteModal) {
        openModalBtn.addEventListener('click', function () {
            deleteModal.classList.add('show');
        });
    }

    if (cancelModalBtn && deleteModal) {
        cancelModalBtn.addEventListener('click', function () {
            deleteModal.classList.remove('show');
        });
    }

    // Close modal when clicking outside
    if (deleteModal) {
        deleteModal.addEventListener('click', function (e) {
            if (e.target === deleteModal) {
                deleteModal.classList.remove('show');
            }
        });
    }

    // Auto dismiss alert after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(function () {
                alert.remove();
            }, 500);
        }, 5000);
    });
});
