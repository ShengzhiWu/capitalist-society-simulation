import java.util.Random;


public class Data{//���ݼ�¼��ѭ��ʹ��һ�����飩
	static final int maxlength=1000;
	
	private double[] array=new double[maxlength];
	int length=0;//�Ѽ�¼�����ݳ���
	private int p=maxlength-1;//��ǰָ��λ�ã�ָ�ŵ�ǰ���µ����ݣ�
	
	void put(double d1)//��һ������
	{p++;
	 if(p>=maxlength)
		 p=0;
	 array[p]=d1;
	 if(length<maxlength)
		 length++;}
	
	double get(int index)//ȡһ������
	{if(p>=index)
		 return array[p-index];
	 else
		 return array[maxlength-(index-p)];}
	
	void fill(double d1)//������ݣ����ڳ�ʼ��ʱα����ʷ
	{int i1;
	 for(i1=0;i1<maxlength;i1++)
		 array[i1]=d1;
	 length=maxlength;}
	
	void randomFill(Random ra,double d1,double d2)//���������ݣ����ڳ�ʼ��ʱα����ʷ  �����������  ��Сֵ  ���ֵ
	{int i1;
	 d2-=d1;
	 for(i1=0;i1<maxlength;i1++)
		 array[i1]=d1+ra.nextDouble()*d2;
	 length=maxlength;}
}
