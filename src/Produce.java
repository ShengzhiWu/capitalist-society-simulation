
public class Produce {// 正在进行中的生产

	int pro_quantity;// 产量
	int equ_number;// 占用设备数
	double workers_number;// 占用工人数
	double salary;// 总工资

	int timelength;// 总时长
	int time;// 剩余时间

	Produce next;// 链表下一项

	Produce(int i1, int i2, double d1, double d2, int i3, Produce pr1)// 构造器 产量 设备数 工人数 工人单位时间平均工资 时长 链表下一项
	{
		pro_quantity = i1;
		equ_number = i2;
		workers_number = d1;
		// System.out.println("产量:"+i1);
		// System.out.println("雇佣工人数:"+d1);
		// System.out.println("平均工资:"+d2);
		salary = d1 * d2 * i3;
		time = i3;
		timelength = i3;
		next = pr1;
	}
}
