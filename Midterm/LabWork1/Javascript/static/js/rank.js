// render a list of result objects (top10) into the table and optionally save to storage
function renderTable(data) {
    const table = document.getElementById('resultsTable');
    const tbody = table.querySelector('tbody');
    tbody.innerHTML = '';

    data.forEach((item, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${item.title}</td>
            <td>${item.platform}</td>
            <td>${item.genre}</td>
            <td>${item.publisher}</td>
            <td>${item.totalSales.toFixed(2)} M</td>
        `;
        tbody.appendChild(row);
    });

    table.style.display = 'table';
    // persist results so refresh will show them
    try {
        localStorage.setItem('top10Data', JSON.stringify(data));
    } catch (_) {}
}

// restore from storage on load
window.addEventListener('DOMContentLoaded', () => {
    const saved = localStorage.getItem('top10Data');
    if (saved) {
        try {
            const data = JSON.parse(saved);
            if (Array.isArray(data) && data.length > 0) {
                renderTable(data);
            }
        } catch (_) {}
    }
});

document.getElementById('uploadBtn').addEventListener('click', async () => {
    const fileInput = document.getElementById('csvFile');
    const file = fileInput.files[0];
    const errorAlert = document.getElementById('errorAlert');
    const table = document.getElementById('resultsTable');
    const tbody = table.querySelector('tbody');
    const uploadBtn = document.getElementById('uploadBtn');

    errorAlert.style.display = 'none';
    table.style.display = 'none';
    tbody.innerHTML = '';  // Clear previous results

    if (!file) {
        errorAlert.textContent = 'Please select a CSV file.';
        errorAlert.style.display = 'block';
        return;
    }

    if (!file.name.endsWith('.csv')) {
        errorAlert.textContent = 'Please select a valid CSV file.';
        errorAlert.style.display = 'block';
        return;
    }

    // Show loading
    uploadBtn.classList.add('loading');
    uploadBtn.disabled = true;

    const formData = new FormData();
    formData.append('csv', file);

    try {
        const response = await fetch('/analyze', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        const data = await response.json();

        if (data.length === 0) {
            errorAlert.textContent = 'No valid data found in the CSV.';
            errorAlert.style.display = 'block';
            return;
        }

        // Fill table and save
        renderTable(data);
    } catch (err) {
        errorAlert.textContent = err.message || 'Error processing file.';
        errorAlert.style.display = 'block';
    } finally {
        // Hide loading
        uploadBtn.classList.remove('loading');
        uploadBtn.disabled = false;
    }
});