document.addEventListener('DOMContentLoaded', () => {

    // ==================== VARIABLE DECLARATIONS ====================
    
    // DOM Elements
    const fileInput = document.getElementById('csvFile');        // Hidden file input element
    const columnSelect = document.getElementById('columnSelect'); // Dropdown for selecting column
    const maxRowsInput = document.getElementById('maxRows');     // Input field for number of rows to show
    const processBtn = document.getElementById('processBtn');    // Button to start processing
    const outputArea = document.getElementById('outputArea');    // Area where results are displayed

    // Data Storage Variables
    let csvData = [];     // Stores all data rows (after skipping metadata)
    let headers = [];     // Stores the header row columns


    // ==================== FILE UPLOAD HANDLING ====================

    // Triggered when user selects a CSV file
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        
        // When file is successfully read
        reader.onload = function(event) {
            const text = event.target.result;
            parseCSV(text);           // Pass file content to parser
        };
        
        reader.readAsText(file);      // Read file as plain text
    });


    // ==================== CSV PARSING FUNCTION ====================

    /**
     * Parses the uploaded CSV text.
     * Skips first 6 metadata lines and reads header from line 7.
     */
    function parseCSV(text) {
        const lines = text.split(/\r?\n/);   // Split text into array of lines
        
        const headerLineIndex = 7;           // Header is on the 7th line (1-based index)

        // Safety check: ensure file has enough lines
        if (lines.length < headerLineIndex) {
            outputArea.textContent = "Error: CSV file is too short or missing header row.";
            return;
        }

        // Extract header line and parse it
        const headerLine = lines[headerLineIndex - 1];
        headers = splitCsvLine(headerLine);

        // Populate column dropdown (skipping empty columns and "Column1")
        columnSelect.innerHTML = '';
        let added = 0;

        headers.forEach((col, index) => {
            const cleaned = col.trim();
            // Skip empty column names and placeholder "Column1"
            if (cleaned === '' || cleaned.toLowerCase() === 'column1') return;

            const option = document.createElement('option');
            option.value = index;
            option.textContent = cleaned || `Column ${index}`;
            columnSelect.appendChild(option);
            added++;
        });

        // Fallback if no valid columns found
        if (added === 0) {
            const option = document.createElement('option');
            option.value = 0;
            option.textContent = "Column 0";
            columnSelect.appendChild(option);
        }

        // Store data rows (everything after header)
        csvData = lines.slice(headerLineIndex);

        // Show success message
        outputArea.textContent = `CSV loaded successfully!\nDetected ${headers.length} columns.\nReady to process.`;
    }


    // ==================== CSV LINE SPLITTER ====================

    /**
     * Splits a single CSV line into columns.
     * Handles quoted fields and escaped quotes properly.
     */
    function splitCsvLine(line) {
        const result = [];
        let current = '';
        let inQuotes = false;

        for (let i = 0; i < line.length; i++) {
            const char = line[i];

            if (char === '"') {
                // Handle escaped quotes ("")
                if (inQuotes && line[i + 1] === '"') {
                    current += '"';
                    i++;
                } else {
                    inQuotes = !inQuotes;   // Toggle quote state
                }
            } 
            else if (char === ',' && !inQuotes) {
                result.push(current);       // End of current column
                current = '';
            } 
            else {
                current += char;            // Add character to current column
            }
        }
        result.push(current);               // Push the last column
        return result;
    }


    // ==================== MAIN PROCESSING LOGIC ====================

    /**
     * Triggered when "Process CSV" button is clicked.
     * Performs analysis on the selected column.
     */
    processBtn.addEventListener('click', () => {
        // Check if file has been loaded
        if (csvData.length === 0) {
            outputArea.textContent = "Please upload a CSV file first.";
            return;
        }

        // Get user selections
        const selectedIndex = parseInt(columnSelect.value);
        let maxRows = parseInt(maxRowsInput.value) || 25;
        if (maxRows < 1) maxRows = 25;

        // Get column name for display
        const columnName = headers[selectedIndex]?.trim() || `Column ${selectedIndex}`;

        // Processing variables
        let validRows = 0;
        const extractedValues = [];     // Stores all non-empty values from selected column
        const uniqueValues = new Set(); // Stores unique values (automatically removes duplicates)

        // ==================== DATASET PROCESSING LOOP ====================
        // Loop through every data row
        csvData.forEach(line => {
            if (!line.trim()) return;                    // Skip empty lines

            const columns = splitCsvLine(line);          // Split row into columns

            // Safety check: make sure column exists
            if (selectedIndex >= columns.length) return;

            const value = columns[selectedIndex].trim();

            // Skip empty values
            if (!value) return;

            // Collect data
            validRows++;
            extractedValues.push(value);
            uniqueValues.add(value);                     // Set automatically handles uniqueness
        });

        // ==================== BUILD HTML OUTPUT ====================
        let html = `
            <h2 class="summa">=== CSV Summary ===</h2>

            <table class="summary-table">
                <tr><td><strong>Selected Column</strong></td><td>${columnName}</td></tr>
                <tr><td><strong>Total Valid Rows</strong></td><td>${validRows} (excluding empty)</td></tr>
                <tr><td><strong>Unique Values Count</strong></td><td>${uniqueValues.size}</td></tr>
            </table>

            <h4 style="margin:25px 0 12px 0;">First ${Math.min(maxRows, extractedValues.length)} Extracted Values</h4>
            <div class="table-container">
                <table class="data-table">
                    <thead><tr><th>#</th><th>Value</th></tr></thead>
                    <tbody>
        `;

        // Add extracted values
        const showCount = Math.min(maxRows, extractedValues.length);
        for (let i = 0; i < showCount; i++) {
            html += `<tr><td>${i+1}</td><td>${extractedValues[i]}</td></tr>`;
        }

        html += `</tbody></table></div>`;

        if (extractedValues.length > maxRows) {
            html += `<p style="text-align:center; color:#666; margin:10px 0;">... showing first ${maxRows} values ...</p>`;
        }

        // Add ALL unique values
        html += `
            <h4 style="margin:35px 0 12px 0;">All Unique Values (${uniqueValues.size})</h4>
            <div class="table-container unique-table">
                <table class="data-table">
                    <thead><tr><th>#</th><th>Unique Value</th></tr></thead>
                    <tbody>
        `;

        let idx = 1;
        for (let val of uniqueValues) {
            html += `<tr><td>${idx++}</td><td>${val}</td></tr>`;
        }

        html += `</tbody></table></div>`;

        // Display final result
        outputArea.innerHTML = html;
    });

});