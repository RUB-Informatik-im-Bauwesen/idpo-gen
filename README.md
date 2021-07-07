# idpo-generation
## Working example
- https://w3id.org/idpo#examples

## Run application
- this application bases on https://github.com/sparql-generate/sparql-generate 
- however, to process BPMN-XML files with CamelCase XML-Nodes, the generation of IDPO or BPMN instances only works with the following fork: https://github.com/philhag/sparql-generate using the raw XML string instead of validating and prettyfing it first
- the webservice can be startet from `Main.java` and listens on `http://localhost:8080/`
- conversion can be startet using `HTTP POST http://localhost:8080/convert` for IDPO and  `HTTP POST http://localhost:8080/convertBPMN` with `application/xml` body content

## Example Query
- Example:  
```
curl --location --request POST 'http://localhost:8080/convert/' 
--header 'Content-Type: application/xml' 
--data-raw '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
	<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exporter="bpmn-js (https://demo.bpmn.io)" exporterVersion="8.2.2" id="sid-38422fae-e03e-43a3-bef4-bd33b32041b2" targetNamespace="http://bpmn.io/bpmn">  
		<collaboration id="Collaboration_XYZ123" >    
			<participant id="Participant_XYZ123" name="User 1" />
        </collaboration>              		
	 	</definitions>' 
```   
    
- Result:  

```
@prefix instances: <http://w3id.org/idpo/instances/> .  
@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .  
@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .  
@prefix bpmn2: <https://www.omg.org/spec/BPMN/2.0#> .  
@prefix ite:   <http://w3id.org/sparql-generate/iter/> .  
@prefix fun:   <http://w3id.org/sparql-generate/fn/> .  

instances:Collaboration_XYZ123  
        a       bpmn2:Collaboration .  
        
instances:Participant_XYZ123  \
        a           bpmn2:Participant ;  
        bpmn2:name  "User 1" .
```
