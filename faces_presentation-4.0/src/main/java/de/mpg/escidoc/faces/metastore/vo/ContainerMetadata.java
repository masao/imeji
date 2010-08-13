package de.mpg.escidoc.faces.metastore.vo;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/container/")
@RdfType("metadata")
@Embedded
public class ContainerMetadata {
	

	private String title;
	

	private String description;
	
	
	private Collection<Person> person = new LinkedList<Person>();




	@RdfProperty("http://purl.org/dc/elements/1.1/title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@RdfProperty("http://purl.org/dc/elements/1.1/description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@RdfProperty("http://purl.org/escidoc/metadata/terms/0.1/creator")
	public Collection<Person> getPerson() {
		return person;
	}

	public void setPerson(Collection<Person> person) {
		this.person = person;
	}
	
	
	
	


}