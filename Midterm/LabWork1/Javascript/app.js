const express = require('express');
const multer = require('multer');
const csv = require('csv-parser');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();

// Enable CORS (important for fetch from browser)
app.use(cors());

// Parse JSON bodies (good practice, even if not used right now)
app.use(express.json());

// Serve all static files from the project root folder
// (css, js, images, etc.)
app.use(express.static(__dirname));

// If you have a "templates" folder, serve it too
// (optional — only needed if you have other HTML files there)
app.use('/templates', express.static(path.join(__dirname, 'templates')));

// Multer setup – make sure 'uploads' folder exists!
const upload = multer({ dest: 'uploads/' });

// Main page – serve Ranking.html from the templates folder
app.get('/', (req, res) => {
    const filePath = path.join(__dirname, 'templates', 'Ranking.html');
    
    // Optional: log to see what path we're really trying
    console.log('Trying to serve:', filePath);
    
    res.sendFile(filePath, (err) => {
        if (err) {
            console.error('sendFile error:', err);
            res.status(404).send('Cannot find Ranking.html in /templates folder');
        }
    });
});

// Analyze endpoint (unchanged except better error logging)
app.post('/analyze', upload.single('csv'), (req, res) => {
    if (!req.file) {
        return res.status(400).json({ error: 'No file uploaded' });
    }

    const filePath = req.file.path;
    console.log('Processing uploaded file:', filePath);

    const aggregates = new Map();

    fs.createReadStream(filePath)
        .pipe(csv())
        .on('data', (row) => {
            const title = row.title?.trim();
            const platform = row.console?.trim();
            const genre = row.genre?.trim();
            const publisher = row.publisher?.trim();
            const totalSalesStr = row.total_sales?.trim();

            if (!title || !totalSalesStr) return;

            const totalSales = parseFloat(totalSalesStr);
            if (isNaN(totalSales)) return;

            if (!aggregates.has(title)) {
                aggregates.set(title, {
                    title,
                    platform: platform || 'N/A',
                    genre: genre || 'N/A',
                    publisher: publisher || 'N/A',
                    totalSales: 0
                });
            }

            aggregates.get(title).totalSales += totalSales;
        })
        .on('end', () => {
            const sorted = Array.from(aggregates.values())
                .sort((a, b) => b.totalSales - a.totalSales)
                .slice(0, 10);

            // Cleanup uploaded file
            fs.unlink(filePath, (err) => {
                if (err) console.error('Cleanup failed:', err);
            });

            res.json(sorted);
        })
        .on('error', (err) => {
            console.error('CSV stream error:', err);
            res.status(500).json({ error: 'Error reading CSV' });
        });
});

// Start server
const PORT = 4000;
app.listen(PORT, () => {
    console.log(`Server started → open this exact URL:`);
    console.log(`http://localhost:${PORT}`);
});