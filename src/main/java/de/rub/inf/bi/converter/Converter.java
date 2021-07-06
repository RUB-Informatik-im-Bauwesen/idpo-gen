package de.rub.inf.bi.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Paths;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.util.Context;

import de.rub.inf.bi.misc.RandomString;
import fr.mines_stetienne.ci.sparql_generate.SPARQLExt;
import fr.mines_stetienne.ci.sparql_generate.engine.PlanFactory;
import fr.mines_stetienne.ci.sparql_generate.engine.RootPlan;
import fr.mines_stetienne.ci.sparql_generate.query.SPARQLExtQuery;
import fr.mines_stetienne.ci.sparql_generate.stream.LocationMapperAccept;
import fr.mines_stetienne.ci.sparql_generate.stream.SPARQLExtStreamManager;
import fr.mines_stetienne.ci.sparql_generate.utils.ContextUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


/**
 * Root resource (exposed at "convert" path)
 */
@Path("convert")
public class Converter {
    java.nio.file.Path path = Paths.get(".").toAbsolutePath().normalize();
    String pathQueries = path.toFile().getAbsolutePath() + "/src/main/resources/queries";
    String pathInput = path.toFile().getAbsolutePath() + "/src/main/resources/input";
    String pathOutput = path.toFile().getAbsolutePath() + "/src/main/resources/output";

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces("text/turtle")
    public Response postIt(String bpmnXmlString) {

        System.out.println("Called convert function");
        String transactionString = RandomString.getAlphaNumericString(10);

        String filename = pathInput+"/input_"+transactionString+".xml";
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename);
            fw.write(bpmnXmlString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        String queries[] = new File(pathQueries).list();
        java.nio.file.Path queryPaths[] = new java.nio.file.Path[queries.length];
        System.out.println("Found queries to generate:");
        for(int i=0; i<queries.length; i++) {
            queryPaths[i]=Paths.get(pathQueries+"/"+queries[i]);
        }

        Model overallModel = ModelFactory.createDefaultModel();
        LocationMapperAccept mapper = new LocationMapperAccept();
        mapper.addAltEntry("http://example.org/document#document0.xml", filename);

        SPARQLExtStreamManager sm = SPARQLExtStreamManager.makeStreamManager(mapper);

        Context context = ContextUtils.build()
                .setStreamManager(sm)
                .build();

        for(int i=0; i<queryPaths.length; i++) {
            SPARQLExtQuery query = (SPARQLExtQuery) QueryFactory.read(queryPaths[i].toString(), SPARQLExt.SYNTAX);
            RootPlan plan = PlanFactory.create(query);
            Model model = plan.execGenerate(context);
            overallModel.add(model);
        }



        FileOutputStream stream1 = null;
        try {
            stream1 = new FileOutputStream(pathOutput+"/output_"+transactionString+".ttl");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        overallModel.write(stream1, "TURTLE") ;

        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        overallModel.write(stream2, "TURTLE") ;
        System.out.println("Finished convert function");
        return Response.ok()
                .entity(new String(stream2.toByteArray()),new Annotation[0])
                .build();
    }


}
