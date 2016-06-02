package beans;

import java.util.List;

import com.google.gson.Gson;

import Jama.Matrix;

import org.ejml.*;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public class CellModel {
	List<Test> tests;
	double[] coefficients = new double[5];
	
	public CellModel(List<Test> tests) {
		super();
		this.tests = tests;
	}

	public void build() {
		/* Calcul matriciel
		 * 
		 * Y = [P1, P2, Pi]'
		 * A = [I1 I1*I1 U1*I1*F U1*U1*F I1*I1*F; et ainsi de suite]
		 * X = [a, b, c, d, e]' (inconnue)
		 * 
		 * X = (At*A)^(-1)*At*Y
		 *
		 */

		double[] valAd = new double[tests.size()*5];
		double[] valYd = new double[tests.size()];
		
		double[] valA = new double[tests.size()*4];
		double[] valY = new double[tests.size()];
		
		int j = 0;
	    for (Test t : tests) {
	    	double u = Double.parseDouble(t.getVoltage());
	    	double i = Double.parseDouble(t.getCurrent());
	    	double f = Double.parseDouble(t.getFrequency());
	    	double l = Double.parseDouble(t.getLosses());
	    	
	    	valAd[j*5] = i;
	    	valAd[j*5+1] = i*i;
	    	valAd[j*5+2] = u*i*f;
	    	valAd[j*5+3] = u*u*f;
	    	valAd[j*5+4] = i*i*f;
	    	valYd[j] = l;
	    	
	    	valA[j*4] = i;
	    	valA[j*4+1] = u*i*f;
	    	valA[j*4+2] = u*u*f;
	    	valA[j*4+3] = i*i;
	    	valY[j] = l; 
	   
	    	j++;
	    }
	    
	    // Tests
		DenseMatrix64F A = new DenseMatrix64F(tests.size(), 4, true, valA);
		DenseMatrix64F Y = new DenseMatrix64F(tests.size(), 1, true, valY);
		DenseMatrix64F Ainv = new DenseMatrix64F(4, tests.size(), true, valA);
		CommonOps.pinv(A, Ainv);
		DenseMatrix64F X = new DenseMatrix64F(4, 1, true, valY);
		CommonOps.mult(Ainv, Y, X);
		A.print();
		System.out.println();
		Ainv.print();
		System.out.println();
		Y.print();
		System.out.println();
		System.out.println("X");
		X.print();
		System.out.println();
		
		double[] xb = X.data;
		this.coefficients[0] =  xb[0];
		this.coefficients[1] =  0.8*0.028;
		this.coefficients[2] =  xb[1];
		this.coefficients[3] =  xb[2];
		this.coefficients[4] =  (xb[3] - 0.8*0.028) / 120000;
		

	    // A
		DenseMatrix64F Ad = new DenseMatrix64F(tests.size(), 5, true, valAd);
		DenseMatrix64F Adinv = new DenseMatrix64F(5, tests.size(), true, valAd);
		CommonOps.pinv(Ad, Adinv);
		
		// Y
		DenseMatrix64F Yd = new DenseMatrix64F(tests.size(), 1, true, valYd);
		
		// X
		DenseMatrix64F Xd = new DenseMatrix64F(5, 1, true, valYd);
		CommonOps.mult(Adinv, Yd, Xd);

		this.coefficients = Xd.data;
	}
	
	public void setTests(List<Test> tests) {
		this.tests = tests;
	}
	
	public double[] getCoefficients() {
		return coefficients;
	}
}
