import java.util.Random;


public class Product{//��Ʒ
	
	int id;//���
	String name;//����
	boolean producible;//��������
	double avePrice;//ƽ���۸�
	Data price;
	Expect expePrice;//�۸�Ԥ��
	
	//�豸
	double equ_price;//�豸�۸�
	int equ_productivity;//�豸������
	double equ_loss;//(����������)�豸���
	//ԭ����
	double rawMat_price;//����λ��Ʒ�ģ�ԭ���ϼ۸�
	
	//��ֵ
	int pro_time;//����ʱ��
	double workers_number;//һ̨�豸��ҵ������
	
	Product(int i1,String s1)//������
	{id=i1;
	 name=s1;
	 price=new Data();}
	
	void randomSet(Random ra)//�����ʼ��
	{producible=ra.nextFloat()<0.8f;
	 if(producible)
	 {equ_price=300d+20000d*Math.pow(ra.nextDouble(),2d);
	  equ_productivity=ra.nextInt(40)+1;
	  equ_loss=0.00005d+0.1d*Math.pow(ra.nextDouble(),4d);
	  rawMat_price=Math.exp(9d*ra.nextDouble());
	  pro_time=round(0.1d*Math.exp(9d*ra.nextDouble())+3d);
	  workers_number=6d*Math.exp(1.7d*ra.nextDouble());
	  avePrice=value();
	  price.fill(avePrice);}
	 else
	 {avePrice=Math.exp(9d*ra.nextDouble());
	  price.randomFill(ra,avePrice*0.6d,avePrice*1.5d);}
	 expePrice=new Expect(price,1d);}
	
	double addedValue()//����λ��Ʒ�ģ���ֵ��ֵ=����ʱ��*��ҵ������(δ��)*��λʱ���Ͷ���ֵ/�豸������
	{return pro_time*workers_number*Society.wor.value/equ_productivity;}
	
	double c()//�����ʱ�
	{return rawMat_price//ԭ����
			+equ_price*equ_loss/equ_productivity;/*�豸���*/}
	
	double value()//��λ��Ʒ�ļ�ֵ
	{return c()//�����ʱ�c
			+addedValue();/*v+m*/}
	
	double cost()//�ɱ�
	{return c()//c
			+Society.wor.aveSalary*workers_number*pro_time/equ_productivity;/*v*/}
	
	double profit()//ƽ������
	{return avePrice-cost();}
	
	double interestRate(double ra)//Ԥ������  �����̶�
	{double d1=cost();//�ɱ�
	 return (expePrice.expect(ra,pro_time)-d1)/d1;}
	
	void countAveragePrice()//������ۣ����ݲ�����Ȩƽ����
	{double d1=0d,d2=0d;
	 int i1;
	 Capitalist cap;
	 
	 for(i1=0;i1<Society.capn;i1++)
	 {cap=Society.cap[i1];
	  if(cap.pro_participant&&cap.pro_id==id)
	  {d1+=cap.stocks;
	   d2+=cap.stocks*cap.price;}}
	 if(d1==0)
		 return;
	 avePrice=d2/d1;
	 price.put(avePrice);
	 expePrice=new Expect(price,1d);}
	
	void print()//��ӡ��Ϣ
	{System.out.println(name);
	 System.out.println("�������ԣ�"+producible);
	 System.out.println("ƽ���۸�"+avePrice);
	 if(producible)
	 {System.out.println("�豸�۸�"+equ_price);
	  System.out.println("�豸�����ʣ�"+equ_productivity);
	  System.out.println("�豸��ģ�"+equ_loss);
	  System.out.println("ԭ���ϼ۸�"+rawMat_price);
	  System.out.println("����ʱ�䣺"+pro_time);
	  System.out.println("���蹤������"+workers_number);
	  System.out.println("��ֵ��ֵ��"+addedValue());
	  System.out.println("��ֵ��"+value());}}
	
	static int round(double d)
	{return (int)(d+0.5d);}
}
