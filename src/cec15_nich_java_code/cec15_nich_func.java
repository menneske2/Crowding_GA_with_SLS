package cec15_nich_java_code;

/*
  CEC15 Niching Test Function Suite 
  B. Zheng (email: zheng.b1988@gmail.com) 
  Nov. 21th 2014

  Reference: 
  B. Y. Qu, J. J. Liang, P. N. Suganthan, Q. Chen, "Problem Definitions and Evaluation Criteria for 
  the CEC 2015 Special Session and Competition on Niching Numerical Optimization",Technical 
  Report201411B,Computational Intelligence Laboratory, Zhengzhou University, Zhengzhou China 
  and Technical Report, Nanyang Technological University, Singapore, November 2014
*/

import java.io.File;
import java.util.Scanner;



public class cec15_nich_func {

	final double INF = 1.0e99;
	final double EPS = 1.0e-14;
	final double E  = 2.7182818284590452353602874713526625;
	final double PI = 3.1415926535897932384626433832795029;
	
	double[] OShift,M,y,z,x_bound;
	int ini_flag,n_flag,func_flag;
	int[] SS;
	
	int[] cf_nums = {0,1,1,1,1,1,1,1,1,10,10,10,10,10,10,10};
	
	
	//double twopeaks_func  (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Two-Peak Trap  */
	//double fiveuneven_func   (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Five-Uneven-Peak Trap  */
	//double equalmin_func   (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Equal Minima  */
	//double decreasemin_func   (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Decreasing Minima   */
	//double unevenmin_func   (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Uneven Minima   */
	//double himmelblau_func   (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Himmelblau��s Function  */
	//double camelback_func   (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Six-Hump Camel Back  */
	//double vincent_func    (double[] , double , int , double[] ,double[] ,int ,int) /* Modified Vincent Function  */
		
	//double sphere_func (double[] , double , int , double[] ,double[] ,int ,int) /* Sphere */
	//double ellips_func (double[] , double , int , double[] ,double[] ,int ,int) /* Ellipsoidal */
	//double bent_cigar_func (double[] , double , int , double[] ,double[] ,int ,int) /* Bent_Cigar */
	//double discus_func_func (double[] , double , int , double[] ,double[] ,int ,int) /* Discus */
	//double dif_powers_func (double[] , double , int , double[] ,double[] ,int ,int) /* Different Powers  */
	//double rosenbrock_func (double[] , double , int , double[] ,double[] ,int ,int) /* Rosenbrock's  */
	//double schaffer_F7_func (double[] , double , int , double[] ,double[] ,int ,int) /* Schwefel's F7  */
	//double ackley_func (double[] , double , int , double[] ,double[] ,int ,int) /* Ackley's  */
	//double rastrigin_func (double[] , double , int , double[] ,double[] ,int ,int) /* Rastrigin's  */
	//double weierstrass_func (double[] , double , int , double[] ,double[] ,int ,int) /* Weierstrass's  */
	//double griewank_func (double[] , double , int , double[] ,double[] ,int ,int) /* Griewank's  */
	//double schwefel_func (double[] , double , int , double[] ,double[] ,int ,int) /* Schwefel's */
	//double katsuura_func (double[] , double , int , double[] ,double[] ,int ,int) /* Katsuura */
	//double bi_rastrigin_func (double[] , double , int , double[] ,double[] ,int ,int) /* Lunacek Bi_rastrigin  */
	//double grie_rosen_func (double[] , double , int , double[] ,double[] ,int ,int) /* Griewank-Rosenbrock  */
	//double escaffer6_func (double[] , double , int , double[] ,double[] ,int ,int) /* Expanded Scaffer��s F6  */
	//double step_rastrigin_func (double[] , double , int , double[] ,double[] ,int ,int) /* Noncontinuous Rastrigin's  */
	//double happycat_func (double[] , double , int , double[] ,double[] ,int ,int) /* HappyCat  */
	//double hgbat_func (double[] , double , int , double[] ,double[] ,int ,int) /* HGBat  */
	
