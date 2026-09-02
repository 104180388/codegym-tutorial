document.addEventListener('DOMContentLoaded', function () {
    // Dynamic Invoice total recalculation for Staff Billing
    const serviceFeeInput = document.getElementById('serviceFee');
    const surchargeInput = document.getElementById('surcharge');
    const discountInput = document.getElementById('discount');
    const totalAmountDisplay = document.getElementById('totalAmountDisplay');

    function calculateInvoiceTotal() {
        if (!serviceFeeInput || !totalAmountDisplay) return;

        const fee = parseFloat(serviceFeeInput.value) || 0;
        const surcharge = parseFloat(surchargeInput ? surchargeInput.value : 0) || 0;
        const discount = parseFloat(discountInput ? discountInput.value : 0) || 0;

        let total = fee + surcharge - discount;
        if (total < 0) total = 0;

        totalAmountDisplay.textContent = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(total);
    }

    if (serviceFeeInput) serviceFeeInput.addEventListener('input', calculateInvoiceTotal);
    if (surchargeInput) surchargeInput.addEventListener('input', calculateInvoiceTotal);
    if (discountInput) discountInput.addEventListener('input', calculateInvoiceTotal);

    // Auto dismiss alert after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
});
