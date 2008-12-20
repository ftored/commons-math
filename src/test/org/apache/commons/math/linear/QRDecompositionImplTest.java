/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.linear;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class QRDecompositionImplTest extends TestCase {
    double[][] testData3x3NonSingular = { 
            { 12, -51, 4 }, 
            { 6, 167, -68 },
            { -4, 24, -41 }, };

    double[][] testData3x3Singular = { 
            { 1, 4, 7, }, 
            { 2, 5, 8, },
            { 3, 6, 9, }, };

    double[][] testData3x4 = { 
            { 12, -51, 4, 1 }, 
            { 6, 167, -68, 2 },
            { -4, 24, -41, 3 }, };

    double[][] testData4x3 = { 
            { 12, -51, 4, }, 
            { 6, 167, -68, },
            { -4, 24, -41, }, 
            { -5, 34, 7, }, };

    private static final double entryTolerance = 10e-16;

    private static final double normTolerance = 10e-14;

    public QRDecompositionImplTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(QRDecompositionImplTest.class);
        suite.setName("QRDecompositionImpl Tests");
        return suite;
    }

    /** test dimensions */
    public void testDimensions() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        QRDecomposition qr = new QRDecompositionImpl(matrix);
        assertEquals("3x3 Q size", qr.getQ().getRowDimension(), 3);
        assertEquals("3x3 Q size", qr.getQ().getColumnDimension(), 3);
        assertEquals("3x3 R size", qr.getR().getRowDimension(), 3);
        assertEquals("3x3 R size", qr.getR().getColumnDimension(), 3);

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        qr = new QRDecompositionImpl(matrix);
        assertEquals("4x3 Q size", qr.getQ().getRowDimension(), 4);
        assertEquals("4x3 Q size", qr.getQ().getColumnDimension(), 4);
        assertEquals("4x3 R size", qr.getR().getRowDimension(), 4);
        assertEquals("4x3 R size", qr.getR().getColumnDimension(), 3);

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        qr = new QRDecompositionImpl(matrix);
        assertEquals("3x4 Q size", qr.getQ().getRowDimension(), 3);
        assertEquals("3x4 Q size", qr.getQ().getColumnDimension(), 3);
        assertEquals("3x4 R size", qr.getR().getRowDimension(), 3);
        assertEquals("3x4 R size", qr.getR().getColumnDimension(), 4);
    }

    /** test A = QR */
    public void testAEqualQR() {
        RealMatrix A = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        QRDecomposition qr = new QRDecompositionImpl(A);
        RealMatrix Q = qr.getQ();
        RealMatrix R = qr.getR();
        double norm = Q.multiply(R).subtract(A).getNorm();
        assertEquals("3x3 nonsingular A = QR", 0, norm, normTolerance);

        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        qr = new QRDecompositionImpl(matrix);
        norm = qr.getQ().multiply(qr.getR()).subtract(matrix).getNorm();
        assertEquals("3x3 singular A = QR", 0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        qr = new QRDecompositionImpl(matrix);
        norm = qr.getQ().multiply(qr.getR()).subtract(matrix).getNorm();
        assertEquals("3x4 A = QR", 0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        qr = new QRDecompositionImpl(matrix);
        norm = qr.getQ().multiply(qr.getR()).subtract(matrix).getNorm();
        assertEquals("4x3 A = QR", 0, norm, normTolerance);
    }

    /** test the orthogonality of Q */
    public void testQOrthogonal() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        RealMatrix q  = new QRDecompositionImpl(matrix).getQ();
        RealMatrix qT = new QRDecompositionImpl(matrix).getQT();
        RealMatrix eye = MatrixUtils.createRealIdentityMatrix(3);
        double norm = qT.multiply(q).subtract(eye).getNorm();
        assertEquals("3x3 nonsingular Q'Q = I", 0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        q  = new QRDecompositionImpl(matrix).getQ();
        qT = new QRDecompositionImpl(matrix).getQT();
        eye = MatrixUtils.createRealIdentityMatrix(3);
        norm = qT.multiply(q).subtract(eye).getNorm();
        assertEquals("3x3 singular Q'Q = I", 0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        q  = new QRDecompositionImpl(matrix).getQ();
        qT = new QRDecompositionImpl(matrix).getQT();
        eye = MatrixUtils.createRealIdentityMatrix(3);
        norm = qT.multiply(q).subtract(eye).getNorm();
        assertEquals("3x4 Q'Q = I", 0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        q  = new QRDecompositionImpl(matrix).getQ();
        qT = new QRDecompositionImpl(matrix).getQT();
        eye = MatrixUtils.createRealIdentityMatrix(4);
        norm = qT.multiply(q).subtract(eye).getNorm();
        assertEquals("4x3 Q'Q = I", 0, norm, normTolerance);
    }

    /** test that R is upper triangular */
    public void testRUpperTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        RealMatrix R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        R = new QRDecompositionImpl(matrix).getR();
        for (int i = 0; i < R.getRowDimension(); i++)
            for (int j = 0; j < i; j++)
                assertEquals("R lower triangle", R.getEntry(i, j), 0,
                        entryTolerance);
    }

    /** test that H is trapezoidal */
    public void testHTrapezoidal() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData3x3NonSingular);
        RealMatrix H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

        matrix = MatrixUtils.createRealMatrix(testData3x3Singular);
        H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

        matrix = MatrixUtils.createRealMatrix(testData3x4);
        H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

        matrix = MatrixUtils.createRealMatrix(testData4x3);
        H = new QRDecompositionImpl(matrix).getH();
        for (int i = 0; i < H.getRowDimension(); i++)
            for (int j = i + 1; j < H.getColumnDimension(); j++)
                assertEquals(H.getEntry(i, j), 0, entryTolerance);

    }

    /** test matrices values */
    public void testMatricesValues() {
        QRDecomposition qr =
            new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular));
        RealMatrix qRef = MatrixUtils.createRealMatrix(new double[][] {
                { -12.0 / 14.0,   69.0 / 175.0,  -58.0 / 175.0 },
                {  -6.0 / 14.0, -158.0 / 175.0,    6.0 / 175.0 },
                {   4.0 / 14.0,  -30.0 / 175.0, -165.0 / 175.0 }
        });
        RealMatrix rRef = MatrixUtils.createRealMatrix(new double[][] {
                { -14.0,  -21.0, 14.0 },
                {   0.0, -175.0, 70.0 },
                {   0.0,    0.0, 35.0 }
        });
        RealMatrix hRef = MatrixUtils.createRealMatrix(new double[][] {
                { 26.0 / 14.0, 0.0, 0.0 },
                {  6.0 / 14.0, 648.0 / 325.0, 0.0 },
                { -4.0 / 14.0,  36.0 / 325.0, 2.0 }
        });

        // check values against known references
        RealMatrix q = qr.getQ();
        assertEquals(0, q.subtract(qRef).getNorm(), 1.0e-13);
        RealMatrix qT = qr.getQT();
        assertEquals(0, qT.subtract(qRef.transpose()).getNorm(), 1.0e-13);
        RealMatrix r = qr.getR();
        assertEquals(0, r.subtract(rRef).getNorm(), 1.0e-13);
        RealMatrix h = qr.getH();
        assertEquals(0, h.subtract(hRef).getNorm(), 1.0e-13);

        // check the same cached instance is returned the second time
        assertTrue(q == qr.getQ());
        assertTrue(r == qr.getR());
        assertTrue(h == qr.getH());
        
    }

}