	//double cf01 (double[] , double[] , int , double[] ,double[] ,int ) /* Composition Function 1 */
	//double cf02 (double[] , double[] , int , double[] ,double[] ,int ) /* Composition Function 2 */
	//double cf03 (double[] , double[] , int , double[] ,double[] ,int ) /* Composition Function 3 */
	//double cf04 (double[] , double[] , int , double[] ,double[] ,int ) /* Composition Function 4 */
	//double cf05 (double[] , double[] , int , double[] ,double[] ,int ) /* Composition Function 5 */
	//double cf06 (double[] , double[] , int , double[] ,double[] ,int ) /* Composition Function 6 */
	//double cf07 (double[] , double[] , int , double[] ,double[] ,int ) /* Composition Function 7 */
	
	
	//void shiftfunc (double[] , double[] , int ,double[] )
	//void rotatefunc (double[] , double[] , int ,double[] )
	//void sr_func (double[] .double[] ,int ,double[] ,double[] ,double ,int ,int )/* shift and rotate*/
	//double cf_cal(double[] , double , int , double[] ,double[] ,double[] ,double[] , int )
	
	public void test_func(double[] x, double[] f, int nx, int mx,int func_num)throws Exception{
	
		int cf_num=10,i,j;
	
		if (ini_flag==1) 
		{
			if ((n_flag!=nx)||(func_flag!=func_num)) 
			{
				ini_flag=0;
			}
		}
		
		if (ini_flag==0) /* initiailization*/
		{
			
			y=new double[nx];
			z=new double[nx];
			x_bound=new double[nx];
			for (i=0; i<nx; i++){
				x_bound[i]=100.0;
			}
		
		
		/*Load Matrix M*****************************************************/
		File fpt = new File("cec15_nich/input_data/M_"+func_num+"_D"+nx+".txt");//* Load M data *
		Scanner input = new Scanner(fpt);
		if (!fpt.exists())
		{
		    System.out.println("\n Error: Cannot open input file for reading ");
		}
		
		
		M=new double[nx*nx*cf_nums[func_num]]; 
		
		for (i=0;i<nx*nx*cf_nums[func_num]; i++)
		{
//			M[i]=input.nextDouble();
			String s = input.next();
			s = s.strip();
			if(s.length()>0)
				M[i] = Double.parseDouble(s);
		}
		
		input.close();
		
		/*Load shift_data***************************************************/
		fpt=new File("cec15_nich/input_data/shift_data_"+func_num+".txt");
		input = new Scanner(fpt);
		if (!fpt.exists())
		{
			System.out.println("\n Error: Cannot open input file for reading ");
		}
		
		OShift=new double[cf_nums[func_num]*nx];
		
		/*for(i=0;i<cf_nums[func_num]*nx;i++)
		{
			OShift[i]=input.nextDouble();
			
		}
		input.close();*/
		
		if(func_num<9)
		{
			for (i=0;i<nx*cf_nums[func_num];i++)
			{
				
//				OShift[i]=input.nextDouble();
				String s = input.next();
				s = s.strip();
				if(s.length()>0)
					OShift[i] = Double.parseDouble(s);
			}
		}
		else
		{
			for(i=0;i<cf_nums[func_num]-1;i++)
			{
				for(j=0;j<nx;j++)
				{
//					OShift[i*nx+j]=input.nextDouble();
					String s = input.next();
					s = s.strip();
					if(s.length()>0)
						OShift[i*nx+j] = Double.parseDouble(s);
				}
				String sss=input.nextLine();
				
				//System.out.println(OShift[i*nx+j]);
			}
			for(j=0;j<nx;j++)
			{
//				OShift[(cf_nums[func_num]-1)*nx+j]=input.nextDouble();
				String s = input.next();
				s = s.strip();
				if(s.length()>0)
					OShift[(cf_nums[func_num]-1)*nx+j] = Double.parseDouble(s);
			}
			
		}
			
			
			
			
			
		
		

		n_flag=nx;
		func_flag=func_num;
		ini_flag=1;
		
		}
		
		
		double[] t = new double[nx];
		
		for (i = 0; i < mx; i++){
			
			for ( j=0; j<nx; j++){
				t[j] = x[i*nx+j];
			}
			
			switch(func_num)
			{
			case 1:	
				f[i]=twopeaks_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=100.0;
				break;
			case 2:	
				f[i]=fiveuneven_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=200.0;
				break;
			case 3:	
				f[i]=equalmin_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=300.0;
				break;
			case 4:	
				f[i]=decreasemin_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=400.0;
				break;
			case 5:
				f[i]=unevenmin_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=500.0;
				break;
			case 6:
				f[i]=himmelblau_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=600.0;
				break;
			case 7:	
				f[i]=camelback_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=700.0;
				break;
			case 8:	
				f[i]=vincent_func(t,f[i],nx,OShift,M,1,1);
//				f[i]+=800.0;
				break;
			case 9:	
				f[i]=cf01(t,f[i],nx,OShift,M,1);
//				f[i]+=900.0;
				break;
			case 10:	
				f[i]=cf02(t,f[i],nx,OShift,M,1);
//				f[i]+=1000.0;
				break;
			case 11:	
				f[i]=cf03(t,f[i],nx,OShift,M,1);
//				f[i]+=1100.0;
				break;
			case 12:	
				f[i]=cf04(t,f[i],nx,OShift,M,1);
//				f[i]+=1200.0;
				break;
			case 13:	
				f[i]=cf05(t,f[i],nx,OShift,M,1);
//				f[i]+=1300.0;
				break;
			case 14:	
				f[i]=cf06(t,f[i],nx,OShift,M,1);
//				f[i]+=1400.0;
				break;
			case 15:	
				f[i]=cf07(t,f[i],nx,OShift,M,1);
//				f[i]+=1500.0;
				break;
			

			default:
				System.out.println("\nError: There are only 15 test functions in this test suite!");
				f[i] = 0.0;
				break;
			}
			
		}			
		}
		
		
		double twopeaks_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Two-Peak Trap */
		{
			int i;
			f=0.0;
			sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/
			
			for(i=0;i<nx;i++){
				z[i] +=20.0;//shift to orgin
				if((z[i]<15.0)&(z[i]>0.0)){
					f += -(160.0/15.0)*(15.0-z[i]);
				}
				else if ((z[i]<20.0)&(z[i]>0.0))
				{
					f += -40.0*(z[i]-15.0);
				}
				else if (z[i]<0.0)
				{
					f += -160.0+Math.pow(z[i],2.0);
				}
				else
				{
					f += -200.0+Math.pow(z[i]-20.0,2.0);
				}
			}
			
			f += 200.0*nx;
			
			return f;
							
			
		}
		
		
		
