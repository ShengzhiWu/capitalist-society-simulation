import java.util.Random;


public class Data{//数据记录（循环使用一个数组）
	static final int maxlength=1000;
	
	private double[] array=new double[maxlength];
	int length=0;//已记录的数据长度
	private int p=maxlength-1;//当前指针位置（指着当前最新的数据）
	
	void put(double d1)//存一个数据
	{p++;
	 if(p>=maxlength)
		 p=0;
	 array[p]=d1;
	 if(length<maxlength)
		 length++;}
	
	double get(int index)//取一个数据
	{if(p>=index)
		 return array[p-index];
	 else
		 return array[maxlength-(index-p)];}
	
	void fill(double d1)//填充数据，用于初始化时伪造历史
	{int i1;
	 for(i1=0;i1<maxlength;i1++)
		 array[i1]=d1;
	 length=maxlength;}
	
	void randomFill(Random ra,double d1,double d2)//随机填充数据，用于初始化时伪造历史  随机数发生器  最小值  最大值
	{int i1;
	 d2-=d1;
	 for(i1=0;i1<maxlength;i1++)
		 array[i1]=d1+ra.nextDouble()*d2;
	 length=maxlength;}
}
