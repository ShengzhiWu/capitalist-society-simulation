import java.util.Random;

public class Society {  // 社会

	int pron = 3;  // 产品种类数
	Product[] pro = new Product[pron];  // 产品
	int capn = 10;  // 资本家个数
	Capitalist[] cap = new Capitalist[capn];  // 资本家
	Workers wor;  // 工人

	double aveInterestRates;  // 平均利率

	Random ra;  // 随机数发生器

	public Society() {
		this.wor = new Workers(this);
	}

	public void run(int steps) {
		int i1, i2;

		  //
		/*
		 * Data d=new Data();
		 * d.put(277.08641857975994);
		 * d.put(289.86120326384565);
		 * d.put(274.34406699162355);
		 * d.put(265.20581363236477);
		 * d.put(253.93373602882875);
		 * d.put(244.06911672350768);
		 * System.out.println(new Expect(d,1d).expect(0.5d, 4));
		 * System.exit(0);
		 */

		ra = new Random(3);  // 产品初始化
		for (i1 = 0; i1 < pron; i1++) {
			pro[i1] = new Product(i1, "产品" + (char) ('A' + i1));
			pro[i1].initialize(this, ra);
			pro[i1].print(this);
			System.out.println();
		}

		ra = new Random(16);  // 资本家初始化
		for (i1 = 0; i1 < capn; i1++) {
			cap[i1] = new Capitalist(i1);
			cap[i1].initialize(this, ra);
			cap[i1].setPrice(this, ra);
			cap[i1].print(this);
			System.out.println();
		}

		ra = new Random(16);  // 工人整体初始化 16
		wor.number = 0d;
		wor.money = 0d;
		for (i1 = 0; i1 < capn; i1++) {
			if (!cap[i1].pro_participant)
				continue;
			wor.number += cap[i1].avaEqu_number * pro[cap[i1].pro_id].workers_number;
			wor.money += cap[i1].avaEqu_number * pro[cap[i1].pro_id].equ_productivity
					* (pro[cap[i1].pro_id].avePrice - pro[cap[i1].pro_id].c()) / pro[cap[i1].pro_id].pro_time;
		}
		wor.aveSalary = wor.money / wor.number * 0.7d;
		wor.number *= 2d;  // 工人盈余
		wor.avaNumber = wor.number;
		wor.minSalary = wor.value * 0.3d;
		wor.income.randomFill(ra, wor.money * 0.8d, wor.money * 1.2d);
		wor.expeIncome = new Expect(wor.income, 1d);
		wor.print();
		wor.setNeed(this, ra);
		wor.printNeed(this);
		System.out.println();

		ra = new Random(5);
		for (i1 = 0; i1 < steps; i1++) {  // System.out.print((float)pro[0].avePrice+",");
										  // System.out.print((float)cap[0].capital+",");
										  // System.out.print((float)cap[0].avaEqu_number+",");
			System.out.print(cap[0].stocks + ",");
			for (i2 = 0; i2 < capn; i2++) {
				if (cap[i2].pro_participant) {
					cap[i2].setPrice(this, ra);  // 工业资本家制定售价
					cap[i2].handleProduces(this);
				}
				cap[i2].handleLoans(this);
				cap[i2].invest(this, ra);
			}   // 资本家投资
			for (i2 = 0; i2 < pron; i2++)
				pro[i2].countAveragePrice(this);  // 重算市场均价
			  // System.out.print((float)cap[0].price+",");
			  // System.out.print((float)wor.temNumber+",");
			wor.countIncome();
			  // System.out.print((float)wor.aveSalary+",");
			  // System.out.print((float)wor.money+",");
			wor.buy(this);  // 工人消费
			  // System.out.println();
		}

		System.out.println();
	}

}
