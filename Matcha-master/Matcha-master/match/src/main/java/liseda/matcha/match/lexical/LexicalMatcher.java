/******************************************************************************
* Copyright 2013-2018 LASIGE                                                  *
*                                                                             *
* Licensed under the Apache License, Version 2.0 (the "License"); you may     *
* not use this file except in compliance with the License. You may obtain a   *
* copy of the License at http://www.apache.org/licenses/LICENSE-2.0           *
*                                                                             *
* Unless required by applicable law or agreed to in writing, software         *
* distributed under the License is distributed on an "AS IS" BASIS,           *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    *
* See the License for the specific language governing permissions and         *
* limitations under the License.                                              *
*                                                                             *
*******************************************************************************
* Matches Ontologies by finding literal full-name matches between their       *
* Lexicons. Weighs matches according to the provenance of the names.          *
*                                                                             *
* @author Daniel Faria                                                        *
******************************************************************************/
package liseda.matcha.match.lexical;

import java.util.HashSet;
import java.util.Set;

import liseda.matcha.alignment.Alignment;
import liseda.matcha.alignment.Mapping;
import liseda.matcha.alignment.MappingRelation;
import liseda.matcha.match.AbstractHashMatcher;
import liseda.matcha.ontology.Ontology;
import liseda.matcha.ontology.lexicon.Lexicon;
import liseda.matcha.semantics.EntityType;
import liseda.matcha.semantics.SemanticMap;
import liseda.matcha.settings.InstanceMatchingCategory;
import liseda.matcha.settings.LanguageSetting;
import liseda.matcha.settings.Settings;

public class LexicalMatcher extends AbstractHashMatcher
{
	
//Attributes
	
	protected static final String DESCRIPTION = "Matches entities that have one or more exact\n" +
											  "String matches between their Lexicon entries";
	protected static final String NAME = "Lexical Matcher";
	protected static final EntityType[] SUPPORT = {EntityType.CLASS,EntityType.INDIVIDUAL,EntityType.DATA_PROP,EntityType.OBJECT_PROP};
		
//Constructors

	public LexicalMatcher()
	{
		description = DESCRIPTION;
		name = NAME;
		support = SUPPORT;
	}
	
//Protected Methods

	@Override
	public Alignment hashMatch(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		//If we have a multi-language Lexicon, we must match language by language
		if(Settings.getInstance().getLanguageSetting().equals(LanguageSetting.MULTI))
			return hashMatchMulti(o1,o2,e,thresh);
		else
			return hashMatchSimple(o1,o2,e,thresh);
	}
	
	public Alignment hashMatchMulti(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		//Initialize the alignment
		Alignment maps = new Alignment(o1,o2);
		if(!checkEntityType(e))
			return maps;
		//Get the lexicons of the source and target Ontologies
		Lexicon sLex = o1.getLexicon(e);
		Lexicon tLex = o2.getLexicon(e);
		Settings set = Settings.getInstance();
		//Get the shared languages
		Set<String> languages = new HashSet<String>(sLex.getLanguages());
		languages.retainAll(tLex.getLanguages());
		
		for(String l : languages)
		{
			//To minimize iterations, we want to iterate through the
			//Ontology with the smallest Lexicon
			boolean sourceIsSmaller = (sLex.languageNameCount(l) <= tLex.languageNameCount(l));
			Set<String> names;
			if(sourceIsSmaller)
				names = sLex.getNamesWithLanguage(l);
			else
				names = tLex.getNamesWithLanguage(l);
			for(String s : names)
			{
				boolean isSmallFormula = sLex.isFormula(s) && s.length() < 10;
				Set<String> sourceIndexes = sLex.getEntities(s,l);
				Set<String> targetIndexes = tLex.getEntities(s,l);
				//If the name doesn't exist in either ontology, skip it
				if(sourceIndexes == null || targetIndexes == null)
					continue;
				//Otherwise, match all indexes
				for(String i : sourceIndexes)
				{
					if(e.equals(EntityType.INDIVIDUAL) && !set.isToMatch(i))
						continue;
					if(isSmallFormula && sLex.containsNonSmallFormula(i))
						continue;
					//Get the weight of the name for the term in the smaller lexicon
					double weight = sLex.getCorrectedWeight(s, i);
					for(String j : targetIndexes)
					{
						if(e.equals(EntityType.INDIVIDUAL) && (!set.isToMatch(j) ||
								(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_CLASSES) &&
								!SemanticMap.getInstance().shareClass(i,j))))
							continue;
						if(isSmallFormula && tLex.containsNonSmallFormula(j))
							continue;
						//Get the weight of the name for the term in the larger lexicon
						double similarity = tLex.getCorrectedWeight(s, j);
						//Then compute the similarity, by multiplying the two weights
						similarity *= weight;
						//If the similarity is above threshold
						if(similarity >= thresh)
							maps.add(new Mapping(i, j, similarity, MappingRelation.EQUIVALENCE));
					}
				}
			}
		}
		return maps;
	}
	
	public Alignment hashMatchSimple(Ontology o1, Ontology o2, EntityType e, double thresh)
	{
		//Initialize the alignment
		Alignment maps = new Alignment(o1,o2);
		if(!checkEntityType(e))
			return maps;
		//Get the lexicons of the source and target Ontologies
		Lexicon sLex = o1.getLexicon(e);
		Lexicon tLex = o2.getLexicon(e);
		Settings set = Settings.getInstance();
		//To minimize iterations, we want to iterate through the
		//Ontology with the smallest Lexicon
		boolean sourceIsSmaller = (sLex.nameCount() <= tLex.nameCount());
		Set<String> names;
		if(sourceIsSmaller)
			names = sLex.getNames();
		else
			names = tLex.getNames();
		for(String s : names)
		{
			//If the name doesn't exist in either ontology, skip it
			if(!sLex.contains(s) || !tLex.contains(s))
				continue;
			boolean isSmallFormula = sLex.isFormula(s) && s.length() < 10;
			Set<String> sourceIndexes = sLex.getEntities(s);
			Set<String> targetIndexes = tLex.getEntities(s);
			//Otherwise, match all indexes
			for(String i : sourceIndexes)
			{
				if(e.equals(EntityType.INDIVIDUAL) && !Settings.getInstance().isToMatch(i))
					continue;
				if(isSmallFormula && sLex.containsNonSmallFormula(i))
					continue;
				//Get the weight of the name for the term in the smaller lexicon
				double weight = sLex.getCorrectedWeight(s, i);
				for(String j : targetIndexes)
				{
					if(e.equals(EntityType.INDIVIDUAL) && (!set.isToMatch(j) ||
							(set.getInstanceMatchingCategory().equals(InstanceMatchingCategory.SAME_CLASSES) &&
							!SemanticMap.getInstance().shareClass(i,j))))
						continue;
					if(isSmallFormula && tLex.containsNonSmallFormula(j))
						continue;
					//Get the weight of the name for the term in the larger lexicon
					double similarity = tLex.getCorrectedWeight(s, j);
					//Then compute the similarity, by multiplying the two weights
					similarity *= weight;
					//If the similarity is above threshold
					if(similarity >= thresh)
						maps.add(new Mapping(i, j, similarity, MappingRelation.EQUIVALENCE));
				}
			}
		}
		return maps;
	}
}