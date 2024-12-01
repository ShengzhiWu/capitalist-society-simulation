
public class Expect {//预期
	
	static double tx1=300d,tx2=80,tx3=30,tx4=8,//分析基准时间尺度
				  tv1=1000d,tv2=200d,tv3=20d,tv4=4d;//分析基准时间尺度
	
	double x1,x2,x3,x4,//近期平均值
		   v1,v2,v3,v4;//各个时间尺度下线性回归得到的增长率
	
	Expect(Data data,double tk)//基于以往的离散数据，给出对未来形势的预期
								  //以往数据   时间尺度系数（对于变化平稳的领域的数据，应对更长时间的数据进行分析，取较大的值）
	{int txi1,txi2,txi3,txi4,tvi1,tvi2,tvi3,tvi4;
	 int i1,i2;
	 double d1;
	 
	 if(data.length==0)
	 {x1=0d;
	  x2=0d;
	  x3=0d;
	  x4=0d;
	  v1=0d;
	  v2=0d;
	  v3=0d;
	  v4=0d;
	  return;}
	 
	 txi1=Math.min(round(tx1*tk),Data.maxlength);
	 txi2=Math.min(round(tx2*tk),Data.maxlength);
	 txi3=Math.min(round(tx3*tk),Data.maxlength);
	 txi4=Math.min(round(tx4*tk),Data.maxlength);
	 tvi1=Math.min(round(tv1*tk),Data.maxlength);
	 tvi2=Math.min(round(tv2*tk),Data.maxlength);
	 tvi3=Math.min(round(tv3*tk),Data.maxlength);
	 tvi4=Math.min(round(tv4*tk),Data.maxlength);
	 if(txi1==0)
		 txi1=1;
	 if(txi2==0)
		 txi2=1;
	 if(txi3==0)
		 txi3=1;
	 if(txi4==0)
		 txi4=1;
	 if(tvi1==0)
		 tvi1=1;
	 if(tvi2==0)
		 tvi2=1;
	 if(tvi3==0)
		 tvi3=1;
	 if(tvi4==0)
		 tvi4=1;
	 
	 i1=Math.min(data.length,txi1);
	 d1=0d;
	 for(i2=0;i2<i1;i2++)
		 d1+=data.get(i2);
	 x1=d1/i1;//近期平均值
	 i1=Math.min(data.length,txi2);
	 d1=0d;
	 for(i2=0;i2<i1;i2++)
		 d1+=data.get(i2);
	 x2=d1/i1;//近期平均值
	 i1=Math.min(data.length,txi3);
	 d1=0d;
	 for(i2=0;i2<i1;i2++)
		 d1+=data.get(i2);
	 x3=d1/i1;//近期平均值
	 i1=Math.min(data.length,txi4);
	 d1=0d;
	 for(i2=0;i2<i1;i2++)
		 d1+=data.get(i2);
	 x4=d1/i1;//近期平均值
	 
	 if(data.length<tvi1)
		 v1=0d;
	 else
		 v1=v(data,tvi1);
	 if(data.length<tvi2)
		 v2=0d;
	 else
		 v2=v(data,tvi2);
	 if(data.length<tvi3)
		 v3=0d;
	 else
		 v3=v(data,tvi3);
	 if(data.length<tvi4)
		 v4=0d;
	 else
		 v4=v(data,tvi4);}
	
	double expect(double ra,int time)//预期  激进程度（即对短期内信息的信任程度）（0~1）
	{double d1=1d-ra,
			a1=d1*d1*d1,
			a2=3d*d1*d1*ra,
			a3=3d*d1*ra*ra,
			a4=ra*ra*ra;
	 return a1*(x1+v1*time)+a2*(x2+v2*time)+a3*(x3+v3*time)+a4*(x4+v4*time);}
	
	static int round(double d)
	{return (int)(d+0.5d);}
	
	static double v(Data data,int l)//使用最小二乘法求斜率  数据  求斜率部分的长度
	{double d1=0d,d2=0d,d3=0d,d4=0d,d5;
	 double x;
	 int i1;
	 for(i1=0;i1<l;i1++)
	 {x=data.get(i1);
	  d1+=i1;
	  d2+=x;
	  d3+=i1*i1;
	  d4+=i1*x;}
	 d5=1d/l;
	 d1*=d5;//<t>
	 d2*=d5;//<x>
	 d3*=d5;//<tt>
	 d4*=d5;//<tx>
	 return -(d4-d1*d2)/(d3-d1*d1);}
	
	static double k(Data data1,Data data2,int l)//使用最小二乘法求斜率  数据（自变量） 数据（因变量）  求斜率部分的长度
	{double d1=0d,d2=0d,d3=0d,d4=0d,d5;
	 double x,y;
	 int i1;
	 for(i1=0;i1<l;i1++)
	 {x=data1.get(i1);
	  y=data2.get(i1);
	  d1+=x;
	  d2+=y;
	  d3+=x*x;
	  d4+=x*y;}
	 d5=1d/l;
	 d1*=d5;//<x>
	 d2*=d5;//<y>
	 d3*=d5;//<xx>
	 d4*=d5;//<xy>
	 return (d4-d1*d2)/(d3-d1*d1);}
}
