package com.arpitos.hiptestapi;

public class TestRun {

	String id;
	String created_at;
	String updated_at;
	String last_author;
	String name;
	String description;
	int passed;
	int failed;
	int retest;
	int undefined;
	int blocked;
	int skipped;
	int wip;

	public TestRun(String id, String created_at, String updated_at, String last_author, String name, String description, int passed, int failed,
			int retest, int undefined, int blocked, int skipped, int wip) {
		super();
		this.id = id;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.last_author = last_author;
		this.name = name;
		this.description = description;
		this.passed = passed;
		this.failed = failed;
		this.retest = retest;
		this.undefined = undefined;
		this.blocked = blocked;
		this.skipped = skipped;
		this.wip = wip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getLast_author() {
		return last_author;
	}

	public void setLast_author(String last_author) {
		this.last_author = last_author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPassed() {
		return passed;
	}

	public void setPassed(int passed) {
		this.passed = passed;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getRetest() {
		return retest;
	}

	public void setRetest(int retest) {
		this.retest = retest;
	}

	public int getUndefined() {
		return undefined;
	}

	public void setUndefined(int undefined) {
		this.undefined = undefined;
	}

	public int getBlocked() {
		return blocked;
	}

	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	public int getSkipped() {
		return skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

	public int getWip() {
		return wip;
	}

	public void setWip(int wip) {
		this.wip = wip;
	}

}
