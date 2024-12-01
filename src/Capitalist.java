import java.util.Random;


public class Capitalist {//�ʱ���
	
	static final int cpt=30;//�����������������ݵ����ۼ�
	
	int id;//���
	double capital;//��ǰ�����ʱ������޻��ң�
	
	//����
	boolean pro_participant;//�Ƿ�ֱ�Ӳ�������
	int pro_id;//�����Ĳ�Ʒ
	double avaEqu_number;//��ǰ�����豸������Ϊ�豸�����������д�����ģ�����Ϊ������
	Produce prod=null;//��ǰ�����е���������
	int stocks;//�����
	double price;//����
	Data difPrice=new Data();//�۸������-�г����ۣ�
	Data income=new Data();//��������ʱ���Ĺ�ҵ����
	Expect expeIncome;//��ҵ����Ԥ��
	
	//���
	Loan loan=null;//������ˣ�����
	
	//Ͷ��
	int pro_quantity[];//���в�Ʒ��������Ϊ��Ʒ��������
	
	Capitalist(int i1)//������
	{id=i1;}
	
	void randomSet(Random ra)//�����ʼ��
	{capital=10000d+50000d*Math.exp(2d*ra.nextDouble());
	 pro_participant=ra.nextFloat()<0.6f;
	 if(pro_participant)
	 {do
		  pro_id=ra.nextInt(Society.pron);
	  while(!Society.pro[pro_id].producible);
	  avaEqu_number=ra.nextInt(50)+1-ra.nextDouble();
	  stocks=round(capital/Society.pro[pro_id].avePrice*ra.nextDouble());}}
	
	void setPrice(Random ra)//�ƶ��ۼۣ���������������������ۼۣ�
	{//if(id==0&&difPrice.length>=cpt)
	//	 System.out.print((float)Expect.k(difPrice,income,cpt)+",");
	 if(difPrice.length>=cpt)//�ѻ����㹻���ݣ������������ݵ���
		 price*=Expect.k(difPrice,income,cpt)>0d?1d+ra.nextDouble()*0.01d:1d-ra.nextDouble()*0.01d;
	 else//δ�����㹻���ݣ��������
		 price=Society.pro[pro_id].avePrice*(ra.nextDouble()*0.2d+0.9d);
	 difPrice.put(price);}
	
	void finishProduce(Produce p1)//�����ѵ�ʱ�������
	{stocks+=p1.pro_quantity;
	 //System.out.println("cap["+id+"].stocks+="+p1.pro_quantity);
	 avaEqu_number+=p1.equ_number*(1d-Society.pro[pro_id].equ_loss);
	 Society.wor.avaNumber+=p1.workers_number;
	 //System.out.println("wor.temIncome+="+p1.salary);
	 }

	boolean finishLoan(Loan lo1)//�����ѵ��ڵĴ���  �����Ƿ���ճɹ������������������ʧ�ܣ����������֣��������ʧ��
	{double d1=Math.min(lo1.value, Society.wor.money);
	 Society.wor.money-=d1;
	 lo1.value-=d1;
	 capital+=d1;
	 return lo1.value<=Society.wor.money;}
	
	void handleProduces()//����ʱ�������ѵ�ʱ�����������Ʒ�����棬�����豸�������豸��ģ��黹�Ͷ�����֧�����˹���
	{Produce p1,p2;
	 for(p1=prod;p1!=null;p1=p1.next)
	 {Society.wor.temNumber+=p1.workers_number;
	  Society.wor.temIncome+=p1.salary/p1.timelength;
	  Society.wor.money+=p1.salary/p1.timelength;//�����ս�
	  p1.time--;/*����ʱ*/}
	 while(prod!=null&&prod.time==0)
	 {finishProduce(prod);
	  prod=prod.next;}
	 if(prod==null)
		 return;
	 for(p2=prod,p1=prod.next;p1!=null;p2=p1,p1=p1.next)
		 if(p1.time==0)
		 {finishProduce(p1);
		  p2.next=p1.next;}}
	
	void handleLoans()//����ʱ�������ѵ��ڵĴ���
	{Loan lo1,lo2;
	 for(lo1=loan;lo1!=null;lo1=lo1.next)
		 lo1.time--;
	 while(loan!=null&&loan.time==0)
		 if(finishLoan(loan))
			 loan=loan.next;
		 else//��������
			 loan.time=1;
	 if(loan==null)
		 return;
	 for(lo2=loan,lo1=loan.next;lo1!=null;lo2=lo1,lo1=lo1.next)
		 if(lo1.time==0)
			 if(finishLoan(loan))
				 lo2.next=lo1.next;
			 else//��������
				 lo1.time=1;}
	
