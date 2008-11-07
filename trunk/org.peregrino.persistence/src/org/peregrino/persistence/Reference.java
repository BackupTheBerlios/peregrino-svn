package org.peregrino.persistence;

public interface Reference {

	public int getId();
	
	public void setId(int id);
	
	public int getCategory();

	public void setCategory(int category);

	public String getHeader();

	public void setHeader(String header);

	public String getDescription();

	public void setDescription(String description);

}