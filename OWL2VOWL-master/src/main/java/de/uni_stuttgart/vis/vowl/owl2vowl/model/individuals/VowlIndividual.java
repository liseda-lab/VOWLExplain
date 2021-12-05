/*
 * VowlIndividual.java
 *
 */

package de.uni_stuttgart.vis.vowl.owl2vowl.model.individuals;

import de.uni_stuttgart.vis.vowl.owl2vowl.constants.NodeType;
//import de.uni_stuttgart.vis.vowl.owl2vowl.constants.NodeType;
//import de.uni_stuttgart.vis.vowl.owl2vowl.model.AbstractVowlObject;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.AbstractEntity;
//import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.nodes.classes.AbstractClass;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.visitor.VowlElementVisitor;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 */
public class VowlIndividual extends AbstractEntity {
	public VowlIndividual(IRI iri) {
		super(iri, NodeType.TYPE_INDIVIDUAL);
	}

	@Override
	public void accept(VowlElementVisitor visitor) {
		visitor.visit(this);
	}
}

//public class VowlClass extends AbstractClass {
//
//	public VowlClass(IRI iri) {
//		super(iri, NodeType.TYPE_CLASS);
//	}
//
//	@Override
//	public void accept(VowlElementVisitor visitor) {
//		visitor.visit(this);
//	}
//
//}
