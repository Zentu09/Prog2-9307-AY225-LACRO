/**
 * =====================================================
 * Student Name    : ART LOUIE LACRO
 * Course          : Math 101 — Linear Algebra / BSCSIT 9307 - Programming 2
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : 3/16/26
 * GitHub Repo     : https://github.com/Zentu09/Prog2-9307-AY225-LACRO
 *
 * Description:
 *   This program computes the determinant of a hardcoded 3x3 matrix assigned
 *   to Art Louie Lacro for Math 101. The solution is computed using cofactor
 *   expansion along the first row. Each intermediate step (2x2 minor,
 *   cofactor term, running sum) is printed to the console in a readable format.
 * =====================================================
 */


public class DeterminantSolver {    

    // ── SECTION 1: computeMinor method ───────────────────────────────────    
    //method for computing the minor 2x2 determinant
    //variables from the main is passed as a argument to the method
    //only [R2C1], [R2C2], [R2C3], [R3C1], [R3C2], [R3C3]  are used
    public static int[] computeMinor(int D, int E, int F, 
                                    int G, int H, int I) {
        int MajorA = (E*I)-(H*F); 
        int MajorB = (D*I)-(G*F);
        int MajorC = (D*H)-(G*E);

        return new int[]{MajorA, MajorB, MajorC};
    } 

    // ── SECTION 2: solveDeterminant method ─────────────────────────────────── 
    //method for computing the cofactor after finding the 2x2 minor
    //in this time, [R1C1], [R1C2], [R1C3] will be passed in the argument along the computed 2x2 minor 
    //the returned value will be added again as a argument to be displayed at resultBar
    public static int solveDeterminant(int A, int B,int C, 
                                        int minorA, int minorB, int minorC) {
        return (A * minorA) - (B * minorB) + (C * minorC);
    }


    // ── SECTION 3: RESULT SECTION ─────────────────────────────────── 
    //in this sectiom, the determinant will be displayed along the step by step solution for the matrix
    //print display using for loops for showing the given matrix
    public static void resultsBar(int determinant) {
        String[] matrix = {
            "|  5  4  2  |",
            "|  3  1  6  |",
            "|  2  5  3  |"
        };

        System.out.println("=".repeat(52));
        System.out.println("3x3 MATRIX DETERMINATN SOLVER");
        System.out.println("Student: Art Louie R. Lacro");
        System.out.println("Asigned Matrix:");
        System.out.println("=".repeat(52));
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(matrix[i]); // prints each row
        }
        System.out.println("=".repeat(52));

        System.out.println("Expanding along Row 1 (cofactor expansion):");
        System.out.println("Step 1 - MinorA: det([1,6],[5,3]) = (1x3) - (5x6) = 3 - 30 = -27");
        System.out.println("Step 2 - MinorB: det([3,6],[2,3]) = (3x3) - (2x6) = 9 - 12 = -3");
        System.out.println("Step 3 - MinorC: det([3,1],[2,5]) = (3x5) - (2x1) = 15 - 2 = 13");
        System.out.println("\n");

        System.out.println("Cofactor A = 5 x MinorA = -135");
        System.out.println("Cofactor B = 4 x MinorB = -12");
        System.out.println("Cofactor B = 2 x MinorB = 26");
        System.out.println("\n");
        System.out.println("det(M) = (-135) - (-12) + (26)");
        System.out.println("\n");
        System.out.println("=".repeat(52));
        System.out.println("✅ DETERMINANT = " + determinant);
        System.out.println("=".repeat(52));
    }

    public static void main(String[] args) {
    // Matrix variables that will be used
    int A = 5, B = 4, C = 2;
    int D = 3, E = 1, F = 6;
    int G = 2, H = 5, I = 3;

    //will first get the minor 2x2 and use the computeMinor method to get results
    int[] minors = computeMinor(D, E, F, G, H, I); 

    //After getting the minor will use the array result from computeMinor and compute the final determinant
    int determinant = solveDeterminant(A, B, C, minors[0], minors[1], minors[2]);

    //will call the resultBar method and print the results inside
    resultsBar(determinant);        
    }
}