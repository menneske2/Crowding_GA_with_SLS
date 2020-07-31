package cec15_nich_java_code;




import java.io.*;
import java.util.Scanner;


	public class testmain {
		public static void main(String[] args) throws Exception{
			double[] OShift,M,y,z,x_bound;
			int ini_flag = 0,n_flag,func_flag;
			int[] SS;
			
			int i,j,k,n,m,func_num;
			double[] f,x;
			File fpt;
			
			m=2;
			n=2;
			
			x = new double[m*n];
			f = new double[m];
			
			cec15_nich_func tf = new cec15_nich_func();
			
			for (i=0;i<15;i++)
			{
				func_num = i+1;
				fpt = new File("cec15_nich/input_data/shift_data_"+func_num+".txt");
				Scanner input = new Scanner(fpt);
				
				for(k=0;k<n;k++)
				{
//					x[k] = input.nextDouble();
					String s = input.next();
					s = s.strip();
					if(s.length()>0)
						x[k] = Double.parseDouble(s);
				}
				
				for(int ii=0;ii<n;ii++){
					//System.out.println(x[i]);
				}
				
				input.close();
				
				
				for(j=0;j<n;j++)
				{
					x[1*n+j] = 0.0;
					//System.out.println(x[1*n+j]);
				}
				
				// x = input
				// f = der fitness blir lagret.
				// n = dimensjoner
				// m = trials / num_samples.
				// enten m eller n er dimensjoner.
				// func_num = funksjonsnummer.
				
				for (k=0;k<1;k++)
				{
					tf.test_func(x,f,n,m,func_num);
					for(j=0;j<m;j++)
					{
						System.out.println("f"+func_num+"(x["+(j+1)+"])="+f[j]);
					}
				}
				
				
			}
		
			double[] x2 = new double[]{0, 0, 5, 5, 7};
			double[] f2 = new double[]{0, 0}; // like lang som m.
			int n2 = 5;
			int m2 = 1;

			tf.test_func(x2,f2,n2,m2,1);
			
		}
	}



