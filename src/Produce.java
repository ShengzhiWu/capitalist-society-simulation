
public class Produce{//���ڽ����е�����
	
	int pro_quantity;//����
	int equ_number;//ռ���豸��
	double workers_number;//ռ�ù�����
	double salary;//�ܹ���

	int timelength;//��ʱ��
	int time;//ʣ��ʱ��
	
	Produce next;//������һ��
	
	Produce(int i1,int i2,double d1,double d2,int i3,Produce pr1)//������  ����  �豸��  ������  ���˵�λʱ��ƽ������  ʱ��  ������һ��
	{pro_quantity=i1;
	 equ_number=i2;
	 workers_number=d1;
	 //System.out.println("����:"+i1);
	 //System.out.println("��Ӷ������:"+d1);
	 //System.out.println("ƽ������:"+d2);
	 salary=d1*d2*i3;
	 time=i3;
	 timelength=i3;
	 next=pr1;}
}
