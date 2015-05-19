package com.zznode.opentnms.isearch.routeAlgorithm;

import java.util.LinkedList;

class Point {
	public int x;
	public int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

public class Doorknobs {
	public static int[][] move = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };// 在迷宫中可走的四个方向
	public Point[] point; // 记录迷宫中的o点
	public int[][] dist; // 记录任意两个o点的距离
	public boolean[] used; // 用于DFS时作已访问标记
	public int count; // 记录o点的个数
	public char[][] ch; // 迷宫的描述

	public Doorknobs() // 初始化
	{
		point = new Point[10];
		dist = new int[10][10];
		used = new boolean[10];
		ch = new char[51][51];
		count = 0;
	}

	// BFS求Point(x1,y1)和Point(x2,y2)的最短距离
	public int distance(String[] house, int x1, int y1, int x2, int y2) {
		int row = house.length;
		int col = house[0].length();
		int deep = 0; // 记录最短距离

		for (int i = 0; i < row; i++) // 将String[]转化为char的二维数组
		{
			ch[i] = house[i].toCharArray();
		}

		LinkedList queue = new LinkedList(); // 用LinkedList实现队列
		ch[x1][y1] = '#'; // 置访问标志
		queue.add(new Point(x1, y1));
		queue.add(null); // null为BFS搜索一层的标志
		while (!queue.isEmpty()) {
			Point p;
			while ((p = (Point) queue.poll()) != null) {
				int x = p.x;
				int y = p.y;
				if (x == x2 && y == y2) // 找到
				{
					return deep;
				}
				for (int i = 0; i < 4; i++) {
					int nx = x + move[i][0];
					int ny = y + move[i][1];
					if (nx < 0 || nx >= row || ny < 0 || ny >= col
							|| ch[nx][ny] == '#') {
						continue;
					}
					ch[nx][ny] = '#'; // 置访问标志
					queue.add(new Point(nx, ny)); // 加入队列
				}
			}
			deep++;
			queue.add(null);

		}
		return Integer.MAX_VALUE;
	}

	// 从第start个点开始找left个点的最短距离
	public int dfs(int start, int left) {
		int ret = Integer.MAX_VALUE;
		for (int i = 0; i < count; i++) {
			if (!used[i]) {
				int temp = dist[start][i];
				System.out.println("dist of " + start +"," + i + "::  "+ temp );
				if (left != 1) {
					used[i] = true;
					temp += dfs(i, left - 1);
					used[i] = false;
				}
				if (temp < ret) {
					ret = temp;
				}
			}
		}
		return ret;
	}

	public int shortest(String[] house, int doorknobs) {
		int row = house.length;
		int col = house[0].length();
		int i = 0, j = 0;
		for (i = 0; i < row; i++) {
			for (j = 0; j < col; j++) {
				if (house[i].charAt(j) == 'o') {
					point[count] = new Point(i, j);
					count++;
				}
			}
		}
		point[count] = new Point(0, 0);// 初始点
		count++;

		// dist[i][j]==dist[j][i],可以进行优化一下
		for (i = 0; i < count; i++) {
			for (j = 0; j < count; j++) {
				dist[i][j] = distance(house, point[i].x, point[i].y,
						point[j].x, point[j].y);
				// System.out.println (i+" "+j+" "+dist[i][j]);
			}
		}

		used[count - 1] = true;
		int ret = dfs(count - 1, doorknobs);
		return (ret == Integer.MAX_VALUE) ? -1 : ret;
	}

	public static void main(String args[]) {
		Doorknobs d = new Doorknobs();
		String house[] = { ".....", "o....", "o....", "o....", "...o." };
		System.out.println(d.shortest(house, 4));
	}
}
