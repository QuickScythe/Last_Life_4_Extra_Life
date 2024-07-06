package me.quickscythe.ll4el.utils.misc;

import java.util.List;

public class PageResult {
	public List<String> pages;
	public int page;
	public int maxPages;

	public PageResult(List<String> pages, int page, int maxPages) {
		this.pages = pages;
		this.page = page;
		this.maxPages = maxPages;
	}
}