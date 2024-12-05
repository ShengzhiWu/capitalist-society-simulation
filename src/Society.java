import java.util.Random;

public class Society {  // 社会

	int productKinds = 3;  // 产品种类数
	Product[] products = new Product[productKinds];  // 产品
	int capitalistNumbrt = 10;  // 资本家个数
	Capitalist[] cap = new Capitalist[capitalistNumbrt];  // 资本家
	Workers workers;  // 工人

	double aveInterestRates;  // 平均利率

	Random ra;  // 随机数发生器

	public Society() {
		this.workers = new Workers(this);
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
		for (i1 = 0; i1 < productKinds; i1++) {
			products[i1] = new Product(i1, "产品" + (char) ('A' + i1));
			products[i1].initialize(this, ra);
			products[i1].print(this);
			System.out.println();
		}

		ra = new Random(16);  // 资本家初始化
		for (i1 = 0; i1 < capitalistNumbrt; i1++) {
			cap[i1] = new Capitalist(i1);
			cap[i1].initialize(this, ra);
			cap[i1].setPrice(this, ra);
			cap[i1].print(this);
			System.out.println();
		}

		ra = new Random(16);  // 工人整体初始化 16
		workers.number = 0d;
		workers.money = 0d;
		for (i1 = 0; i1 < capitalistNumbrt; i1++) {
			if (!cap[i1].pro_participant)
				continue;
			workers.number += cap[i1].avaEqu_number * products[cap[i1].pro_id].workers_number;
			workers.money += cap[i1].avaEqu_number * products[cap[i1].pro_id].equ_productivity
					* (products[cap[i1].pro_id].avePrice - products[cap[i1].pro_id].c()) / products[cap[i1].pro_id].pro_time;
		}
		workers.aveSalary = workers.money / workers.number * 0.7d;
		workers.number *= 2d;  // 工人盈余
		workers.avaNumber = workers.number;
		workers.minSalary = workers.value * 0.3d;
		workers.income.randomFill(ra, workers.money * 0.8d, workers.money * 1.2d);
		workers.expeIncome = new Expect(workers.income, 1d);
		workers.print();
		workers.setNeed(this, ra);
		workers.printNeed(this);
		System.out.println();

		ra = new Random(5);
		for (i1 = 0; i1 < steps; i1++) {  // System.out.print((float)pro[0].avePrice+",");
										  // System.out.print((float)cap[0].capital+",");
										  // System.out.print((float)cap[0].avaEqu_number+",");
			System.out.print(cap[0].stocks + ",");
			for (i2 = 0; i2 < capitalistNumbrt; i2++) {
				if (cap[i2].pro_participant) {
					cap[i2].setPrice(this, ra);  // 工业资本家制定售价
					cap[i2].updateProduces(this);  // 结束已到时间的生产，产品收入库存，回收设备，计算设备损耗，归还劳动力，支付工人工资
				}
				cap[i2].updateLoans(this);  // 更新借贷
				cap[i2].invest(this, ra);  // 投资
			}   // 资本家投资
			for (i2 = 0; i2 < productKinds; i2++)
				products[i2].countAveragePrice(this);  // 重算市场均价
			  // System.out.print((float)cap[0].price+",");
			  // System.out.print((float)wor.temNumber+",");
			workers.countIncome();
			  // System.out.print((float)wor.aveSalary+",");
			  // System.out.print((float)wor.money+",");
			workers.buy(this);  // 工人消费
			  // System.out.println();
		}

		System.out.println();
	}

}
