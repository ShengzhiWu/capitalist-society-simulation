import java.util.Random;

public class Product {// 产品

	int id;// 编号
	String name;// 名称
	boolean producible;// 可生产性
	double avePrice;// 平均价格
	Data price;
	Expect expePrice;// 价格预期

	// 设备
	double equ_price;// 设备价格
	int equ_productivity;// 设备生产率
	double equ_loss;// (单次生产的)设备损耗
	// 原材料
	double rawMat_price;// （单位产品的）原材料价格

	// 价值
	int pro_time;// 生产时间
	double workers_number;// 一台设备作业工人数

	Product(int i1, String s1)// 构造器
	{
		id = i1;
		name = s1;
		price = new Data();
	}

	void randomSet(Random ra)// 随机初始化
	{
		producible = ra.nextFloat() < 0.8f;
		if (producible) {
			equ_price = 300d + 20000d * Math.pow(ra.nextDouble(), 2d);
			equ_productivity = ra.nextInt(40) + 1;
			equ_loss = 0.00005d + 0.1d * Math.pow(ra.nextDouble(), 4d);
			rawMat_price = Math.exp(9d * ra.nextDouble());
			pro_time = round(0.1d * Math.exp(9d * ra.nextDouble()) + 3d);
			workers_number = 6d * Math.exp(1.7d * ra.nextDouble());
			avePrice = value();
			price.fill(avePrice);
		} else {
			avePrice = Math.exp(9d * ra.nextDouble());
			price.randomFill(ra, avePrice * 0.6d, avePrice * 1.5d);
		}
		expePrice = new Expect(price, 1d);
	}

	double addedValue()// （单位产品的）价值增值=生产时间*作业工人数(未定)*单位时间劳动价值/设备生产率
	{
		return pro_time * workers_number * Society.wor.value / equ_productivity;
	}

	double c()// 不变资本
	{
		return rawMat_price// 原材料
				+ equ_price * equ_loss / equ_productivity;
		/* 设备损耗 */}

	double value()// 单位产品的价值
	{
		return c()// 不变资本c
				+ addedValue();
		/* v+m */}

	double cost()// 成本
	{
		return c()// c
				+ Society.wor.aveSalary * workers_number * pro_time / equ_productivity;
		/* v */}

	double profit()// 平均利润
	{
		return avePrice - cost();
	}

	double interestRate(double ra)// 预期利率 激进程度
	{
		double d1 = cost();// 成本
		return (expePrice.expect(ra, pro_time) - d1) / d1;
	}

	void countAveragePrice()// 计算均价（依据产量加权平均）
	{
		double d1 = 0d, d2 = 0d;
		int i1;
		Capitalist cap;

		for (i1 = 0; i1 < Society.capn; i1++) {
			cap = Society.cap[i1];
			if (cap.pro_participant && cap.pro_id == id) {
				d1 += cap.stocks;
				d2 += cap.stocks * cap.price;
			}
		}
		if (d1 == 0)
			return;
		avePrice = d2 / d1;
		price.put(avePrice);
		expePrice = new Expect(price, 1d);
	}

	void print()// 打印信息
	{
		System.out.println(name);
		System.out.println("可生产性：" + producible);
		System.out.println("平均价格：" + avePrice);
		if (producible) {
			System.out.println("设备价格：" + equ_price);
			System.out.println("设备生产率：" + equ_productivity);
			System.out.println("设备损耗：" + equ_loss);
			System.out.println("原材料价格：" + rawMat_price);
			System.out.println("生产时间：" + pro_time);
			System.out.println("所需工人数：" + workers_number);
			System.out.println("价值增值：" + addedValue());
			System.out.println("价值：" + value());
		}
	}

	static int round(double d) {
		return (int) (d + 0.5d);
	}
}
