
public class Loan {  // 贷款（向工人）

	static int loanTerm = 240;  // 贷款期限

	double value;  // 应归还量（连本带息）

	int time;  // 剩余时间

	Loan next;  // 链表下一项

	Loan(double d1, int i1, Loan lo1)  // 构造器
	{
		value = d1;
		time = i1;
		next = lo1;
	}

	static double interestRate(Society society, double ra)  // 利率 激进程度
	{
		double d1 = society.wor.expeIncome.expect(ra, 0);
		return (society.wor.expeIncome.expect(ra, loanTerm) - d1) / d1;
	}
}
