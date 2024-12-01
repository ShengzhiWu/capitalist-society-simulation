import java.util.Random;


public class Capitalist {//资本家
	
	static final int cpt=30;//基于最近多少天的数据调整售价
	
	int id;//编号
	double capital;//当前可用资本（仅限货币）
	
	//生产
	boolean pro_participant;//是否直接参与生产
	int pro_id;//生产的产品
	double avaEqu_number;//当前空闲设备数（因为设备在生产过程中存在损耗，所以为分数）
	Produce prod=null;//当前进行中的生产链表
	int stocks;//库存量
	double price;//定价
	Data difPrice=new Data();//价格差额（定价-市场均价）
	Data income=new Data();//单个结算时间点的工业收入
	Expect expeIncome;//工业收入预期
	
	//借贷
	Loan loan=null;//贷款（向工人）链表
	
	//投机
	int pro_quantity[];//持有产品量（长度为产品种类数）
	
	Capitalist(int i1)//构造器
	{id=i1;}
	
	void randomSet(Random ra)//随机初始化
	{capital=10000d+50000d*Math.exp(2d*ra.nextDouble());
	 pro_participant=ra.nextFloat()<0.6f;
	 if(pro_participant)
	 {do
		  pro_id=ra.nextInt(Society.pron);
	  while(!Society.pro[pro_id].producible);
	  avaEqu_number=ra.nextInt(50)+1-ra.nextDouble();
	  stocks=round(capital/Society.pro[pro_id].avePrice*ra.nextDouble());}}
	
	void setPrice(Random ra)//制定售价（依据以往销售情况调整售价）
	{//if(id==0&&difPrice.length>=cpt)
	//	 System.out.print((float)Expect.k(difPrice,income,cpt)+",");
	 if(difPrice.length>=cpt)//已积累足够数据，依据以往数据调整
		 price*=Expect.k(difPrice,income,cpt)>0d?1d+ra.nextDouble()*0.01d:1d-ra.nextDouble()*0.01d;
	 else//未积累足够数据，随意调整
		 price=Society.pro[pro_id].avePrice*(ra.nextDouble()*0.2d+0.9d);
	 difPrice.put(price);}
	
	void finishProduce(Produce p1)//结束已到时间的生产
	{stocks+=p1.pro_quantity;
	 //System.out.println("cap["+id+"].stocks+="+p1.pro_quantity);
	 avaEqu_number+=p1.equ_number*(1d-Society.pro[pro_id].equ_loss);
	 Society.wor.avaNumber+=p1.workers_number;
	 //System.out.println("wor.temIncome+="+p1.salary);
	 }

	boolean finishLoan(Loan lo1)//回收已到期的贷款  返回是否回收成功（若无力偿还则回收失败，仅偿还部分，链表项不消失）
	{double d1=Math.min(lo1.value, Society.wor.money);
	 Society.wor.money-=d1;
	 lo1.value-=d1;
	 capital+=d1;
	 return lo1.value<=Society.wor.money;}
	
	void handleProduces()//倒计时，结束已到时间的生产，产品收入库存，回收设备，计算设备损耗，归还劳动力，支付工人工资
	{Produce p1,p2;
	 for(p1=prod;p1!=null;p1=p1.next)
	 {Society.wor.temNumber+=p1.workers_number;
	  Society.wor.temIncome+=p1.salary/p1.timelength;
	  Society.wor.money+=p1.salary/p1.timelength;//工资日结
	  p1.time--;/*倒计时*/}
	 while(prod!=null&&prod.time==0)
	 {finishProduce(prod);
	  prod=prod.next;}
	 if(prod==null)
		 return;
	 for(p2=prod,p1=prod.next;p1!=null;p2=p1,p1=p1.next)
		 if(p1.time==0)
		 {finishProduce(p1);
		  p2.next=p1.next;}}
	
	void handleLoans()//倒计时，回收已到期的贷款
	{Loan lo1,lo2;
	 for(lo1=loan;lo1!=null;lo1=lo1.next)
		 lo1.time--;
	 while(loan!=null&&loan.time==0)
		 if(finishLoan(loan))
			 loan=loan.next;
		 else//无力偿还
			 loan.time=1;
	 if(loan==null)
		 return;
	 for(lo2=loan,lo1=loan.next;lo1!=null;lo2=lo1,lo1=lo1.next)
		 if(lo1.time==0)
			 if(finishLoan(loan))
				 lo2.next=lo1.next;
			 else//无力偿还
				 lo1.time=1;}
	
