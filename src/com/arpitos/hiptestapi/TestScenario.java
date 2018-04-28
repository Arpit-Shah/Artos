package com.arpitos.hiptestapi;

public class TestScenario {

	String id;
	String created_at;
	String updated_at;
	String last_author;
	String name;
	String description;
	int folder_id;
	String definition;

	public TestScenario(String id, String created_at, String updated_at, String last_author, String name, String description, int folder_id,
			String definition) {
		super();
		this.id = id;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.last_author = last_author;
		this.name = name;
		this.description = description;
		this.folder_id = folder_id;
		this.definition = definition;
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

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public int getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(int folder_id) {
		this.folder_id = folder_id;
	}
}
