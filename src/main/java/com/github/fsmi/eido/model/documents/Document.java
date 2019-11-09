package com.github.fsmi.eido.model.documents;

import com.github.fsmi.eido.util.Guard;

public class Document {

	private int id;
	private Department department;
	private long creationTime;
	private int numberOfPages;
	private SolutionType solutionType;
	private String comment;
	private DocumentType documentType;
	private boolean hasFile;
	private boolean hasBarcode;
	private long validationTime;
	private String submitter;
	private boolean earlyDocumentEligible;
	private boolean depositReturnEligible;

	public Document(int id, Department department, long creationTime, int numberOfPages, SolutionType solutionType,
			String comment, DocumentType documentType, boolean hasFile, boolean hasBarcode, long validationTime,
			String submitter, boolean earlyDocumentEligible, boolean depositReturnEligible) {
		Guard.nullCheck(department, solutionType, documentType, submitter);
		this.id = id;
		this.creationTime = creationTime;
		this.numberOfPages = numberOfPages;
		this.solutionType = solutionType;
		this.comment = comment;
		this.documentType = documentType;
		this.hasFile = hasFile;
		this.hasBarcode = hasBarcode;
		this.validationTime = validationTime;
		this.submitter = submitter;
		this.earlyDocumentEligible = earlyDocumentEligible;
		this.depositReturnEligible = depositReturnEligible;
	}

	/**
	 * @return Can the student get the deposit back he paid buying this document
	 */
	public boolean depositReturnEligible() {
		return depositReturnEligible;
	}

	/**
	 * @return Comments noted for this document, possibly null
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return UNIX time stamp of when the document was created
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * @return Department the document belongs to
	 */
	public Department getDepartment() {
		return department;
	}

	/**
	 * @return type of this document
	 */
	public DocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * @return Internal database id
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return Amount of pages this document has
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * @return Kind of solution that is within the document
	 */
	public SolutionType getSolutionType() {
		return solutionType;
	}

	/**
	 * @return Identifier used by the student who submitted the document
	 */
	public String getSubmitter() {
		return submitter;
	}

	/**
	 * @return UNIX timestamp of when the document was validated. -1 if the document
	 *         was not validated yet
	 */
	public long getValidationTime() {
		return validationTime;
	}

	/**
	 * @return Does the document have a barcode associated with it
	 */
	public boolean hasBarcode() {
		return hasBarcode;
	}
	
	/**
	 * @return Was the document already manually validated by a worker
	 */
	public boolean hasBeenValidated() {
		return validationTime > 0;
	}

	/**
	 * @return Does this document have any special comments
	 */
	public boolean hasComment() {
		return comment != null;
	}

	/**
	 * @return Does the document have a file associated with it
	 */
	public boolean hasFile() {
		return hasFile;
	}

	/**
	 * @return Can the student get an early document reward for submitting this
	 *         document
	 */
	public boolean isEarlyDocumentEligible() {
		return earlyDocumentEligible;
	}

}
