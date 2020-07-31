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


testfunc.java is the test function
Example:
cec15_nich_func tf = new cec15_nich_func();

tf.test_func(x, f, dimension,population_size,func_num);

testmain.java is an example function about how to use testfunc.java