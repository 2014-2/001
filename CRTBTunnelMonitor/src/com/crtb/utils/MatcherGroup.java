package com.crtb.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.*;

public class MatcherGroup {
	private String groupStr;
	private List<MatcherGroup> subGroup;
	private int startIndex;
	private int endIndex;

	public String group() {
		return groupStr;
	}

	public List<MatcherGroup> getSubGroupList() {
		return subGroup;
	}

	public MatcherGroup[] getSubGroupArray() {
		MatcherGroup[] a = null;
		return subGroup.toArray(a);
	}

	/**
	 * @param index start from 0.
	 */
	public MatcherGroup getSubGroup(int index) {
		if (subGroup == null || index < 0 || index >= subGroup.size()) {
			return null;
		}
		return subGroup.get(index);
	}

	public static MatcherGroup toMatcherGroup(Matcher m) {
		if (m == null) {
			return null;
		}

		Stack<MatcherGroup> stack = new Stack<MatcherGroup>();
		for (int i = 0; i <= m.groupCount(); ++i) {
			MatcherGroup mg = new MatcherGroup();
			mg.groupStr = m.group(i);
			mg.startIndex = m.start(i);
			mg.endIndex = m.end(i);
			if (!addGroup(mg, stack)) {
				stack.add(mg);
			}
		}

		if (stack.isEmpty()) {
			return null;
		} else {
			return stack.get(0);
		}
	}

	private static boolean addGroup(MatcherGroup mg, Stack<MatcherGroup> stack) {
		if (stack.isEmpty()) {
			return false;
		}

		MatcherGroup topML = null;
		while (!stack.isEmpty()) {
			topML = stack.peek();
			if (mg.startIndex > topML.endIndex) {
				stack.pop();
			} else {
				// startIndex && endIndex between each group must't cross!!! (eg.: [3,7] <-> [5,8])
				break;
			}
		}

		if (stack.isEmpty()) {
			return false;
		} else {
			if (topML.subGroup == null) {
				topML.subGroup = new ArrayList<MatcherGroup>();
			}
			topML.subGroup.add(mg);
			stack.add(mg);
		}

		return true;
	}
}