		double fiveuneven_func  (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Five-Uneven-Peak Trap  */
		{
			int i;
			f=0.0;
			sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/
			
			for (i=0; i<nx; i++)
			{	
				if (z[i]<0)
				{
					f += -200.0+Math.pow(z[i],2.0);
				}
				else if (z[i]<2.5)
				{
					f += -80.0*(2.5-z[i]);
				}
				else if (z[i]<5.0)
				{
					f += -64.0*(z[i]-2.5);
				}
				else if (z[i]<7.5)
				{
					f += -160+Math.pow(z[i],2.0);
				}
				else if (z[i]<12.5)
				{
					f += -28.0*(z[i]-7.5);
				}
				else if (z[i]<17.5)
				{
					f += -28.0*(17.5-z[i]);
				}
				else if (z[i]<22.5)
				{
					f += -32.0*(z[i]-17.5);
				}
				else if (z[i]<27.5)
				{
					f += -32.0*(27.5-z[i]);
				}
				else if (z[i]<=30.0)
				{
					f += -80.0*(z[i]-27.5);
				}
				else
				{
					f += -200.0+Math.pow(z[i]-30.0,2.0);
				}
			}

			f += 200.0*nx;
			return f;
							
						
		}
		
		double equalmin_func  (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Equal Minima   */
		{
			int i;
			f=0.0;
			sr_func(x,z,nx,Os,Mr,1.0/20.0,s_flag,r_flag);/*shift and rotate*/
			
			for (i=0; i<nx; i++)
			{	
				z[i] += 0.1;//shift to orgin
				if ((z[i]<=1.0)&(z[i]>=0.0))
				{
					f += -Math.pow((Math.sin(5.0*PI*z[i])),6.0);
				}
				else 
				{
					f += Math.pow(z[i],2.0);
				}
			}
			f += 1.0*nx;

			return f;
									
		}
		
		
		
		double decreasemin_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Decreasing Minima */
		{
			int i;
			f=0.0;
			sr_func(x,z,nx,Os,Mr,1.0/20.0,s_flag,r_flag);/*shift and rotate*/
			
			for (i=0; i<nx; i++)
			{	
				z[i] += 0.1;//shift to orgin
				if ((z[i]<=1.0)&(z[i]>=0.0))
				{
					f += -Math.exp(-2.0*Math.log(2.0)*Math.pow((z[i]-0.1)/0.8,2.0))*Math.pow(Math.sin(5.0*PI*z[i]),6.0);
				}
				else 
				{
					f += Math.pow(z[i],2.0);
				}
			}
			f += 1.0*nx;
			return f;
			
						
		}
	
		
		double unevenmin_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Uneven Minima */
		{
			int i;
			f=0.0;
			sr_func(x,z,nx,Os,Mr, 1.0/20.0,s_flag,r_flag);/*shift and rotate*/
			
			for (i=0; i<nx; i++)
			{	
				z[i] += 0.079699392688696;//pow(0.15,4.0/3.0);//shift to orgin
				if ((z[i]<=1.0)&(z[i]>=0.0))
				{
					f -= Math.pow(Math.sin(5*PI*(Math.pow(z[i],0.75)-0.05)),6.0);
				}
				else 
				{
					f += Math.pow(z[i],2.0);
				}
			}
			f += 1.0*nx;
			return f;
			
		}
		
		double himmelblau_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Himmelblau��s Function */
		{
			int i;
			f=0.0;
			sr_func(x,z,nx,Os,Mr,1.0/5.0,s_flag,r_flag);/*shift and rotate*/
			
			for (i=0; i<nx-1; i=i+2)
			{	
				z[i] += 3.0;
				z[i+1] += 2.0;//shift to orgin
				f += Math.pow((z[i]*z[i]+z[i+1]-11.0),2.0)+Math.pow((z[i]+z[i+1]*z[i+1]-7.0),2.0);
			}
			return f;
		}
	
		
		double camelback_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Six-Hump Camel Back */
		{
			int i;
			f=0.0;
			sr_func(x,z,nx,Os,Mr,1.0/20.0,s_flag,r_flag);/*shift and rotate*/
			
			for (i=0; i<nx-1; i=i+2)
			{	
				z[i] += 0.089842;   
				z[i+1] += -0.712656;//shift to orgin
				f += ((4.0-2.1*Math.pow(z[i],2.0)+Math.pow(z[i],4.0)/3)*Math.pow(z[i],2.0)+z[i]*z[i+1] + ((-4.0+4.0*Math.pow(z[i+1],2.0))*Math.pow(z[i+1],2.0)))*4.0;
			}
			f += 4.126514*nx/2.0;	 
			return f;
									
		}
		
		double vincent_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Modified Vincent Function */
		{
			//orginal bound [0.25, 10], optima=[0.333; 0.6242; 1.1701; 2.1933; 4.1112; 7.7063]
			int i;
			f = 0.0;
			
			/*for (int j=0;j<nx;j++)
			{
				System.out.println(Os[j]);
			}*/
			
			sr_func (x, z, nx, Os, Mr, 1.0/5.0, s_flag, r_flag); /* shift and rotate */ 	
			
			/*for (int j=0;j<nx;j++)
			{
				System.out.println(z[j]);
			}*/
			
			
			
			for (i=0; i<nx; i++)
			{	
				z[i] += 4.1112;//shift to orgin
				if ((z[i]>=0.25)&&(z[i]<=10.0))
				{
					f += -Math.sin(10.0*Math.log(z[i]));
				}
				else if (z[i]<0.25)
				{
					f += Math.pow(0.25-z[i],2.0)-Math.sin(10.0*Math.log(2.5));
				}
				else
				{
					f += Math.pow(z[i]-10,2.0)-Math.sin(10.0*Math.log(10.0));
				}
			}
			f=f/nx;
			f += 1.0;
			return f;
		}

		
		double sphere_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Sphere */
		{
			int i;
			f = 0.0;
			sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */	
			for (i=0; i<nx; i++)
			{					
				f += z[i]*z[i];
			}
			return f;

		}
		
		double ellips_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Ellipsoidal */
		{
		    int i;
		    f = 0.0;
		    sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/
		        
		    for (i=0; i<nx; i++)
		    {
		        f += Math.pow(10.0,6.0*i/(nx-1.0))*z[i]*z[i];
		    }
		    return f;
		}
		
		double bent_cigar_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Bent_Cigar */
		{
			int i;
			sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/
		    
			f = z[0]*z[0];
			for (i=1; i<nx; i++)
		    {
		        f += Math.pow(10.0,6.0)*z[i]*z[i];
		    }
		    return f;
		}
		
		double discus_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Discus */
		{
		    int i;
		    sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/

			f = Math.pow(10.0,6.0)*z[0]*z[0];
		    for (i=1; i<nx; i++)
		    {
		        f += z[i]*z[i];
		    }
		    
		    return f;
		}
		
		double dif_powers_func  (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Different Powers  */
		{
			int i;
			f = 0.0;
			sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */

			for (i=0; i<nx; i++)
			{
				f += Math.pow(Math.abs(z[i]),2.0+4.0*i/(nx-1.0));
			}
			f=Math.pow(f,0.5);
			return f;
			
		}
		
		
		double rosenbrock_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Rosenbrock's */
		{
		    int i;
			double tmp1,tmp2;
			f = 0.0;
		    sr_func(x,z,nx,Os,Mr,2.048/100.0,s_flag,r_flag);/*shift and rotate*/
		    z[0] +=1.0; //shift to origin
		    for (i=0; i<nx-1; i++)
		    {
				z[i+1] += 1.0; //shift to orgin
		    	tmp1=z[i]*z[i]-z[i+1];
				tmp2=z[i]-1.0;
		        f += 100.0*tmp1*tmp1 +tmp2*tmp2;
		    }
		    
		    
		    return f;
		}
		
		
		double ackley_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Ackley's  */
		{
		    int i;
		    double sum1, sum2;
		    sum1 = 0.0;
		    sum2 = 0.0;
		    
		    sr_func(x,z,nx,Os,Mr,1.0,s_flag,r_flag);/*shift and rotate*/ 		
		    
		    for (i=0; i<nx; i++)
		    {
		        sum1 += z[i]*z[i];
		        sum2 += Math.cos(2.0*PI*z[i]);
		    }
		    sum1 = -0.2*Math.sqrt(sum1/nx);
		    sum2 /= nx;
		    f =  E - 20.0*Math.exp(sum1) - Math.exp(sum2) +20.0;
		    
		    return f;
		}
		
		
		double weierstrass_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Weierstrass's  */
		{
		    int i,j,k_max;
		    double sum,sum2=0, a, b;
		    
		    sr_func(x,z,nx,Os,Mr,0.5/100.0,s_flag,r_flag);/*shift and rotate*/ 
			
				   
		    a = 0.5;
		    b = 3.0;
		    k_max = 20;
		    f = 0.0;
		    for (i=0; i<nx; i++)
		    {
		        sum = 0.0;
				sum2 = 0.0;
		        for (j=0; j<=k_max; j++)
		        {
		            sum += Math.pow(a,j)*Math.cos(2.0*PI*Math.pow(b,j)*(z[i]+0.5));
					sum2 += Math.pow(a,j)*Math.cos(2.0*PI*Math.pow(b,j)*0.5);
		        }
		        f += sum;
		    }
			f -= nx*sum2;
			
			return f;
		}
		
		double griewank_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Griewank's  */
		{
		    int i;
		    double s, p;

		    sr_func(x,z,nx,Os,Mr,600.0/100.0,s_flag,r_flag);/*shift and rotate*/ 

		    s = 0.0;
		    p = 1.0;
		    for (i=0; i<nx; i++)
		    {
		        s += z[i]*z[i];
		        p *= Math.cos(z[i]/Math.sqrt(1.0+i));
		    }
		    f = 1.0 + s/4000.0 - p;
		    
		    return f;
		}
		
		
		double rastrigin_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Rastrigin's  */
		{
		    int i;
			f=0.0;
		    
			sr_func(x,z,nx,Os,Mr,5.12/100.0,s_flag,r_flag);/*shift and rotate*/ 

			for(i=0;i<nx;i++)
			{
				f += (z[i]*z[i] - 10.0*Math.cos(2.0*PI*z[i]) + 10.0);
			}
		    
		    return f;
		}
		
		
		double schwefel_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Schwefel's  */
		{
		    int i;
			double tmp;
			
			sr_func(x,z,nx,Os,Mr,1000.0/100.0,s_flag,r_flag);/*shift and rotate*/ 
			
					
		    f=0;
		    for (i=0; i<nx; i++)
			{
		    	z[i] += 4.209687462275036e+002;
		    	if (z[i]>500)
				{
					f-=(500.0-(z[i]%500))*Math.sin(Math.pow(500.0-(z[i]%500),0.5));
					tmp=(z[i]-500.0)/100;
					f+= tmp*tmp/nx;
				}
				else if (z[i]<-500)
				{
					f-=(-500.0+(Math.abs(z[i])%500))*Math.sin(Math.pow(500.0-(Math.abs(z[i])%500),0.5));
					tmp=(z[i]+500.0)/100;
					f+= tmp*tmp/nx;
				}
				else
					f-=z[i]*Math.sin(Math.pow(Math.abs(z[i]),0.5));
		    }
		    f=4.189828872724338e+002*nx+f;
		    
		    return f;
		}
		
		
		double katsuura_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Katsuura  */
		{
		    int i,j;
			double temp,tmp1,tmp2,tmp3;
			tmp3=Math.pow(1.0*nx,1.2);
			
			sr_func(x,z,nx,Os,Mr,5/100.0,s_flag,r_flag);/*shift and rotate*/ 
			
		    
		    f=1.0;
		    for (i=0; i<nx; i++)
			{
				temp=0.0;
				for (j=1; j<=32; j++)
				{
					tmp1=Math.pow(2.0,j);
					tmp2=tmp1*z[i];
					temp += Math.abs(tmp2-Math.floor(tmp2+0.5))/tmp1;
				}
				f *= Math.pow(1.0+(i+1)*temp,10.0/tmp3);
		    }
			tmp1=10.0/nx/nx;
		    f=f*tmp1-tmp1;
		    
		    return f;

		}
		
		double grie_rosen_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Griewank-Rosenbrock  */
		{
		    int i;
		    double temp,tmp1,tmp2;
		    
		    sr_func (x, z, nx, Os, Mr, 5.0/100.0, s_flag, r_flag); /* shift and rotate */

			
		    f=0.0;
		    
		    z[0] += 1.0; //shift to orgin
		    for (i=0; i<nx-1; i++)
		    {
		    	z[i+1] += 1.0; //shift to orgin
				tmp1 = z[i]*z[i]-z[i+1];
				tmp2 = z[i]-1.0;
		        temp = 100.0*tmp1*tmp1 + tmp2*tmp2;
		         f += (temp*temp)/4000.0 - Math.cos(temp) + 1.0;
		    }
			tmp1 = z[nx-1]*z[nx-1]-z[0];
			tmp2 = z[nx-1]-1.0;
		    temp = 100.0*tmp1*tmp1 + tmp2*tmp2;;
		     f += (temp*temp)/4000.0 - Math.cos(temp) + 1.0 ;
		     
		     return f;
		}
		
		double happycat_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) 
		/*HappyCat, probided by Hans-Georg Beyer (HGB)*/
		/*original global optimum: [-1,-1,...,-1]*/
		{
			int i;
			double alpha,r2,sum_z;
			alpha = 1.0/8.0;
			
			sr_func(x,z,nx,Os,Mr,5/100.0,s_flag,r_flag);/*shift and rotate*/ 
			
			r2 = 0.0;
			sum_z = 0.0;
			f = 0.0;
			for (i=0;i<nx;i++)
			{
				z[i] = z[i] - 1.0; //shift to orgin
				r2 += z[i]*z[i];
				sum_z += z[i];
				
			}
			f = Math.pow(Math.abs(r2-nx), 2.0*alpha) + (0.5*r2 + sum_z)/nx + 0.5;
			
			return f;
		}
		
		double hgbat_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) 
		/*HGBat, provided by Hans-Georg Beyer (HGB)*/
		/*original global optimum: [-1,-1,...-1]*/
		{
			int i;
			double alpha,r2,sum_z;
			alpha=1.0/4.0;

			sr_func (x, z, nx, Os, Mr, 5.0/100.0, s_flag, r_flag); /* shift and rotate */

			r2 = 0.0;
			sum_z=0.0;
		    for (i=0; i<nx; i++)
		    {
				z[i]=z[i]-1.0;//shift to orgin
		        r2 += z[i]*z[i];
				sum_z += z[i];
		    }
		    f=Math.pow(Math.abs(Math.pow(r2,2.0)-Math.pow(sum_z,2.0)),2*alpha) + (0.5*r2 + sum_z)/nx + 0.5;
		    return f;

		}
		
		double escaffer6_func (double[] x, double f, int nx, double[] Os,double[] Mr,int s_flag,int r_flag) /* Expanded Scaffer��s F6  */
		{
		    int i;
		    double temp1, temp2;
		    
		    sr_func (x, z, nx, Os, Mr, 1.0, s_flag, r_flag); /* shift and rotate */

			
		    f = 0.0;
		    for (i=0; i<nx-1; i++)
		    {
		        temp1 = Math.sin(Math.sqrt(z[i]*z[i]+z[i+1]*z[i+1]));
				temp1 =temp1*temp1;
		        temp2 = 1.0 + 0.001*(z[i]*z[i]+z[i+1]*z[i+1]);
		        f += 0.5 + (temp1-0.5)/(temp2*temp2);
		    }
		    temp1 = Math.sin(Math.sqrt(z[nx-1]*z[nx-1]+z[0]*z[0]));
			temp1 =temp1*temp1;
		    temp2 = 1.0 + 0.001*(z[nx-1]*z[nx-1]+z[0]*z[0]);
		    f += 0.5 + (temp1-0.5)/(temp2*temp2);
		    
		    return f;
		}
		
		
		double cf01 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 1 */
		{
			int i,j,cf_num=10;
			double[] fit = new double[10];
			double[] delta = {10,20,10,20,10,20,10, 20, 10, 20};
			double[] bias = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			
			
			double[] tOs = new double[nx];
			double[] tMr = new double[cf_num*nx*nx];
			
			
			i=0;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=sphere_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			i=1;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=sphere_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			i=2;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=3;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=4;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=bent_cigar_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=5;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=bent_cigar_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=6;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=discus_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+4;
		    i=7;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=discus_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+4;
		    i=8;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=dif_powers_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+5;
			i=9;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=dif_powers_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+5;
			
			return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
		}
		
		
		double cf02 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 2 */
		{
			int i,j,cf_num=10;
			double[] fit = new double[10];
			double[] delta = {10,20,30,40,50,60,70, 80, 90, 100};
			double[] bias = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};
			
			
			double[] tOs = new double[nx];
			double[] tMr = new double[cf_num*nx*nx];
			
			
			i=0;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=1;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=ellips_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=2;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=dif_powers_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+5;
			i=3;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=dif_powers_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+5;
			i=4;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=bent_cigar_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=5;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=bent_cigar_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+6;
			i=6;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=discus_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+4;
		    i=7;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=discus_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+4;
		    i=8;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=sphere_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			
			i=9;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=sphere_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			
			
			return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
		}
		
		
		double cf03 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 3 */
		{
			int i,j,cf_num=10;
			double[] fit=new double[10];
			double[] delta = {10,10,10,10,10,10,10, 10, 10, 10};
			double[] bias = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};;
			
