/**
 * 
 */
package org.peregrino.persistence.impl;

import org.peregrino.persistence.Reference;

/**
 * @author tderflinger
 *
 */
public class ReferenceImpl implements Reference {
	
	private int id;
	
	private int category;
	
	private String header;
	
	private String description;

	/* (non-Javadoc)
	 * @see org.peregrino.derby.test.Reference#getCategory()
	 */
	public int getCategory() {
		return category;
	}

	/* (non-Javadoc)
	 * @see org.peregrino.derby.test.Reference#setCategory(int)
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/* (non-Javadoc)
	 * @see org.peregrino.derby.test.Reference#getHeader()
	 */
	public String getHeader() {
		return header;
	}

	/* (non-Javadoc)
	 * @see org.peregrino.derby.test.Reference#setHeader(java.lang.String)
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/* (non-Javadoc)
	 * @see org.peregrino.derby.test.Reference#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.peregrino.derby.test.Reference#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public ReferenceImpl(int category, String header, String description) {
		super();
		this.category = category;
		this.header = header;
		this.description = description;
	}

	public ReferenceImpl() {
		super();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}
	
	

}
