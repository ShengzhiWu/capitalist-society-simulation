
public class Loan {//������ˣ�

	static int loanTerm=240;//��������
	
	double value;//Ӧ�黹����������Ϣ��
	
	int time;//ʣ��ʱ��
	
	Loan next;//������һ��
	
	Loan(double d1,int i1,Loan lo1)//������
	{value=d1;
	 time=i1;
	 next=lo1;}
	
	static double interestRate(double ra)//����  �����̶�
	{double d1=Society.wor.expeIncome.expect(ra,0);
	 return (Society.wor.expeIncome.expect(ra,loanTerm)-d1)/d1;}
}
