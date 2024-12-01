import java.util.Random;


public class Workers {//��������
	
	static final double log6=Math.log(6);
	static double aveWorkersNumber=50;//ƽ����Ӷ�����������ô˹�����ʱ�˾�����=ƽ�����ʣ� 90
										//���͵�ֵ�ᵼ�¹��˹������ǣ��ʱ���ͣ��������Ѹ��˥��
										//���ߵ�ֵ�ᵼ�¾����ڴ�ʱ��߶���˥��
	
	double number;//��������
	double avaNumber;//��ǰ���ù�����
	double value=170;//һ�����˵�λʱ�䴴��ļ�ֵ
	double minSalary;//���������˵�λʱ�䣩Ը����ܵ���͹���
	double aveSalary;//�˾����ʣ�������������ˣ�
	Data income=new Data();//��������
	Expect expeIncome;//����Ԥ��
	double temIncome;//���ε����루��������ҵ�ʱ��Ҳɹ�ԭ�ϵĻ��ѣ������ⲿ�ֻ��ѽ����˹���֧������
	double temNumber;//���λ�ù��ʵĹ������˹���ʱ��
	double money;//��ǰ֧����
	
	//����
	double[] demand=new double[Society.pron];//����λʱ�䣩������
	double[] pay_rate=new double[Society.pron];//������֧��ռ�ȣ���������ʵ��֧��ռ�Ȼ����仯��
	double[] demand_elasticity=new double[Society.pron];//�����ԣ�0~1����������������ƫ��֧�������ĳ̶ȣ�
	
	void setNeed(Random ra)//���������ʼ�������ڵ�ǰ�ܲ�����
	{int i1,pro_id;
	 double d1;
	 for(i1=0;i1<Society.capn;i1++)
		 if(Society.cap[i1].pro_participant)
		 {pro_id=Society.cap[i1].pro_id;
		  demand[pro_id]+=(double)Society.cap[i1].avaEqu_number*Society.pro[pro_id].equ_productivity/Society.pro[pro_id].pro_time;}
	 d1=0d;
	 for(i1=0;i1<Society.pron;i1++)
	 {pay_rate[i1]=demand[i1]*Society.pro[i1].avePrice;
	  d1+=pay_rate[i1];
	  demand_elasticity[i1]=ra.nextDouble();}
	 d1=1d/d1;
	 for(i1=0;i1<Society.pron;i1++)
	 {pay_rate[i1]*=d1;}}
	
	double salary(double wn)//�˾�����  �����ʱ��ҹ�Ӷ������
	{return minSalary+(aveSalary-minSalary)*Math.log(5d*wn/aveWorkersNumber+1d)/log6;}
	
	void countIncome()//��������
	{if(temNumber!=0d)
		 aveSalary=temIncome/temNumber;
	 //System.out.print(aveSalary+"="+temIncome+"/"+temNumber+",");
	 income.put(temIncome);
	 temIncome=0d;
	 temNumber=0d;
	 expeIncome=new Expect(income,1d);}
	
	void buy()//����
	{double[] da1=new double[Society.pron],
			  da2=new double[Society.pron],
			  da3=new double[Society.pron];
	 Capitalist[] capa=new Capitalist[Society.capn];
	 int i1,i2,i3,
	 	 need;
	 double d1,d2;

	 d1=0d;
	 for(i1=0;i1<Society.pron;i1++)
	 {da1[i1]=demand[i1]*Society.pro[i1].avePrice;
	  d1+=da1[i1];
	  da2[i1]=pay_rate[i1]*money;}
	 if(d1>money)
	 {d1=money/d1;
	  for(i1=0;i1<Society.pron;i1++)
		  da1[i1]*=d1;}

	 d2=0d;
	 for(i1=0;i1<Society.pron;i1++)//����
	 {need=(int)((da1[i1]*(1d-demand_elasticity[i1])+da2[i1]*demand_elasticity[i1])/Society.pro[i1].avePrice);
	  da3[i1]=need*Society.pro[i1].avePrice;//����֧��
	  d2+=da3[i1];
	  i2=0;
	  for(i3=0;i3<Society.capn;i3++)
		  if(Society.cap[i3].pro_participant&&Society.cap[i3].pro_id==i1)
			  capa[i2++]=Society.cap[i3];
	  if(i2==0)
		  continue;
	  sortCapitalists(capa,i2);
	  for(i3=0;i3<i2;i3++)
		  if(capa[i3].stocks<need)//����
		  {d1=capa[i3].stocks*capa[i3].price;
		   capa[i3].income.put(d1);
		   money-=d1;
		   capa[i3].capital+=d1;
		   need-=capa[i3].stocks;
		   //System.out.println("cap["+capa[i3].id+"].stocks-="+capa[i3].stocks);
		   capa[i3].stocks=0;}
		  else//����
		  {d1=need*capa[i3].price;
		   capa[i3].income.put(d1);
		   money-=d1;
		   capa[i3].capital+=d1;
		   //System.out.println("cap["+capa[i3].id+"].stocks-="+need);
		   capa[i3].stocks-=need;
		   need=0;
		   break;}
	  for(i3++;i3<i2;i3++)//�۸���ߣ����˹�������Ϊ0
		  capa[i3].income.put(0d);
	  for(i3++;i3<i2;i3++)
		  capa[i3].expeIncome=new Expect(capa[i3].income,1d);}
	 d2=1d/d2;
	 for(i1=0;i1<Society.pron;i1++)//ʵ��֧������
		 da3[i1]*=d2;
	 for(i1=0;i1<Society.pron;i1++)//֧��Ԥ����ʵ��֧��ƫ��
		 pay_rate[i1]+=(da3[i1]-pay_rate[i1])*0.02d;}
	
	void sortCapitalists(Capitalist[] capa,int l)//���ۼ۵�����˳�������ʱ���
	{int i1,i2;
	 boolean bo1;
	 Capitalist cap;
	 
	 for(i1=l-1;i1>0;i1--)//ð������
	 {bo1=true;
	  for(i2=0;i2<i1;i2++)
		  if(capa[i2].price>capa[i2+1].price)
		  {cap=capa[i2];
		   capa[i2]=capa[i2+1];
		   capa[i2+1]=cap;
		   bo1=false;}
	  if(bo1)
		  break;}}
	
	void print()//��ӡ��Ϣ
	{System.out.println("����������"+number);
	 System.out.println("��ǰ���ù�������"+avaNumber);
	 System.out.println("һ�����˵�λʱ�䴴��ļ�ֵ��"+value);
	 System.out.println("Ը����ܵ���͹��ʣ�"+minSalary);
	 System.out.println("��ǰ֧������"+money);}
	
	void printNeed()//��ӡ������Ϣ
	{int i1;
	 for(i1=0;i1<Society.pron;i1++)
	 {System.out.println(Society.pro[i1].name);
	  System.out.println("  ��������"+demand[i1]);
	  if(Society.pro[i1].producible)
	  {System.out.println("  ������֧��ռ�ȣ�"+pay_rate[i1]);
	   System.out.println("  �����ԣ�"+demand_elasticity[i1]);}}}
}
