package com.example.pc.testslidinglayer;



/**
 * Item definition including the section.
 */
public  class SectionListItem {
	protected String item;
	private String section;//头部内容

	public Object getItem() {
		return item;
	}

	public SectionListItem setItem(String item) {
		this.item = item;
		return this;
	}

	public String getSection() {
		return section;
	}
	public SectionListItem setSection(String section){
		this.section = section;
		return this;
	}
	
}
