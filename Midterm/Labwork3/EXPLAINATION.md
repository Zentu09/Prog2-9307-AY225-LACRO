# Program Logic Explanations
---

## CSV Column Analyzer (GUI Version)

*Program Logic:*
This program is a user-friendly desktop application with a graphical interface that helps students and professors easily analyze one specific column from a CSV dataset. The program opens a window where the user can browse and select their CSV file. It automatically skips the first 6 lines of metadata that are common in many research datasets. After the file is selected, the program reads the header line and loads only the meaningful column names into a dropdown menu (it skips empty columns and placeholder names like "Column1"). The user then chooses one column from the dropdown, types how many rows they want to preview (default is 25), and clicks the "Process CSV" button. The program reads through the entire file, counts all valid (non-empty) rows in the selected column, collects all the values, and identifies the unique values. Finally, it displays a clean summary in the output area showing the column name, total valid rows, number of unique values, the first requested number of values with numbers, and the full list of unique values. Everything is done with simple buttons and menus so even beginners can use it without typing commands.

---

## Key Features Explained

*Program Logic:*
The application performs three main tasks that are very useful when working with real CSV datasets:

1. **Count valid rows excluding empty rows**  
   The program goes through every row in the selected column, ignores completely empty cells or rows that only have spaces, and counts only the rows that actually contain data. This gives a more accurate "real data" count instead of just counting total lines.

2. **Extract and display a selected column**  
   After the user picks a column from the dropdown, the program reads the entire CSV file again (skipping the metadata), finds the correct column position (even if some columns were hidden), and pulls out all the values from that column. It then shows the first X values (whatever number the user entered) nicely numbered so it's easy to read and check the data.

3. **Display unique values in a column**  
   While reading the column, the program also keeps track of every different value that appears using a Set (which automatically removes duplicates). At the end, it lists all the unique values with numbers. This is very helpful to quickly see what different answers or categories exist in that column without scrolling through hundreds of repeated entries.

---

## How the Program Works Step by Step (Student View)

*Program Logic: JAVA*
- The window has a "Browse CSV" button that opens the normal file picker.
- Once a file is chosen, the program skips the first 6 metadata lines and reads the real column headers.
- Only clean column names appear in the dropdown list.
- When "Process CSV" is clicked, the program re-opens the file, skips metadata again, and carefully finds the exact column the user selected.
- It reads line by line, splits each line properly (even when there are commas inside quotes), and collects values from the chosen column.
- Empty values are ignored when counting valid rows.
- All values are stored to show the preview, and unique values are collected automatically.
- A nice formatted report is built and shown in the big text box in the middle of the window.
- If anything goes wrong (like no file selected), friendly warning messages pop up.



*Program Logic: JAVASCRIPT (Browser Version)*
- The web page has a "Browse CSV" button that lets the user select a file directly from their computer using the browser's file input.
- Once a file is chosen, the program reads the file using the File API and FileReader (no server needed).
- It skips the first 6 metadata lines and reads the 7th line to get the column headers.
- Only clean and meaningful column names are added to the dropdown list (empty columns and "Column1" are skipped).
- When the user clicks the "Analyze Column" button, the program re-reads the entire file from the beginning.
- It skips the first 6 metadata lines again, then processes every remaining line.
- Each line is split properly using a smart CSV parser that handles commas inside quotes.
- The program finds the exact column the user selected from the dropdown and extracts its values.
- Empty values are ignored when counting valid rows.
- All non-empty values from the selected column are stored to show the preview, and a Set is used to automatically collect unique values.
- A clean and well-formatted summary is built, including the column name, total valid rows, number of unique values, first X preview values, and the full list of unique values.
- Everything is displayed nicely in a big output area on the webpage.


This design makes it very safe and easy to explore different columns in large or messy CSV files without breaking anything or getting confused by the extra header lines at the top.