	void invest(Random ra)//Ͷ��
	{double d1,//��ҵ����
			d2,//���
			d3,//Ͷ��
			d4,d5,d6,
			cost,
			c;
	 int i1,i2;
	 Product pro1=null,pro2;
	 
	 if(pro_participant)
	 {pro1=Society.pro[pro_id];
	  d1=Math.pow(pro1.interestRate(ra.nextDouble())+1d,1d/pro1.pro_time)-1d;/*��ҵ������λʱ�������*/
	 //System.out.println("��ҵ��"+pro1.interestRate(0.5d));
	 }
	 else
		 d1=0d;
	 //if(id==0)
	//	 System.out.print((float)d1+",");
	 d6=Loan.interestRate(ra.nextDouble());//�������
	 d2=Math.pow(d6+1d,1d/Loan.loanTerm)-1d;//�����λʱ�������
	 																		  //����Ϊ���ڼ���������Ԥ�ڿ��ܻ�<-1�����Կ��ܻ�õ�NaN��
	 d3=Double.NEGATIVE_INFINITY;
	 i2=0;
	 for(i1=0;i1<Society.pron;i1++)//Ѱ�����Ͷ����Ʒ������Ͷ����λʱ�������
	 {pro2=Society.pro[i1];
	  if(!pro2.producible)//��������
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
	 d2=0d;/////////////////////////////////////////////�Ŵ���ֹ
	 d3=0d;/////////////////////////////////////////////Ͷ����ֹ
	 
	 d4=d1+d2+d3;
	 if(d4!=0)//������ͼ
	 {d6=d2;
	  d4=capital/d4;
	  d1*=d4;//��ҵ����Ͷ��
	  d2*=d4;//���Ͷ��
	  d3*=d4;//Ͷ��Ͷ��
	  //if(pro_participant)
		  //System.out.println("��ҵ����Ͷ��["+id+"]="+d1+" "+pro1.cost());
	  
	  if(d1!=0)//��ҵ����Ͷ��
	  {//System.out.println("��ҵ����Ͷ�ʣ�"+d1);
	   cost=pro1.cost();
	   d4=d1/cost;//Ŀ��������ģ
	   d5=avaEqu_number*pro1.equ_productivity;//��������
	   //System.out.println(" Ŀ��������ģ��"+d4);
	   //System.out.println(" ����������"+d5);
	   if(d4>d5)//һ�����ʽ����ڹ������豸
	   {i1=(int)((d4-d5)*cost/pro1.equ_price);//���ڹ������豸����
	    //System.out.println("�����豸��["+id+"]="+i1);
	    capital-=i1*pro1.equ_price;//���ʱ����ڹ������豸
	    //Society.wor.money+=i1*pro1.equ_price;//Ϊ�˷�ֹ���Ҵ�ϵͳ�������ٶ��ʱ��һ��ѵĲ����ʱ�ȫ�����빤������
	    d1-=i1*pro1.equ_price;
	    avaEqu_number+=i1;
	    d4=d5;}
	   i1=(int)(d4/pro1.equ_productivity);//Ŀ�꿪���豸�������ڹ����˾��������Ӷ����������أ�����ʵ�ʿ����豸�����ܻᷢ���仯��
	   //System.out.println(" Ŀ�꿪���豸����"+i1);
	   c=pro1.c()*pro1.equ_productivity;//����һ̨��������Ҫ�Ĳ����ʱ�
	   for(;c*i1+Society.wor.salary(i1*pro1.workers_number)*i1*pro1.workers_number*pro1.pro_time<d1;i1++);//�����Ͷ����۸���������豸��
	   for(;(c*i1+Society.wor.salary(i1*pro1.workers_number)*i1*pro1.workers_number*pro1.pro_time>d1||i1>avaEqu_number)&&i1>0;i1--);//�����Ͷ����۸���������豸��
	   //System.out.println(" �����豸����"+i1);
	   capital-=c*i1+Society.wor.salary(i1*pro1.workers_number)*i1*pro1.workers_number*pro1.pro_time;//���ʱ����ڽ�������
	   Society.wor.money+=c*i1;//Ϊ�˷�ֹ���Ҵ�ϵͳ�������ٶ��ʱ��һ��ѵĲ����ʱ�ȫ�����빤������
	   //System.out.println("c/E="+pro1.c()+"*"+pro1.equ_productivity+"="+c);
	   //System.out.println("wor.temIncome+="+c+"*"+i1+"="+c*i1);
	   avaEqu_number-=i1;
	   Society.wor.avaNumber-=i1*pro1.workers_number;
	   //System.out.println("��Ӷ������["+id+"]="+i1*pro1.workers_number);
	   prod=new Produce(i1*pro1.equ_productivity,i1,i1*pro1.workers_number,Society.wor.salary(i1*pro1.workers_number),pro1.pro_time,prod);/*ͷ������е���������*/}
	  
	  if(d2!=0)//���Ͷ��
	  {//System.out.println("���Ͷ�ʣ�"+d2);
	   d6=d2*(1+d6);
	   capital-=d2;
	   Society.wor.money+=d2;
	   loan=new Loan(d6,Loan.loanTerm,loan);/*ͷ�������ˣ���������*/}
	  
	  if(d3!=0)//Ͷ��Ͷ��
	  {System.out.println("Ͷ��Ͷ�ʣ�"+d3);
	  ///////////////////////////////////////////////////////////
	  }}
	 
	 /*��ͼ����Ͷ��Ʒ*/}
	
	void print()//��ӡ��Ϣ
	{System.out.println("��ǰ�����ʱ���"+capital);
	 System.out.println("�Ƿ�ֱ�Ӳ���������"+pro_participant);
	 if(pro_participant)
	 {System.out.println("�����Ĳ�Ʒ��"+Society.pro[pro_id].name);
	  System.out.println("��ǰ�����豸����"+avaEqu_number);
	  System.out.println("�������"+stocks);
	  System.out.println("���ۣ�"+price);}}
	
	static int round(double d)
	{return (int)(d+0.5d);}
}