			double[] tOs = new double[nx];
			double[] tMr = new double[cf_num*nx*nx];
			
			
			i=0;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=1;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=2;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=3;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=4;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=5;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=6;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=escaffer6_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*100;
		    i=7;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
		    fit[i]=escaffer6_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*100;
		    i=8;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
		    fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			i=9;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			
			return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
		}

		
		double cf04 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 4 */
		{
			int i,j,cf_num=10;
			double[] fit=new double[10];
			double[] delta = {10,10,20,20,30,30,40, 40, 50, 50};
			double[] bias = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};;
			
			double[] tOs = new double[nx];
			double[] tMr = new double[cf_num*nx*nx];
			
			
			i=0;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=1;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=2;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=3;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=4;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=5;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=6;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=escaffer6_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*100;
		    i=7;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=escaffer6_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*100;
		    i=8;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			i=9;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			
			return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
		}
		
		double cf05 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 5 */
		{
			int i,j,cf_num=10;
			double[] fit=new double[10];
			double[] delta = {10,20,30,40,50,60,70, 80, 90, 100};
			double[] bias = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};;
			
			double[] tOs = new double[nx];
			double[] tMr = new double[cf_num*nx*nx];
			
			
			
			i=0;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=1;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=hgbat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=2;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=3;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=ackley_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=4;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=weierstrass_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*2.5;
			i=5;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=katsuura_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+3;
			i=6;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=escaffer6_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*100;
		    i=7;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=grie_rosen_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*2.5;
		    i=8;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10;
			i=9;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			
			return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
		}
		
		
		double cf06 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 6 */
		{
			int i,j,cf_num=10;
			double[] fit=new double[10];
			double[] delta = {10,10,20,20,30,30,40, 40, 50, 50};
			double[] bias = {0, 20, 40, 60, 80, 100, 120, 140, 160, 180};
			
			double[] tOs = new double[nx];
			double[] tMr = new double[cf_num*nx*nx];
			
			
			
			i=0;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=1;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			i=2;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=3;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			i=4;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=5;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			i=6;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
		    i=7;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
		    i=8;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=9;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			
			
			return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
		}
		
		double cf07 (double[] x, double f, int nx, double[] Os,double[] Mr,int r_flag) /* Composition Function 7 */
		{
			int i,j,cf_num=10;
			double[] fit=new double[10];
			double[] delta = {10,10,20,20,30,30,40, 40, 50, 50};
			double[] bias = {0, 20, 40, 60, 80, 100, 120, 140, 160, 180};
			
			double[] tOs = new double[nx];
			double[] tMr = new double[cf_num*nx*nx];
			
			
			
			i=0;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rosenbrock_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=1;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=hgbat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=2;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=rastrigin_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=3;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=ackley_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/10.0;
			i=4;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=weierstrass_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*2.5;
			i=5;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=katsuura_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]/1e+3;
			i=6;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=escaffer6_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*100;
		    i=7;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=grie_rosen_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*2.5;
		    i=8;
		    for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=happycat_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			fit[i]=fit[i]*10.0;
			i=9;
			for(j=0;j<nx;j++){
				tOs[j] = Os[i*nx+j];
			}
			for(j=0;j<nx*nx;j++){
				tMr[j] = Mr[i*nx*nx+j];
			}
			fit[i]=schwefel_func(x,fit[i],nx,tOs,tMr,1,r_flag);
			
			return cf_cal(x, f, nx, Os, delta,bias,fit,cf_num);
		}

		


		
		
		
		
		
		
		
		
	
	
	
	
	
	
	
	
	
	
		
		void shiftfunc (double[] x, double[] xshift, int nx,double[] Os)
		{
			int i;
		    for (i=0; i<nx; i++)
		    {
		        xshift[i]=x[i]-Os[i];
		    }
		}
		
		
		void rotatefunc (double[] x, double[] xrot, int nx,double[] Mr)
		{
			int i,j;
		    for (i=0; i<nx; i++)
		    {
		        xrot[i]=0;
					for (j=0; j<nx; j++)
					{
						xrot[i]=xrot[i]+x[j]*Mr[i*nx+j];
					}
		    }
		}
		
		void sr_func (double[] x, double[] sr_x, int nx, double[] Os, double[] Mr, double sh_rate, int s_flag, int r_flag)
		{
			int i,j;
			if (s_flag==1)
			{
				if (r_flag==1)
				{	
					shiftfunc(x, y, nx, Os);
					for (i=0; i<nx; i++)//shrink to the orginal search range
					{
						y[i]=y[i]*sh_rate;
					}
					rotatefunc(y, sr_x, nx, Mr);
				}
				else
				{
					shiftfunc(x, sr_x, nx, Os);
					for (i=0; i<nx; i++)//shrink to the orginal search range
					{
						sr_x[i]=sr_x[i]*sh_rate;
					}
				}
			}
			else
			{	

				if (r_flag==1)
				{	
					for (i=0; i<nx; i++)//shrink to the orginal search range
					{
						y[i]=x[i]*sh_rate;
					}
					rotatefunc(y, sr_x, nx, Mr);
				}
				else
				
				{
					for (j=0; j<nx; j++)//shrink to the orginal search range
					{
						sr_x[j]=x[j]*sh_rate;
					}
				}
			}
	
	
	
	    }
	
		
		double cf_cal(double[] x, double f, int nx, double[] Os,double[] delta,double[] bias,double[] fit, int cf_num)
		{
			int i,j;
				
			double[] w;
			double w_max=0,w_sum=0;
			w=new double[cf_num];
			for (i=0; i<cf_num; i++)
			{
				fit[i]+=bias[i];
				w[i]=0;
				for (j=0; j<nx; j++)
				{
					w[i]+=Math.pow(x[j]-Os[i*nx+j],2.0);
				}
				if (w[i]!=0)
					w[i]=Math.pow(1.0/w[i],0.5)*Math.exp(-w[i]/2.0/nx/Math.pow(delta[i],2.0));
				else
					w[i]=INF;
				if (w[i]>w_max)
					w_max=w[i];
			}

			for (i=0; i<cf_num; i++)
			{
				w_sum=w_sum+w[i];
			}
			if(w_max==0)
			{
				for (i=0; i<cf_num; i++)
					w[i]=1;
				w_sum=cf_num;
			}
			f = 0.0;
		    for (i=0; i<cf_num; i++)
		    {
				f=f+w[i]/w_sum*fit[i];
		    }
		    
		    return f;
			
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
}