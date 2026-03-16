/**
 * =====================================================
 * Student Name    : ART LOUIE LACRO
 * Course          : Math 101 — Linear Algebra / BSCSIT 9307 - Programming 2
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : 3/16/26
 * GitHub Repo     : https://github.com/Zentu09/Prog2-9307-AY225-LACRO
 * * Runtime         : Node.js (run with: node determinant_solver.js)
 *
 * Description:
 *   JavaScript equivalent of DeterminantSolver.java. This script computes
 *   the determinant of the same hardcoded 3x3 matrix using cofactor expansion
 *   along the first row. All intermediate steps are logged to the console
 *   using console.log() for complete transparency of the solution process.
 * =====================================================
 */


// ── SECTION 1: computeMinor function ───────────────────────────────────   
//variables from the main is passed as a argument to the method
function computeMinor(D, E, F, G, H, I) {
    let MinorA = (E * I) - (H * F);
    let MinorB = (D * I) - (G * F);
    let MinorC = (D * H) - (G * E);

    return [MinorA, MinorB, MinorC];
}


// ── SECTION 2: solveDeterminant function ─────────────────────────────────── 
// Solve the determinant using cofactor expansion
//in this time, [R1C1], [R1C2], [R1C3] will be passed in the argument along the computed 2x2 minor 
function solveDeterminant(A, B, C, minorA, minorB, minorC) {
    return (A * minorA) - (B * minorB) + (C * minorC);
}


// ── SECTION 3: RESULT SECTION ─────────────────────────────────── 
// Print the matrix and step-by-step explanation
function resultsBar(determinant) {
    let matrix = [
        "|  5  4  2  |",
        "|  3  1  6  |",
        "|  2  5  3  |"
    ];

    console.log("===================================================");
    console.log("3x3 MATRIX DETERMINANT SOLVER");
    console.log("Student: Art Louie R. Lacro");
    console.log("Assigned Matrix:");
    console.log("===================================================");
    matrix.forEach(row => console.log(row));
    console.log("===================================================");

    console.log("Expanding along Row 1 (cofactor expansion):");
    console.log("Step 1 - MinorA: det([1,6],[5,3]) = (1x3) - (5x6) = 3 - 30 = -27");
    console.log("Step 2 - MinorB: det([3,6],[2,3]) = (3x3) - (2x6) = 9 - 12 = -3");
    console.log("Step 3 - MinorC: det([3,1],[2,5]) = (3x5) - (2x1) = 15 - 2 = 13");
    console.log("\n");

    console.log("Cofactor A = 5 x MinorA = -135");
    console.log("Cofactor B = 4 x MinorB = -12");
    console.log("Cofactor C = 2 x MinorC = 26");
    console.log("\n");
    console.log("det(M) = (-135) - (-12) + (26)");
    console.log("\n");
    console.log("===================================================");
    console.log("✅ DETERMINANT = " + determinant);
    console.log("===================================================");
}
//=========================
// Main program
//=========================

// Matrix variables that will be used
let A = 5, B = 4, C = 2;
let D = 3, E = 1, F = 6;
let G = 2, H = 5, I = 3;

//will first get the minor 2x2 and use the computeMinor method to get results
let minors = computeMinor(D, E, F, G, H, I);

//After getting the minor will use the array result from computeMinor and compute the final determinant
let determinant = solveDeterminant(A, B, C, minors[0], minors[1], minors[2]);

//will call the resultBar method and print the results inside
resultsBar(determinant);