	void invest(Random ra)//投资
	{double d1,//工业生产
			d2,//借贷
			d3,//投机
			d4,d5,d6,
			cost,
			c;
	 int i1,i2;
	 Product pro1=null,pro2;
	 
	 if(pro_participant)
	 {pro1=Society.pro[pro_id];
	  d1=Math.pow(pro1.interestRate(ra.nextDouble())+1d,1d/pro1.pro_time)-1d;/*工业生产单位时间的利率*/
	 //System.out.println("工业："+pro1.interestRate(0.5d));
	 }
	 else
		 d1=0d;
	 //if(id==0)
	//	 System.out.print((float)d1+",");
	 d6=Loan.interestRate(ra.nextDouble());//借贷利率
	 d2=Math.pow(d6+1d,1d/Loan.loanTerm)-1d;//借贷单位时间的利率
	 																		  //（因为过于激进的利率预期可能会<-1，所以可能会得到NaN）
	 d3=Double.NEGATIVE_INFINITY;
	 i2=0;
	 for(i1=0;i1<Society.pron;i1++)//寻找最佳投机产品，计算投机单位时间的利率
	 {pro2=Society.pro[i1];
	  if(!pro2.producible)//不可生产
	  {d4=ra.nextDouble();
	   d5=pro2.expePrice.expect(d4,0);
	   d5=(pro2.expePrice.expect(d4,1)-d5)/d5;
	   if(d5>d3)
	   {d3=d5;
	    i2=i1;}}}
	 
	 if(d1<0||!Double.isFinite(d1))
		 d1=0d;
	 if(d2<0||!Double.isFinite(d2))
		 d2=0d;
	 if(d3<0||!Double.isFinite(d3))
		 d3=0d;
	 d2=0d;/////////////////////////////////////////////信贷禁止
	 d3=0d;/////////////////////////////////////////////投机禁止
	 
	 d4=d1+d2+d3;
	 if(d4!=0)//有利可图
	 {d6=d2;
	  d4=capital/d4;
	  d1*=d4;//工业生产投资
	  d2*=d4;//借贷投资
	  d3*=d4;//投机投资
	  //if(pro_participant)
		  //System.out.println("工业生产投资["+id+"]="+d1+" "+pro1.cost());
	  
	  if(d1!=0)//工业生产投资
	  {//System.out.println("工业生产投资："+d1);
	   cost=pro1.cost();
	   d4=d1/cost;//目标生产规模
	   d5=avaEqu_number*pro1.equ_productivity;//生产能力
	   //System.out.println(" 目标生产规模："+d4);
	   //System.out.println(" 生产能力："+d5);
	   if(d4>d5)//一部分资金用于购置新设备
	   {i1=(int)((d4-d5)*cost/pro1.equ_price);//用于购置新设备的量
	    //System.out.println("购置设备数["+id+"]="+i1);
	    capital-=i1*pro1.equ_price;//将资本用于购置新设备
	    //Society.wor.money+=i1*pro1.equ_price;//为了防止货币从系统流出，假定资本家花费的不变资本全部进入工人收入
	    d1-=i1*pro1.equ_price;
	    avaEqu_number+=i1;
	    d4=d5;}
	   i1=(int)(d4/pro1.equ_productivity);//目标开动设备数（由于工人人均工资与雇佣工人数正相关，所以实际开动设备数可能会发生变化）
	   //System.out.println(" 目标开动设备数："+i1);
	   c=pro1.c()*pro1.equ_productivity;//开动一台机器所需要的不变资本
	   for(;c*i1+Society.wor.salary(i1*pro1.workers_number)*i1*pro1.workers_number*pro1.pro_time<d1;i1++);//根据劳动力价格调整开动设备数
	   for(;(c*i1+Society.wor.salary(i1*pro1.workers_number)*i1*pro1.workers_number*pro1.pro_time>d1||i1>avaEqu_number)&&i1>0;i1--);//根据劳动力价格调整开动设备数
	   //System.out.println(" 开动设备数："+i1);
	   capital-=c*i1+Society.wor.salary(i1*pro1.workers_number)*i1*pro1.workers_number*pro1.pro_time;//将资本用于进行生产
	   Society.wor.money+=c*i1;//为了防止货币从系统流出，假定资本家花费的不变资本全部进入工人收入
	   //System.out.println("c/E="+pro1.c()+"*"+pro1.equ_productivity+"="+c);
	   //System.out.println("wor.temIncome+="+c+"*"+i1+"="+c*i1);
	   avaEqu_number-=i1;
	   Society.wor.avaNumber-=i1*pro1.workers_number;
	   //System.out.println("雇佣工人数["+id+"]="+i1*pro1.workers_number);
	   prod=new Produce(i1*pro1.equ_productivity,i1,i1*pro1.workers_number,Society.wor.salary(i1*pro1.workers_number),pro1.pro_time,prod);/*头插进行中的生产链表*/}
	  
	  if(d2!=0)//借贷投资
	  {//System.out.println("借贷投资："+d2);
	   d6=d2*(1+d6);
	   capital-=d2;
	   Society.wor.money+=d2;
	   loan=new Loan(d6,Loan.loanTerm,loan);/*头插贷款（向工人）链表链表*/}
	  
	  if(d3!=0)//投机投资
	  {System.out.println("投机投资："+d3);
	  ///////////////////////////////////////////////////////////
	  }}
	 
	 /*试图卖出投机品*/}
	
	void print()//打印信息
	{System.out.println("当前可用资本："+capital);
	 System.out.println("是否直接参与生产："+pro_participant);
	 if(pro_participant)
	 {System.out.println("生产的产品："+Society.pro[pro_id].name);
	  System.out.println("当前空闲设备数："+avaEqu_number);
	  System.out.println("库存量："+stocks);
	  System.out.println("定价："+price);}}
	
	static int round(double d)
	{return (int)(d+0.5d);}
}
