# Approaches to strengthen scholia.toolforge.org

This repository contains the code used in the different alternatives evaluated to make Scholia more robust. Scholia is a service that creates visual scholarly profiles for topics, people, organizations, species, chemicals, etc using bibliographic and other information in Wikidata. Scholia relies on Wikidata, and Wikidata contains only a limited albeit growing subset of the corpus of scholarly literature, its authors and citations.

WESO is a research group at the University of Oviedo founded in 2004 by Jose Emilio Labra Gayo. As a group, our main research activity involves semantic Web, education and technology transfer. We are a constantly evolving group, commited to the development of reseach proyects that empower the use of semantic technologies and web standards.

Roughly speaking, the alternatives evaluated are:
- Create a local copy with all Wikidata data.
- Create a subset containing only the data used from Wikidata.
- Create a local cache containing the result of the queries used by the system.