package org.mobicents.mgcp.stack.parser;

import java.util.ArrayList;
import java.util.List;

public class UtilsFactory {

	private List<Utils> list = new ArrayList<Utils>();
	private int size = 0;
	private int count = 0;

	public UtilsFactory(int size) {
		this.size = size;
		for (int i = 0; i < size; i++) {
			Utils utils = new Utils();
			list.add(utils);
		}
	}

	public Utils allocate() {
		Utils utils = null;

		if (!list.isEmpty()) {
			utils = list.remove(0);
			return utils;
		}

		utils = new Utils();
		count++;
		System.out.println("Count = "+count);
		return utils;
	}

	public void deallocate(Utils utils) {
		if (list.size() < size && utils != null) {
			list.add(utils);
		}
	}

	public int getSize() {
		return this.size;
	}

	public int getCount() {
		return this.count;
	}

}
