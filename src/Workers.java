import java.util.Random;

public class Workers {  // 工人阶级整体

	static final double log6 = Math.log(6);
	static double aveWorkersNumber = 50;  // 平均雇佣工人数（雇用此工人数时人均工资=平均工资） 90
										  // 过低的值会导致工人工资上涨，资本家停产，经济迅速衰退
										  // 过高的值会导致经济在大时间尺度下衰退

	double number;  // 工人总量 // TODO: 这个量好像对系统的行为没有影响
	double avaNumber;  // 当前可用工人量
	double value = 170;  // 一个工人单位时间创造的价值
	double minSalary;  // （单个工人单位时间）愿意接受的最低工资
	double aveSalary;  // 人均工资（仅限有收入的人）
	Data income = new Data();  // 以往收入
	Expect expeIncome;  // 收入预期
	double temIncome;  // 单次的收入（不包括工业资本家采购原料的花费，尽管这部分花费进入了工人支付力）
	double temNumber;  // 单次获得工资的工人数乘工作时长
	double money;  // 当前支付力

	  // 所需
	double[] demand;  // （单位时间）需求量
	double[] pay_rate;  // 期望的支出占比（会随以往实际支出占比缓慢变化）
	double[] demand_elasticity;  // 需求弹性（0~1）（决策由需求量偏向支出期望的程度）

	public Workers(Society society)
	{
		this.demand = new double[society.productKinds];  // （单位时间）需求量
		this.pay_rate = new double[society.productKinds];  // 期望的支出占比（会随以往实际支出占比缓慢变化）
		this.demand_elasticity = new double[society.productKinds];
	}

	void setNeed(Society society, Random ra)  // 需求随机初始化（基于当前总产量）
	{
		int i1, pro_id;
		double d1;
		for (i1 = 0; i1 < society.capitalistNumbrt; i1++)
			if (society.cap[i1].pro_participant) {
				pro_id = society.cap[i1].pro_id;
				demand[pro_id] += (double) society.cap[i1].avaEqu_number * society.products[pro_id].equ_productivity
						/ society.products[pro_id].pro_time;
			}
		d1 = 0d;
		for (i1 = 0; i1 < society.productKinds; i1++) {
			pay_rate[i1] = demand[i1] * society.products[i1].avePrice;
			d1 += pay_rate[i1];
			demand_elasticity[i1] = ra.nextDouble();
		}
		d1 = 1d / d1;
		for (i1 = 0; i1 < society.productKinds; i1++) {
			pay_rate[i1] *= d1;
		}
	}

	double salary(double wn)  // 人均工资 单个资本家雇佣工人数
	{
		return minSalary + (aveSalary - minSalary) * Math.log(5d * wn / aveWorkersNumber + 1d) / log6;
	}

	void countIncome()  // 计算收入
	{
		if (temNumber != 0d)
			aveSalary = temIncome / temNumber;
		  // System.out.print(aveSalary+"="+temIncome+"/"+temNumber+",");
		income.put(temIncome);
		temIncome = 0d;
		temNumber = 0d;
		expeIncome = new Expect(income, 1d);
	}

	void buy(Society society)  // 消费
	{
		double[]
			da1 = new double[society.productKinds],
			da2 = new double[society.productKinds],
			da3 = new double[society.productKinds];
		Capitalist[] capitalists = new Capitalist[society.capitalistNumbrt];
		int i1, i2, i3,
				need;
		double d1, d2;

		d1 = 0d;
		for (i1 = 0; i1 < society.productKinds; i1++) {
			da1[i1] = demand[i1] * society.products[i1].avePrice;
			d1 += da1[i1];
			da2[i1] = pay_rate[i1] * money;
		}
		if (d1 > money) {
			d1 = money / d1;
			for (i1 = 0; i1 < society.productKinds; i1++)
				da1[i1] *= d1;
		}

		d2 = 0d;
		for (i1 = 0; i1 < society.productKinds; i1++)  // 购买
		{
			need = (int) ((da1[i1] * (1d - demand_elasticity[i1]) + da2[i1] * demand_elasticity[i1])
					/ society.products[i1].avePrice);
			da3[i1] = need * society.products[i1].avePrice;  // 该项支出
			d2 += da3[i1];
			i2 = 0;
			for (i3 = 0; i3 < society.capitalistNumbrt; i3++)
				if (society.cap[i3].pro_participant && society.cap[i3].pro_id == i1)
					capitalists[i2++] = society.cap[i3];
			if (i2 == 0)
				continue;
			sortCapitalists(capitalists, i2);
			for (i3 = 0; i3 < i2; i3++)
				if (capitalists[i3].stocks < need)  // 不够
				{
					d1 = capitalists[i3].stocks * capitalists[i3].price;
					capitalists[i3].income.put(d1);
					money -= d1;
					capitalists[i3].capital += d1;
					need -= capitalists[i3].stocks;
					  // System.out.println("cap["+capa[i3].id+"].stocks-="+capa[i3].stocks);
					capitalists[i3].stocks = 0;
				} else  // 够了
				{
					d1 = need * capitalists[i3].price;
					capitalists[i3].income.put(d1);
					money -= d1;
					capitalists[i3].capital += d1;
					  // System.out.println("cap["+capa[i3].id+"].stocks-="+need);
					capitalists[i3].stocks -= need;
					need = 0;
					break;
				}
			for (i3++; i3 < i2; i3++)  // 价格过高，无人购买，收入为0
				capitalists[i3].income.put(0d);
			for (i3++; i3 < i2; i3++)
				capitalists[i3].expeIncome = new Expect(capitalists[i3].income, 1d);
		}
		d2 = 1d / d2;
		for (i1 = 0; i1 < society.productKinds; i1++)  // 实际支出比例
			da3[i1] *= d2;
		for (i1 = 0; i1 < society.productKinds; i1++)  // 支出预期向实际支出偏移
			pay_rate[i1] += (da3[i1] - pay_rate[i1]) * 0.02d;
	}

	void sortCapitalists(Capitalist[] capa, int l)  // 按售价递增的顺序排列资本家
	{
		int i1, i2;
		boolean bo1;
		Capitalist cap;

		for (i1 = l - 1; i1 > 0; i1--)  // 冒泡排序
		{
			bo1 = true;
			for (i2 = 0; i2 < i1; i2++)
				if (capa[i2].price > capa[i2 + 1].price) {
					cap = capa[i2];
					capa[i2] = capa[i2 + 1];
					capa[i2 + 1] = cap;
					bo1 = false;
				}
			if (bo1)
				break;
		}
	}

	void print()  // 打印信息
	{
		System.out.println("工人总量：" + number);
		System.out.println("当前可用工人量：" + avaNumber);
		System.out.println("一个工人单位时间创造的价值：" + value);
		System.out.println("愿意接受的最低工资：" + minSalary);
		System.out.println("当前支付力：" + money);
	}

	void printNeed(Society society)  // 打印需求信息
	{
		int i1;
		for (i1 = 0; i1 < society.productKinds; i1++) {
			System.out.println(society.products[i1].name);
			System.out.println("  需求量：" + demand[i1]);
			if (society.products[i1].producible) {
				System.out.println("  期望的支出占比：" + pay_rate[i1]);
				System.out.println("  需求弹性：" + demand_elasticity[i1]);
			}
		}
	}
}
